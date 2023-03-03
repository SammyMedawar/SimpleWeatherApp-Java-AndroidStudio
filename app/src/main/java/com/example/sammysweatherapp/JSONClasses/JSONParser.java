package com.example.sammysweatherapp.JSONClasses;

import com.example.sammysweatherapp.Models.FiveDays;
import com.example.sammysweatherapp.Models.MyLocation;
import com.example.sammysweatherapp.Models.OneDay;
import com.example.sammysweatherapp.Models.ThreeHours;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JSONParser {

    public static MyLocation getLocation(String jsonStr){
        try{
            JSONArray rootJsonArray = new JSONArray(jsonStr);
            MyLocation searchLocation = new MyLocation();
            JSONObject locationJSON = rootJsonArray.getJSONObject(0);
            searchLocation.setLatitude(locationJSON.getDouble("lat"));
            searchLocation.setLongitude(locationJSON.getDouble("lon"));
            return searchLocation;

        }catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static FiveDays getMyWeather(String jsonStr){

        try{
            JSONObject rootJSONObject = new JSONObject(jsonStr);

            FiveDays forecasts = new FiveDays();
            OneDay[] days = new OneDay[5];
            ThreeHours[] hours = new ThreeHours[8];
            JSONArray mainList = rootJSONObject.getJSONArray("list");
            int jIncrement = 0;

            for(int i = 0; i < 5; i++){//Create Five days in total
                OneDay temp = new OneDay();
                for(int j = 0; j < 8; j++){//Create One day in total
                    ThreeHours tempHour = new ThreeHours();
                    JSONObject tempHourJSON = mainList.getJSONObject(jIncrement);
                    tempHour.setDt((long)tempHourJSON.getLong("dt"));
                    JSONObject main = tempHourJSON.getJSONObject("main");
                    tempHour.setTemp((float)main.getDouble("temp"));
                    tempHour.setFeels_like((float)main.getDouble("feels_like"));
                    tempHour.setHumidity((float) main.getDouble("humidity"));
                    JSONArray weather = tempHourJSON.getJSONArray("weather");
                    JSONObject weatherObject = weather.getJSONObject(0);
                    tempHour.setMain((String)weatherObject.getString("main"));
                    tempHour.setDescription((String)weatherObject.getString(("description")));
                    tempHour.setStringIcon((String)weatherObject.getString("icon"));
                    JSONObject wind = tempHourJSON.getJSONObject("wind");
                    tempHour.setWind_speed((float)wind.getDouble("speed"));
                    tempHour.setWind_direction((float)wind.getDouble("deg"));
                    tempHour.setPop((float)tempHourJSON.getDouble("pop"));
                    JSONObject sys = tempHourJSON.getJSONObject("sys");
                    tempHour.setSys((String)sys.getString("pod"));
                    tempHour.setDt_txt((String)tempHourJSON.getString("dt_txt"));
                    try{
                        tempHour.setDate(getDate(tempHour.getDt_txt()));
                        tempHour.setTime(getTime(tempHour.getDt_txt()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    jIncrement++;
                    hours[j] = tempHour;
                    String temp2 = tempHour.getTime();
                    if(temp2.equals("21:00:00")){
                        temp.setHours(hours);
                        hours = new ThreeHours[8];
                        break;
                    }
                }
                days[i] = temp;
            }

            forecasts.setDays(days);
            JSONObject cityJSONObject = rootJSONObject.getJSONObject("city");
            forecasts.setSunrise((long)cityJSONObject.getLong("sunrise"));
            forecasts.setSunset((long)cityJSONObject.getLong("sunset"));
            return forecasts;
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    private static Date getDate(String dt_txt) throws ParseException {
        String temp="";
        for (int i = 0; i < dt_txt.length(); i++){
            char c = dt_txt.charAt(i);
            if(Character.compare(c, ' ') == 0){//if c = ' '
                break;
            }
            else {
                temp = temp + c;
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = dateFormat.parse(temp);
        return d;
    }

    private static String getTime(String dt_txt) throws ParseException {
        String temp="";
        for (int i = 0; i < dt_txt.length(); i++){
            char c = dt_txt.charAt(i);
            if(Character.compare(c, ' ') == 0){
                temp = dt_txt.substring(i+1);
                break;
            }
        }
        return temp;
    }
}
