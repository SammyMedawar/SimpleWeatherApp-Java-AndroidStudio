package com.example.sammysweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sammysweatherapp.Models.FiveDays;
import com.example.sammysweatherapp.Models.MyLocation;
import com.example.sammysweatherapp.Models.OneDay;
import com.example.sammysweatherapp.Models.ThreeHours;
import com.example.sammysweatherapp.RecyclerViewAdapters.FiveDaysWeatherAdapter;
import com.example.sammysweatherapp.RecyclerViewAdapters.TodaysWeatherAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.model.LatLng;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyLocationTaskListener, MyWeatherTaskListener{

    TextView tvTemperature, tvFeels, tvRainPercentage, tvHumidity, tvWindSpeed,tvSunrise, tvSunset, tvDescription;
    ImageView ivWeather;
    EditText etLocation;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    SharedPreferences sharedPref;
    LatLng here;
    FusedLocationProviderClient flsc;
    String mAPIKey="df7c572251d62207e3e8f4e9e0e1664a";
    static double longi, lati;
    static String country, city;
    int menuIndex;
    ProgressBar progressBar;
    ScrollView scrollView;
    RecyclerView rvToday, rvFiveDays;
    TodaysWeatherAdapter todaysWeatherAdapter;
    FiveDaysWeatherAdapter fiveDaysWeatherAdapter;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();
        setListeners();
        getUserLocation();
        createDrawerMenuItems();
        //disable dark mode on user's phone
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    //Initialize Components
    private void initializeComponents(){
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        etLocation = findViewById(R.id.etLocation);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvFeels = findViewById(R.id.tvFeels);
        tvRainPercentage = findViewById(R.id.tvRain);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWindSpeed = findViewById(R.id.tvWind);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvDescription = findViewById(R.id.tvDescription);
        ivWeather=findViewById(R.id.ivWeather);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navMenu);
        rvToday = findViewById(R.id.rvToday);
        rvFiveDays = findViewById(R.id.rvFiveDays);
        menuIndex = 1; //Used for adding menu items, reason it starts with 1 is because index 0 is reserved for "Manage Favorite Locations" item

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.bringToFront();//used for navigation listener
        flsc = LocationServices.getFusedLocationProviderClient(this);
    }

    //Set the listeners for the navigation view and search button
    private void setListeners(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getTitle().equals("Manage Favorite Locations")){
                    Intent intent = new Intent(getApplicationContext(), activity_manage.class);
                    startActivity(intent);
                    activity.finish();
                }
                else{
                    scrollView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    etLocation.setText(item.getTitle());
                    //get data from shared preferences
                    String coordinates = getLocationStringFromPreferences(item.getTitle().toString());
                    String[] latlong =  coordinates.split(",");
                    lati  = Double.parseDouble(latlong[0]);
                    longi = Double.parseDouble(latlong[1]);
                    loadWeatherData();
                    drawerLayout.closeDrawers();
                    return true;
                }
                return false;
            }
        });
        //Give user keyboard a search button
        etLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    scrollView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    getLocationFromSearch();
                    return true;
                }
                return false;
            }
        });
    }


    //This function uses Nominatim API to geocode a location string into LatLng coordinates
    private void getLocationFromSearch(){
        String userInput = etLocation.getText().toString();
        String myURL="https://nominatim.openstreetmap.org/?addressdetails=1&q="+userInput+"&format=json&limit=1";
        new MyLocationTask(this).execute(myURL);
    }

    @Override
    public void onMyLocationTaskPreExecute(){}//Left Empty

    @Override
    public void onMyLocationTaskPostExecute(MyLocation myLocation){
        if(etLocation.getText().equals("")){
            Toast.makeText(this, "The search field is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(myLocation!=null){
            longi = myLocation.getLongitude();
            lati=myLocation.getLatitude();
            String locationString = etLocation.getText().toString();
            String temp = lati + "," + longi;
            if(!isPreferencesExists(locationString)){
                sharedPref.edit().putString(locationString, temp).commit();
                createDrawerMenuItems();
            }
            loadWeatherData();
        }
        else{//If location is not found properly
            Toast.makeText(activity, "An Error has occured! Please input the location name correctly", Toast.LENGTH_SHORT).show();
            getUserLocation();
        }
    }


    //Check if a shared preference already exists
    private boolean isPreferencesExists(String location){
        Map<String, ?> keys = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()){
            if(entry.getKey().equals(location))
                return true;
        }
        return false;
    }

    private String getLocationStringFromPreferences(String title){
        return sharedPref.getString(title, null);
    }

    //Remove all menu items then re add them again
    private void createDrawerMenuItems(){
        for(int i = 1; i < navigationView.getMenu().size(); i++){
            navigationView.getMenu().removeItem(i);
        }
        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry: keys.entrySet()){
            navigationView.getMenu().add(0,menuIndex,0,entry.getKey()); //.add(groupid, id, order, title)
            menuIndex++;
        }
    }

    //get the user's current coordinates (lattitude and longitude)
    public void getUserLocation(){
        int result = 0;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 23);
            getLocationName();
        }//if we didn't have permission before, redo the function
        else{
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                flsc.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                //Get the user location
                                longi = location.getLongitude();
                                lati = location.getLatitude();
                                LatLng userCoordinates = new LatLng(lati, longi);
                                here = userCoordinates; //idk why I've declared so many variables and at this point I'm scared to remove any
                                getLocationName();
                            }
                        });
            }
        }
    }

    //Based on coordinates, get the address of the current location of user
    private void getLocationName(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder= new Geocoder(this, Locale.getDefault());
        try{
            addresses = geocoder.getFromLocation(lati, longi, 1);
            city = addresses.get(0).getLocality();
            country= addresses.get(0).getCountryName();
            String temp = city+", "+country;
            etLocation.setText(temp);
            if(!isPreferencesExists(temp)){
                String coordinates=lati+","+longi;
                sharedPref.edit().putString(temp, coordinates).commit();
                createDrawerMenuItems();
            }
            loadWeatherData();
        }catch(IOException e){
            Toast.makeText(this, "Error! Unable to get location", Toast.LENGTH_SHORT).show();
            city = null;
            country = null;
        }
    }

    //Start Loading weather data from JSON using the openweathermap API
    private void loadWeatherData(){
        String weatherURL = "https://api.openweathermap.org/data/2.5/forecast?lat="+lati+"&lon="+longi+"&appid="+mAPIKey;
        new MyWeatherTask(this).execute(weatherURL);
    }

    @Override
    public void onMyWeatherTaskPreExecute(){};//Left empty

    @Override
    public void onMyWeatherTaskPostExecute(FiveDays fiveDays){
        if(fiveDays!=null){
            OneDay[] days = fiveDays.getDays();
            ThreeHours[] hours = days[0].getHours();
            ThreeHours thisHour = hours[0];

            int temp = Math.round(thisHour.getTemp() - 273.15f);
            tvTemperature.setText(Integer.toString(temp));
            tvFeels.setText("Feels like " + Integer.toString(Math.round(thisHour.getFeels_like()-273.15f)) + "\u00B0");
            temp = Math.round(thisHour.getPop()*100);
            tvRainPercentage.setText(Integer.toString(temp*100)+"% rain");
            tvDescription.setText(toTitleCase(thisHour.getDescription()));
            temp=Math.round(thisHour.getHumidity());
            tvHumidity.setText(Integer.toString(temp)+"% humidity level");
            temp=Math.round(thisHour.getWind_speed()*3.6f);
            tvWindSpeed.setText("Wind speed is "+temp+"km/hr");
            tvSunrise.setText("Sunrise: " + getDTTOStringConversion(fiveDays.getSunrise()));
            tvSunset.setText("Sunset: " + getDTTOStringConversion(fiveDays.getSunset()));
            String imgUrl = "https://openweathermap.org/img/wn/" + thisHour.getStringIcon() + "@2x.png";


            Glide.with(MainActivity.this)
                    .asBitmap()
                    .load(imgUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(ivWeather);

            //Recycler View code for Today's Forecasts
            todaysWeatherAdapter = new TodaysWeatherAdapter(MainActivity.this, this, hours);
            rvToday.setAdapter(todaysWeatherAdapter);
            rvToday.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            //Recycler View code for Five Days Forecasts
            OneDay[] parameter = {days[1], days[2], days[3], days[4]};
            fiveDaysWeatherAdapter = new FiveDaysWeatherAdapter(MainActivity.this, this, parameter);
            rvFiveDays.setAdapter(fiveDaysWeatherAdapter);
            rvFiveDays.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

        }
        else{
            Toast.makeText(this, "fiveDays is null", Toast.LENGTH_SHORT).show();
        }
    }


    //saved for later, will add wind direction
    private String getWindDirection(Float speed){
        if(speed>337.5)
            return "North";
        else if(speed>292.5)
            return "North West";
        else if (speed>247.5)
            return "West";
        else if (speed>202.5)
            return "South West";
        else if (speed>157.5)
            return "South";
        else if (speed>122.5)
            return "South East";
        else if(speed>67.5)
            return "East";
        else if (speed>22.5)
            return "North East";
        return "North";
    }

    //Convert a long to Date format
    private String getDTTOStringConversion(long dt){
        dt = dt * 1000;
        Date time = new java.util.Date(dt);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa ");//aa means am or pm
        return dateFormat.format(time);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }//idk if I can remove this function

    private static String toTitleCase(String givenString) {
        String[] string = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < string.length; i++) {
            sb.append(Character.toUpperCase(string[i].charAt(0))).append(string[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

};

