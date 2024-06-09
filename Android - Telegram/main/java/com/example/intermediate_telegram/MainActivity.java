package com.example.intermediate_telegram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends AppCompatActivity {

    String rest;

    private static final String TAG = "FrugalLogs";
    private static final int REQUEST_ENABLE_BT = 1;
    //We will use a Handler to get the BT Connection statys
    public static Handler handler;
    private final static int ERROR_READ = 0; // used in bluetooth handler to identify message update
    BluetoothDevice arduinoBTModule = null;
    String deviceAddress;
    UUID arduinoUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //We declare a default UUID to create the global variable


    //BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    ScheduledExecutorService mySchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getReq = (Button) findViewById(R.id.button);
        EditText direccion = (EditText) findViewById(R.id.direccion);
        String a = "";

        Context context = this;

        TelegramActuator tg = new TelegramActuator();
        GetRest gr = new GetRest();



        getReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            String url = direccion.getText().toString();
            Log.d(TAG, url);
                //Vamos a iniciar el bluetooth aqui, luego solo telegram.



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
                                intent.putExtra("URL", url);
                                //intent.putExtra("VIEW", btReadings);
                                startService(intent);
                            }
                        }
                    }
                }
                Log.d(TAG, "Button Pressed");





                //Ojito, que si descomentas esto vas a obtener el utlimo mensaje del canal xD

                /*MiPeticionRest obj = new MiPeticionRest(res);
                try {
                    rest = obj.execute("GET-UPDATES").get();
                    res.setText(getLastMessage(rest));


                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/


                //Esta madre no sirve xD

                Intent intent = new Intent(context, TelegramListenerService.class);
                intent.putExtra("URL", url);
                startService(intent);

                /*gr.getUpdates(getApplicationContext(), new GetRest.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("PREP", getLastMessage(result));
                    }
                });*/


            }
        });

    }

    public String getLastMessage(String bigBoi){
        JSONObject response = null;
        String result = "";

        try {
            response = new JSONObject(bigBoi);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray updates = response.getJSONArray("result");
            if (updates.length() > 0) {
                JSONObject latestUpdate = updates.getJSONObject(updates.length() - 1);
                JSONObject message = latestUpdate.getJSONObject("channel_post");
                String text = message.getString("text");
                // Manejar el mensaje de texto aquí

                Log.d("message", text);

                //res.setText(text);

                result = text;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


}