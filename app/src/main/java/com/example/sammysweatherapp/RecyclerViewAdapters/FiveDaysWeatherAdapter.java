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
import com.example.sammysweatherapp.Models.OneDay;
import com.example.sammysweatherapp.Models.ThreeHours;
import com.example.sammysweatherapp.R;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FiveDaysWeatherAdapter extends RecyclerView.Adapter<FiveDaysWeatherAdapter.MyViewHolder>{

    private Context context;
    private int position;
    private OneDay[] days;
    Activity activity;

    public FiveDaysWeatherAdapter(Activity activity, Context context, OneDay[] days){
        this.activity = activity;
        this.context = context;
        this.days = days;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.five_days_forecast_design, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position){
        this.position = position;
        if(position == getItemCount()) {
            return;
        }

        OneDay dayI = days[position];
        ThreeHours allHours[], morningHour, nightHour;
        allHours = dayI.getHours();
        morningHour = allHours[3];
        nightHour = allHours[7];
        holder.tvRVDate.setText(getDayName(morningHour.getDate()));
        holder.tvRVRain.setText(String.valueOf((int)Math.round(morningHour.getPop()*100)));
        holder.tvRVTemperatureDay.setText(String.valueOf((int)Math.round(morningHour.getTemp()-273.15f))+"\u2103");
        holder.tvRVTemperatureNight.setText(String.valueOf((int)Math.round(nightHour.getTemp()-273.15f))+"\u2103");
        String imgUrl = "https://openweathermap.org/img/wn/" + morningHour.getStringIcon() + "@2x.png";
        Glide.with(this.activity)
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.mipmap.ic_launcher)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(holder.ivRVIconDay);
        imgUrl = "https://openweathermap.org/img/wn/" + nightHour.getStringIcon() + "@2x.png";
        Glide.with(this.activity)
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.mipmap.ic_launcher)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(holder.ivRVIconNight);
        holder.ivRVIconRain.setImageResource(R.drawable.rainicon);
        holder.ivRVIconRain.setImageResource(R.drawable.rainicon);

    }

    @Override
    public int getItemCount(){
        return getLength(days);
    }

    public static<T> int getLength(T[] array){
        int count = 0;
        for(T x : array)
            if(x!=null)
                ++count;
        return count;
    }

    //Date is saved as Fri Mar 03 12:00:00, I want to convert that to a normal day like "Friday"
    public String getDayName(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvRVDate, tvRVRain, tvRVTemperatureDay, tvRVTemperatureNight;
        ImageView ivRVIconDay, ivRVIconNight, ivRVIconRain;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            tvRVDate = itemView.findViewById(R.id.tvRVTodayTime);
            tvRVRain = itemView.findViewById(R.id.tvRVTodayRain);
            tvRVTemperatureDay = itemView.findViewById(R.id.tvRVFiveDaysDayTemperature);
            tvRVTemperatureNight = itemView.findViewById(R.id.tvRVFiveDaysNightTemperature);
            ivRVIconDay = itemView.findViewById(R.id.ivRVFiveDaysDayIcon);
            ivRVIconNight = itemView.findViewById(R.id.ivRVFiveDaysNightIcon);
            ivRVIconRain = itemView.findViewById(R.id.ivRVTodayIconRain);
        }
    }
}
