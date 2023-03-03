package com.example.sammysweatherapp.Models;

public class FiveDays {
    public OneDay[] getDays() {
        return days;
    }

    public void setDays(OneDay[] days) {
        this.days = days;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public OneDay[] days;
    public long sunrise;
    public long sunset;
}
