package com.example.sammysweatherapp.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreeHours {
    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(float feels_like) {
        this.feels_like = feels_like;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStringIcon() {
        return stringIcon;
    }

    public void setStringIcon(String stringIcon) {
        this.stringIcon = stringIcon;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(float wind_speed) {
        this.wind_speed = wind_speed;
    }

    public float getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(float wind_direction) {
        this.wind_direction = wind_direction;
    }

    public float getPop() {
        return pop;
    }

    public void setPop(float pop) {
        this.pop = pop;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            this.readableTime = new SimpleDateFormat("hh:mm aa ").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
    }

    public String getReadableTime(){
        return readableTime;
    }

    public long dt;
    public float temp;
    public float feels_like;

    public float getHumidity() {
        return Humidity;
    }

    public void setHumidity(float humidity) {
        Humidity = humidity;
    }

    public float Humidity;
    public String main;
    public String description;
    public String stringIcon;
    public float wind_speed;
    public float wind_direction;
    public float pop;
    public String sys;
    public String dt_txt;
    public Date date;
    public String time, readableTime;
}
