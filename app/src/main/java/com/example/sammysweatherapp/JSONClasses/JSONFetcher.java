package com.example.sammysweatherapp.JSONClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONFetcher {
    public static String fetchData(String jsonURL){
        String jsonStr = null;

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try{
            URL url = new URL(jsonURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line).append("\n");
            }
            if(stringBuilder.length() != 0){
                jsonStr = stringBuilder.toString();
            }
        }
        catch (IOException ignored){
            //LOL
        }
        finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(bufferedReader != null){
                try{
                    bufferedReader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return jsonStr;
    }
}
