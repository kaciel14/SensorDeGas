package com.example.intermediate_telegram;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GetRest {


    String json = null;


    public String getUpdates(Context context, final VolleyCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.telegram.org/bot" + "5991600174:AAEFmDz5vPujebgyoGYW5fzAbd45CNtP_Bs" + "/getUpdates";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
        return json;
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }
}
