package com.example.intermediate_telegram;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ReadService extends Service {
    private static final String TAG = "FrugalLogs";
    BluetoothDevice arduinoBTModule = null;
    String deviceAddress;

    Service m_service;

    BluetoothAdapter adapter;
    public static Handler handler;
    private final static int ERROR_READ = 0; // used in bluetooth handler to identify message update
    //TextView btReadings;
    PostToServer posting;
    UUID arduinoUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //We declare a default UUID to create the global variable;
    String url;

    Observable<String> connectToBTObservable;

    @Override
    public void onCreate() {
        super.onCreate();

        adapter = BluetoothAdapter.getDefaultAdapter();

        //url = "https://5e18-187-254-104-115.ngrok-free.app/updateValue";
        //posting = new PostToServer();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    case ERROR_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        Log.d(TAG, arduinoMsg);
                        //btReadings.setText(arduinoMsg);
                        break;
                }
            }
        };

        // Create an Observable from RxAndroid
        //The code will be executed when an Observer subscribes to the the Observable
        connectToBTObservable = Observable.create(emitter -> {
            Log.d(TAG, "Calling connectThread class");
            //Call the constructor of the ConnectThread class
            //Passing the Arguments: an Object that represents the BT device,
            // the UUID and then the handler to update the UI
            ConnectThread connectThread = new ConnectThread(arduinoBTModule, arduinoUUID, handler);
            connectThread.run();
            //Check if Socket connected
            if (connectThread.getMmSocket().isConnected()) {
                Log.d(TAG, "Calling ConnectedThread class");
                //The pass the Open socket as arguments to call the constructor of ConnectedThread
                ConnectedThread connectedThread = new ConnectedThread(connectThread.getMmSocket());
                connectedThread.run();
                if (connectedThread.getValueRead() != null) {
                    // If we have read a value from the Arduino
                    // we call the onNext() function
                    //This value will be observed by the observer
                    emitter.onNext(connectedThread.getValueRead());
                }
                //We just want to stream 1 value, so we close the BT stream
                connectedThread.cancel();
            }
            // SystemClock.sleep(5000); // simulate delay
            //Then we close the socket connection
            connectThread.cancel();
            //We could Override the onComplete function
            emitter.onComplete();

        });


    }


    public void getAndConnect() {

        connectToBTObservable = Observable.create(emitter -> {
            Log.d(TAG, "Calling connectThread class");
            //Call the constructor of the ConnectThread class
            //Passing the Arguments: an Object that represents the BT device,
            // the UUID and then the handler to update the UI
            ConnectThread connectThread = new ConnectThread(arduinoBTModule, arduinoUUID, handler);
            connectThread.run();
            //Check if Socket connected
            if (connectThread.getMmSocket().isConnected()) {
                Log.d(TAG, "Calling ConnectedThread class");
                //The pass the Open socket as arguments to call the constructor of ConnectedThread
                ConnectedThread connectedThread = new ConnectedThread(connectThread.getMmSocket());
                connectedThread.run();
                if (connectedThread.getValueRead() != null) {
                    // If we have read a value from the Arduino
                    // we call the onNext() function
                    //This value will be observed by the observer
                    emitter.onNext(connectedThread.getValueRead());
                }
                //We just want to stream 1 value, so we close the BT stream
                connectedThread.cancel();
            }
            // SystemClock.sleep(5000); // simulate delay
            //Then we close the socket connection
            connectThread.cancel();
            //We could Override the onComplete function
            emitter.onComplete();

        });


    }

    ScheduledExecutorService mySchedule;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        url = intent.getStringExtra("URL");
        posting = new PostToServer(url+"/updateValue");

        Context myContext = this.getApplicationContext();
        mySchedule = Executors.newScheduledThreadPool(1);
        mySchedule.scheduleAtFixedRate(new Runnable() {

            @SuppressLint("CheckResult")
            @Override
            public void run() {

                getAndConnect();

                deviceAddress = intent.getStringExtra("DEVICE");
                arduinoBTModule = adapter.getRemoteDevice(deviceAddress);

                arduinoUUID = (UUID) intent.getSerializableExtra("UUID");


                if (arduinoBTModule != null) {
                    //We subscribe to the observable until the onComplete() is called
                    //We also define control the thread management with
                    // subscribeOn:  the thread in which you want to execute the action
                    // observeOn: the thread in which you want to get the response
                    connectToBTObservable.
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribeOn(Schedulers.io()).
                            subscribe(valueRead -> {
                                //valueRead returned by the onNext() from the Observable
                                Log.d(TAG, valueRead);


                                //Men, aquí se hace la petición REST al server para mandarlo xD
                                posting.sendJsonPostRequest(valueRead, myContext, url);
                                //We just scratched the surface with RxAndroid


                            });
                }


            }
        }, 1, 7, TimeUnit.SECONDS);


        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}