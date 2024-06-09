package com.company;

import com.sun.jdi.ThreadReference;
import org.json.*;

import java.io.*;
import java.net.*;


import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {

    public static void main(String[] args) {
        // write your code here

        String value = null;
        String temp = "-1";

        while (true) {

            try {
                value = getValue("https://8c28-187-254-104-115.ngrok.io/samples");

                //Solo si hay un cambio en la deteccion se procesara la informacion y se haara el POST
                if (!value.equals(temp)) {

                    //Procesar el dato y transformarlo a PPM / %
                    value = String.valueOf(Double.parseDouble(value) * 9.5);
                    double porcentaje = Double.parseDouble(value) / 10000;

                    //Hacemos el POST al servidor, el de sendLecture.
                    sendValue("https://8c28-187-254-104-115.ngrok.io/sendLecture", value, String.valueOf(porcentaje));

                    System.out.println(value);
                    temp = value;
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void sendValue(String urlP, String value, String porcentaje) {
        try {
            // Construct manually a JSON object in Java, for testing purposes an object with an object
            JSONObject data = new JSONObject();
            data.put("value", value);
            data.put("porcentaje", porcentaje);

            // URL and parameters for the connection, This particulary returns the information passed
            URL url = new URL(urlP);
            HttpURLConnection httpConnection  = (HttpURLConnection) url.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            // Not required
            // urlConnection.setRequestProperty("Content-Length", String.valueOf(input.getBytes().length));

            // Writes the JSON parsed as string to the connection
            DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
            wr.write(data.toString().getBytes());


            Integer responseCode = httpConnection.getResponseCode();

            BufferedReader bufferedReader;

            // Creates a reader buffer

            //Si el codigo de respuesta está entre 200-299, fue un exito.
            if (responseCode > 199 && responseCode < 300) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            }

            // To receive the response
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();

            // Prints the response
            System.out.println(content.toString());

        } catch (Exception e) {
            System.out.println("Error Message");
            System.out.println(e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }


    public static String extractValue(String value) {

        JSONObject jsonObj = null;
        String valueSingle = null;
        try {
            jsonObj = new JSONObject(value);
            valueSingle = jsonObj.getString("data");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return valueSingle;

    }


    public static String getValue(String urlP) {

        URL url = null;
        String contentFinal = null;

        try {
            url = new URL(urlP);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            //Te regresa un JSON con el valor. {"data":"350"}
            contentFinal = content.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }


        return extractValue(contentFinal);
    }

}
