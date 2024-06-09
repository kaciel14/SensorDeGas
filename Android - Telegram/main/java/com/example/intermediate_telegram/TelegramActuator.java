package com.example.intermediate_telegram;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TelegramActuator {

    public String result;
    static boolean isOn = false;


    public static void setOn(boolean on) {
        isOn = on;
    }

    public static String getLastMessage(String bigBoi){
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

    public  String getUpdates(Context context){


        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://api.telegram.org/bot" + "5991600174:AAEFmDz5vPujebgyoGYW5fzAbd45CNtP_Bs" + "/getUpdates";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta JSON aquí

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

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejar errores de Volley aquí
            }
        });

        queue.add(request);

        return result;

    }


}
