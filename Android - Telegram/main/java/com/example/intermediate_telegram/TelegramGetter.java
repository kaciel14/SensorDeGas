package com.example.intermediate_telegram;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TelegramGetter extends AsyncTask<String, Void, String> {

    private Context context;

    public String result;

    private VolleyResponseListener listener;
    private String error;

    public void MyAsyncTask(Context context, VolleyResponseListener listener) {
        this.context = context;
        this.listener = listener;

    }

    @Override
    protected String doInBackground(String... strings) {


        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://api.telegram.org/bot" + "5991600174:AAEFmDz5vPujebgyoGYW5fzAbd45CNtP_Bs" + "/getUpdates";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta JSON aquí

                        listener.onResponse(response.toString());

                        try {
                            JSONArray updates = response.getJSONArray("result");
                            if (updates.length() > 0) {
                                JSONObject latestUpdate = updates.getJSONObject(updates.length() - 1);
                                JSONObject message = latestUpdate.getJSONObject("channel_post");
                                String text = message.getString("text");
                                // Manejar el mensaje de texto aquí

                                Log.d("message", text);

                                // res.setText(text);



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

                TelegramGetter.this.error = error.toString();
                listener.onError();
            }
        });

        queue.add(request);


        return result;
    }

    public interface VolleyResponseListener {
        void onResponse(String response);
        void onError();
    }

}


