package com.example.intermediate_telegram;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PostToServer {
    String urlGlobal;
    public PostToServer(String url){
        this.urlGlobal = url;
    }

    public void sendJsonPostRequest(String myVar1, Context context, String url){
            try {

                // Make new json object and put params in it
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("value", myVar1);


                //VERIFICACION DEL VALOR....


                // Building a request
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        // Using a variable for the domain is great for testing
                        String.format(urlGlobal),
                        jsonParams,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    Log.d("Response", response.getString("msg"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }


                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                // Handle the error

                            }
                        });

                /*

                  For the sake of the example I've called newRequestQueue(getApplicationContext()) here
                  but the recommended way is to create a singleton that will handle this.

                  Read more at : https://developer.android.com/training/volley/requestqueue

                  Category -> Use a singleton pattern

                */
                Volley.newRequestQueue(context).add(request);


            } catch (JSONException ex) {
                // Catch if something went wrong with the params
            }


    }

    public void sendJsonPostRequestOnOff(String myVar1, Context context){


        try {

            // Make new json object and put params in it
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("msj", myVar1);


            //VERIFICACION DEL VALOR....


            // Building a request
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    // Using a variable for the domain is great for testing
                    String.format(urlGlobal),
                    jsonParams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                Log.d("Response", response.getString("message"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Handle the error

                        }
                    });

                /*

                  For the sake of the example I've called newRequestQueue(getApplicationContext()) here
                  but the recommended way is to create a singleton that will handle this.

                  Read more at : https://developer.android.com/training/volley/requestqueue

                  Category -> Use a singleton pattern

                */
            Volley.newRequestQueue(context).add(request);


        } catch (JSONException ex) {
            // Catch if something went wrong with the params
        }


    }

    public Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


    public void postRegistrarDispositivoEnServidor(String url, Context context, String value){


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("value",value);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }


        };

        queue.add(sr);

    }






}