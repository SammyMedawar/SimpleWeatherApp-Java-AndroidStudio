package com.example.sammysweatherapp.Models;

public class MyLocation {
    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        MyLocation.longitude = longitude;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        MyLocation.latitude = latitude;
    }

    public static double longitude, latitude;
}
