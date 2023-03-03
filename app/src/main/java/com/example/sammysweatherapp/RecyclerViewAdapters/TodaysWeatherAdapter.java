package com.example.sammysweatherapp.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sammysweatherapp.Models.ThreeHours;
import com.example.sammysweatherapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TodaysWeatherAdapter extends RecyclerView.Adapter<TodaysWeatherAdapter.MyViewHolder>{

    private Context context;
    private int position;
    private ThreeHours[] hours;
    Activity activity;

    public TodaysWeatherAdapter(Activity activity, Context context, ThreeHours[] hours){
        this.activity = activity;
        this.context = context;
        this.hours = hours;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.today_forecast_design, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position){
        this.position = position;
        if(position == getItemCount())
            return;


        holder.tvRVTime.setText(hours[position].getReadableTime());
        holder.tvRVRain.setText(String.valueOf((int)hours[position].getPop()*100));
        double y = hours[position].getPop();
        String x = String.valueOf(hours[position].getPop()*100);
        holder.tvRVTemperature.setText(String.valueOf(Math.round(hours[position].getTemp()-273.15f))+"\u2103");
        String imgUrl = "https://openweathermap.org/img/wn/" + hours[position].getStringIcon() + "@2x.png";
        Glide.with(this.activity)
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.mipmap.ic_launcher)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(holder.ivRVIcon);
        holder.ivRVIconRain.setImageResource(R.drawable.rainicon);

    }

    @Override
    public int getItemCount() {
        return getLength(hours);
    }

    //I made it so that my array has 8 elements, when a day only has 2 forecasts, the other elements
    //will be null, so I used this function which will filter out the null elements
    public static <T> int getLength(T[] array){
        int count = 0;
        for(T x : array)
            if (x != null)
                ++count;
        return count;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRVTime, tvRVRain, tvRVTemperature;
        ImageView ivRVIcon, ivRVIconRain;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRVTime = itemView.findViewById(R.id.tvRVTodayTime);
            tvRVRain = itemView.findViewById(R.id.tvRVTodayRain);
            tvRVTemperature = itemView.findViewById(R.id.tvRVTodayTemperature);
            ivRVIcon = itemView.findViewById(R.id.ivRVTodayIcon);
            ivRVIconRain = itemView.findViewById(R.id.ivRVTodayIconRain);
        }
    }
}


