package com.example.intermediate_telegram;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramListenerService extends Service {



    private static final String TAG = "FrugalLogs";
    private static final int REQUEST_ENABLE_BT = 1;
    //We will use a Handler to get the BT Connection statys
    public static Handler handler;
    private final static int ERROR_READ = 0; // used in bluetooth handler to identify message update
    BluetoothDevice arduinoBTModule = null;
    String deviceAddress;
    UUID arduinoUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //We declare a default UUID to create the global variable
    String url;

    //BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    ScheduledExecutorService mySchedule;
    PostToServer posting;
    MiPeticionRest obj;
    GetRest gr;
    String lastMessage = "";

    /*public TelegramListenerService() {
        super("SERVICIO");

        obj = new MiPeticionRest();
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        // Service initialization code goes here
        obj = new MiPeticionRest();
        gr = new GetRest();

        //obj.execute("GET-UPDATES");
    }


    private void startHandler() {
        // Crea un Handler y llama a este método de nuevo después de 5 segundos
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                //obj.execute("GET-UPDATES");

                gr.getUpdates(getApplicationContext(), new GetRest.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //Get into here.

                        //Extraemos el ulitmo mensaje para la comprobacion
                        lastMessage = TelegramActuator.getLastMessage(result);

                        if(lastMessage.equals("ENCENDIDO") && !TelegramActuator.isOn){
                            Log.d("BTSERV", "Servicio Encendido");

                            TelegramActuator.setOn(true);
                            //Log.d("After", String.valueOf(TelegramActuator.isOn));

                            //obj = new MiPeticionRest();
                            //obj.execute("GET-SEND", "Deteccion encendida");

                            posting.sendJsonPostRequestOnOff("on", getApplicationContext());

                            //A partir de aquí, se tiene que iniciar el pinche servicio,
                            // no mms que weba.


                            //Check if the phone supports BT
                           /* if (bluetoothAdapter == null) {
                                // Device doesn't support Bluetooth
                                Log.d(TAG, "Device doesn't support Bluetooth");
                            } else {
                                Log.d(TAG, "Device support Bluetooth");
                                //Check BT enabled. If disabled, we ask the user to enable BT
                                if (!bluetoothAdapter.isEnabled()) {
                                    Log.d(TAG, "Bluetooth is disabled");
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        Log.d(TAG, "We don't BT Permissions");
                                        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                        Log.d(TAG, "Bluetooth is enabled now");
                                    } else {
                                        Log.d(TAG, "We have BT Permissions");
                                        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                        Log.d(TAG, "Bluetooth is enabled now");
                                    }

                                } else {
                                    Log.d(TAG, "Bluetooth is enabled");
                                }
                                String btDevicesString="";
                                Set< BluetoothDevice > pairedDevices = bluetoothAdapter.getBondedDevices();

                                if (pairedDevices.size() > 0) {
                                    // There are paired devices. Get the name and address of each paired device.
                                    for (BluetoothDevice device: pairedDevices) {
                                        String deviceName = device.getName();
                                        String deviceHardwareAddress = device.getAddress(); // MAC address
                                        Log.d(TAG, "deviceName:" + deviceName);
                                        Log.d(TAG, "deviceHardwareAddress:" + deviceHardwareAddress);
                                        //We append all devices to a String that we will display in the UI
                                        btDevicesString=btDevicesString+deviceName+" || "+deviceHardwareAddress+"\n";
                                        //If we find the HC 05 device (the Arduino BT module)
                                        //We assign the device value to the Global variable BluetoothDevice
                                        //We enable the button "Connect to HC 05 device"
                                        if (deviceName.equals("HC-05")) {
                                            Log.d(TAG, "HC-05 found");
                                            arduinoUUID = device.getUuids()[0].getUuid();
                                            arduinoBTModule = device;
                                            //HC -05 Found, enabling the button to read results
                                            deviceAddress = device.getAddress();
                                            Intent intent = new Intent(getApplicationContext(), ReadService.class);
                                            intent.putExtra("DEVICE", deviceAddress);
                                            intent.putExtra("UUID", arduinoUUID);
                                            //intent.putExtra("VIEW", btReadings);
                                            startService(intent);
                                        }
                                    }
                                }
                            }
                            Log.d(TAG, "Button Pressed");*/


                        }
                        else if(lastMessage.equals("APAGADO") && TelegramActuator.isOn) {
                            //Log.d("Bluetooth Service", "Servicio Apagado");
                            TelegramActuator.setOn(false);

                            posting.sendJsonPostRequestOnOff("off", getApplicationContext());

                        }


                    }
                });


                /*try {
                    //Extraemos el ulitmo mensaje para la comprobacion
                    lastMessage = TelegramActuator.getLastMessage(obj.get());

                    if(lastMessage.equals("ENCENDIDO") && !TelegramActuator.isOn){
                        Log.d("Bluetooth Service", "Servicio Encendido");

                        TelegramActuator.setOn(true);
                        //Log.d("After", String.valueOf(TelegramActuator.isOn));

                        obj = new MiPeticionRest();
                        obj.execute("GET-SEND", "Deteccion encendida");

                        //A partir de aquí, se tiene que iniciar el pinche servicio,
                        // no mms que weba.


                        //Check if the phone supports BT
                        if (bluetoothAdapter == null) {
                            // Device doesn't support Bluetooth
                            Log.d(TAG, "Device doesn't support Bluetooth");
                        } else {
                            Log.d(TAG, "Device support Bluetooth");
                            //Check BT enabled. If disabled, we ask the user to enable BT
                            if (!bluetoothAdapter.isEnabled()) {
                                Log.d(TAG, "Bluetooth is disabled");
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    Log.d(TAG, "We don't BT Permissions");
                                    //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                    Log.d(TAG, "Bluetooth is enabled now");
                                } else {
                                    Log.d(TAG, "We have BT Permissions");
                                    //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                    Log.d(TAG, "Bluetooth is enabled now");
                                }

                            } else {
                                Log.d(TAG, "Bluetooth is enabled");
                            }
                            String btDevicesString="";
                            Set< BluetoothDevice > pairedDevices = bluetoothAdapter.getBondedDevices();

                            if (pairedDevices.size() > 0) {
                                // There are paired devices. Get the name and address of each paired device.
                                for (BluetoothDevice device: pairedDevices) {
                                    String deviceName = device.getName();
                                    String deviceHardwareAddress = device.getAddress(); // MAC address
                                    Log.d(TAG, "deviceName:" + deviceName);
                                    Log.d(TAG, "deviceHardwareAddress:" + deviceHardwareAddress);
                                    //We append all devices to a String that we will display in the UI
                                    btDevicesString=btDevicesString+deviceName+" || "+deviceHardwareAddress+"\n";
                                    //If we find the HC 05 device (the Arduino BT module)
                                    //We assign the device value to the Global variable BluetoothDevice
                                    //We enable the button "Connect to HC 05 device"
                                    if (deviceName.equals("HC-05")) {
                                        Log.d(TAG, "HC-05 found");
                                        arduinoUUID = device.getUuids()[0].getUuid();
                                        arduinoBTModule = device;
                                        //HC -05 Found, enabling the button to read results
                                        deviceAddress = device.getAddress();
                                        Intent intent = new Intent(getApplicationContext(), ReadService.class);
                                        intent.putExtra("DEVICE", deviceAddress);
                                        intent.putExtra("UUID", arduinoUUID);
                                        //intent.putExtra("VIEW", btReadings);
                                        startService(intent);
                                    }
                                }
                            }
                        }
                        Log.d(TAG, "Button Pressed");


                    }
                    else if(lastMessage.equals("APAGADO") && TelegramActuator.isOn) {
                        //Log.d("Bluetooth Service", "Servicio Apagado");
                        TelegramActuator.setOn(false);

                        obj = new MiPeticionRest();
                        obj.execute("GET-SEND", "Deteccion apagada");

                        //Se apaga el servicio
                        Intent intent = new Intent(getApplicationContext(), ReadService.class);
                        stopService(intent);

                    }


                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/


                //obj = new MiPeticionRest();

                Log.d("test_tag", "Ejecutando código constantemente...");


                // ...

                // Vuelve a llamar a este método después de 5 segundos
                handler.postDelayed(this, 5000);
            }
        }, 5000); // el segundo parámetro es el tiempo de espera antes de que se ejecute el código por primera vez
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service logic goes here

        url = intent.getStringExtra("URL");
        posting = new PostToServer(url+"/sendUserCommand");


        Log.d("test_tag", "Iniciado correctamente");

        startHandler();
        //conectionHandler();
        //obj.execute("GET-UPDATES");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Service cleanup code goes here
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*@Override
    protected void onHandleIntent(@Nullable Intent intent) {
        conectionHandler();
    }*/

}
