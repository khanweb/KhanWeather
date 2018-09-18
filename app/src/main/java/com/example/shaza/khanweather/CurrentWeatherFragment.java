package com.example.shaza.khanweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class CurrentWeatherFragment extends Fragment {

    String id ="current";
    String units = "imperial";
    double longitude;
    double latitude;
    TextView conditions;
    TextView location;
    TextView temp;
    ListView listView;
    RelativeLayout relay;
    int tempCount = 0;
    ForcastAdapter dAdapter;
    ArrayList<Day> days = new ArrayList<>();
    String[] strDays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday"};
    Calendar calendar = Calendar.getInstance();
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

    public static CurrentWeatherFragment newInstance(double lat, double lon) {
        CurrentWeatherFragment fragmentFirst = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        args.putDouble("long",lon);
        args.putDouble("lat",lat);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.current_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.tempChange:
                if(units.equals("imperial"))
                    units ="metric";
                else
                    units ="imperial";
                Toast.makeText(getActivity(), "Changing Temps", Toast.LENGTH_SHORT).show();
                refresh();
                return true;
            case R.id.update:
                refresh();
                Toast.makeText(getContext(), "Location Updated", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        longitude = getArguments().getDouble("long");

        latitude = getArguments().getDouble("lat");



    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharedPreferences prefs1 = getContext().getSharedPreferences(id, MODE_PRIVATE);
        final String unitString = prefs1.getString(id,null);

        if (unitString != null) units = unitString;

        SharedPreferences.Editor editor = getContext().getSharedPreferences(id, MODE_PRIVATE).edit();
        editor.putString(id, units);
        editor.apply();

        final View v = inflater.inflate(R.layout.current_weather_frag, container, false);
        setHasOptionsMenu(true);

        for (int i = 0; i < 5; i++) {
            days.add(new Day());
        }
        conditions = v.findViewById(R.id.condition);
        location = v.findViewById(R.id.loc_name);
        temp = v.findViewById(R.id.temperature);
        relay = (RelativeLayout) v.findViewById(R.id.rel_back);
        listView = (ListView) v.findViewById(R.id.listy);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherInterface api = retrofit.create(WeatherInterface.class);



        if(MainActivity.locPerm == true){
            try {
                Call<WeatherData> call = api.getLocalWeather(latitude,longitude,units);
                Call<ForcastData> call2 = api.getCurrentForcast(latitude,longitude,units);
                call.enqueue(new Callback<WeatherData>() {
                    @Override
                    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {

                        if (response.body() == null) {
                            location.setText("PLEASE ENTER A VALID CITY");
                        }
                        else {
                            location.setText(response.body().getName());
                            //Log.d("temp",response.toString());
                            temp.setText(String.valueOf(Math.round(response.body().getMain().getTemp())) + "°");
                            conditions.setText(response.body().getWeather().get(0).getMain());
                            String weath = response.body().getWeather().get(0).getMain();
                            if (weath.equals("Clouds")) relay.setBackgroundResource(R.drawable.clouds);
                            if (weath.equals("Rain")) relay.setBackgroundResource(R.drawable.rain);
                            if (weath.equals("Mist")) relay.setBackgroundResource(R.drawable.mist);
                            if (weath.equals("Fog")) relay.setBackgroundResource(R.drawable.fog);
                            if (weath.equals("Clear")) relay.setBackgroundResource(R.drawable.clear);
                            if (weath.equals("Drizzle")) relay.setBackgroundResource(R.drawable.drizzle);
                            if (weath.equals("Smoke")) relay.setBackgroundResource(R.drawable.smoke);
//
                        }


                    }

                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {

                    }
                });


                call2.enqueue(new Callback<ForcastData>() {
                    @Override
                    public void onResponse(Call<ForcastData> call, Response<ForcastData> response) {
                        if (response.body() == null) {
                            location.setText("PLEASE ENTER A VALID CITY");
                        } else {
                            tempCount = 0;
                            for (int i = 0; i < 5; i++) {
                                days.get(i).setDayOfWeek(strDays[(dayOfWeek + i) % 7]);
                                days.get(i).setTemp(Math.round(response.body().getList().get(tempCount).getMain().getTemp()));
                                days.get(i).setCondition(response.body().getList().get(tempCount).getWeather().get(0).getMain());
                                tempCount+=7;
                            }


                            dAdapter = new ForcastAdapter(getContext(), days);

                            listView.setAdapter(dAdapter);

                            Log.d("message",String.valueOf(response.body().getList().size()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ForcastData> call, Throwable t) {

                    }
                });
            }
            catch (Exception e){
                location.setText("PLEASE ENTER A VALID CITY");
            }
            }

            else{

                Log.d("permissions","False");
        }
        return v;
    }

    public void refresh() {
        if(MainActivity.locPerm == false){
            Toast.makeText(getContext(),"Please Restart app and Accept Permissions",Toast.LENGTH_LONG).show();
            return;
        }
        tempCount = 0;

        SharedPreferences.Editor editor = getContext().getSharedPreferences(id, MODE_PRIVATE).edit();
        editor.putString(id, units);
        editor.apply();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final WeatherInterface api = retrofit.create(WeatherInterface.class);

        Call<WeatherData> call = api.getLocalWeather(MainActivity.latitude,MainActivity.longitude,units);
        Call<ForcastData> call2 = api.getCurrentForcast(MainActivity.latitude,MainActivity.longitude,units);

        try {
            call.enqueue(new Callback<WeatherData>() {
                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                    if (response.body() == null) {
                        Toast.makeText(getContext(), "Please Enter a Valid Location", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        location.setText(response.body().getName());
                        Log.d("location",response.body().getName()+"   "+response);
                        temp.setText(String.valueOf(Math.round(response.body().getMain().getTemp())) + "°");
                        conditions.setText(response.body().getWeather().get(0).getMain());
                        String weath = response.body().getWeather().get(0).getMain();
                        if (weath.equals("Clouds")) relay.setBackgroundResource(R.drawable.clouds);
                        if (weath.equals("Rain")) relay.setBackgroundResource(R.drawable.rain);
                        if (weath.equals("Mist")) relay.setBackgroundResource(R.drawable.mist);
                        if (weath.equals("Fog")) relay.setBackgroundResource(R.drawable.fog);
                        if (weath.equals("Clear")) relay.setBackgroundResource(R.drawable.clear);
                        if (weath.equals("Drizzle")) relay.setBackgroundResource(R.drawable.drizzle);
                        if (weath.equals("Smoke")) relay.setBackgroundResource(R.drawable.smoke);
                    }
                }

                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {

                }
            });


            call2.enqueue(new Callback<ForcastData>() {
                @Override
                public void onResponse(Call<ForcastData> call, Response<ForcastData> response) {
                    if (response.body() == null) {
                        return;

                    } else {
                        Log.d("message", response.toString());
                        tempCount = 0;
                        for (int i = 0; i < 5; i++) {
                            days.get(i).setDayOfWeek(strDays[(dayOfWeek + i) % 7]);
                            days.get(i).setTemp(Math.round(response.body().getList().get(tempCount).getMain().getTemp()));
                            days.get(i).setCondition(response.body().getList().get(tempCount).getWeather().get(0).getMain());
                            tempCount += 7;

                        }
                        dAdapter = new ForcastAdapter(getContext(), days);
                        listView.setAdapter(dAdapter);


                        //Log.d("message",String.valueOf(response.body().getList().size()));
                    }
                }

                @Override
                public void onFailure(Call<ForcastData> call, Throwable t) {
                    Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT);

                }
            });
        } catch (Exception E) {
            Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT);

        }
    }





}

