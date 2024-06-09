package com.example.intermediate_telegram;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MiPeticionRest extends AsyncTask<String,String,String> {
    private TextView output;

    HttpURLConnection urlConnection;
    StringBuilder json;
    String datos = "";


    /*MiPeticionRest(TextView output){
        this.output = output;
    }*/

    @Override
    public void onPreExecute(){
    }


    //5991600174:AAEFmDz5vPujebgyoGYW5fzAbd45CNtP_Bs

    @Override
    protected String doInBackground(String... info) {
        String res = "";

        try
        {

            if( info[0].contains("POST")) {
                URL url = new URL("https://api.telegram.org/bot1520871088:AAFnfam8bknFAdhoiDryOguyb-0qz_85B2w/sendMessage?text=Hola");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                String input = "{\"id\": \"0\",\"nota\": \"" + info[0] + "\",\"fecha\": \"" + info[1] + "\",\"user\":" + info[2] + "}";
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {

                }

                conn.disconnect();
            }

            if( info[0].contains("GET-SEND")){
                URL url = new URL("https://api.telegram.org/bot5991600174:AAEFmDz5vPujebgyoGYW5fzAbd45CNtP_Bs/sendMessage?chat_id=-1001873100910&text=" + info[1]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-length", "0");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setConnectTimeout(1000);
                conn.setReadTimeout(1000);
                conn.connect();

                int status = conn.getResponseCode();

                if ( status == 200 ) {
                    res = "Message Send as  BOT";
                }

                conn.disconnect();
            }

            if( info[0].contains("GET-UPDATES")){
                URL url = new URL("https://api.telegram.org/bot" + "5991600174:AAEFmDz5vPujebgyoGYW5fzAbd45CNtP_Bs" + "/getUpdates");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-length", "0");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setConnectTimeout(1000);
                conn.setReadTimeout(1000);
                conn.connect();

                int status = conn.getResponseCode();

                if ( status == 200 ) {
                    //InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
                    InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(reader);

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    res = sb.toString();

                }

                conn.disconnect();
            }
        }
        catch (MalformedURLException e) {
            Log.e("ENVIOREST", "[MalformedURLException]=>" + e.getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            Log.e("ENVIOREST", "[IOException]=>" + e.getMessage());
            e.printStackTrace();
        }

        return res;
    }

    @Override
    protected void onProgressUpdate(String... progress){

    }

    @Override
    protected void onPostExecute(String result) {

        /*JSONObject response = null;

        try {
            response = new JSONObject(result);
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

                //Log.d("message", text);

                //res.setText(text);

                result = text;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*Log.d("Before", String.valueOf(TelegramActuator.isOn));

        Log.d("Returned_From_Rest", result);

        if(result.equals("ENCENDIDO") && !TelegramActuator.isOn){
            Log.d("Bluetooth Service", "Servicio Encendido");

            TelegramActuator.setOn(true);
            Log.d("After", String.valueOf(TelegramActuator.isOn));

        }
        else if(result.equals("APAGADO") && TelegramActuator.isOn){
            Log.d("Bluetooth Service", "Servicio Apagado");
            TelegramActuator.setOn(false);

        }*/


    }
}
