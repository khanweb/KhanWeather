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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class weatherFragment extends Fragment {

    String id;
    String unitId;
    String units = "imperial";
    ListView listView;
    TextView conditions;
    TextView location;
    TextView temp;
    ImageView getData;
    EditText cityExt;
    RelativeLayout relay;
    String city = null;
    int tempCount = 0;
    ForcastAdapter dAdapter;
    ArrayList<Day> days = new ArrayList<>();
    String[] strDays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday"};
    Calendar calendar = Calendar.getInstance();
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);


    @Nullable

    public static weatherFragment newInstance(String location) {
        weatherFragment fragmentFirst = new weatherFragment();
        Bundle args = new Bundle();
        args.putString("id",location);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        unitId=id+"1";

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calls_fragment, menu);
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
            case R.id.Remove_Add:
                getData.setVisibility(View.VISIBLE);
                cityExt.setVisibility(View.VISIBLE);
                tempCount=0;
                return true;
            case R.id.update:
                refresh();
                Toast.makeText(getContext(), "Location Updated", Toast.LENGTH_SHORT).show();
                return true;
                default:
                return super.onOptionsItemSelected(item);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        final View v = inflater.inflate(R.layout.weather_frag, container, false);
        setHasOptionsMenu(true);


        for (int i = 0; i < 5; i++) {
            days.add(new Day());
        }


        conditions = v.findViewById(R.id.condition);
        location = v.findViewById(R.id.loc_name);
        temp = v.findViewById(R.id.temperature);
        relay = (RelativeLayout) v.findViewById(R.id.rel_back);
        getData = v.findViewById(R.id.dataGet);
        cityExt = v.findViewById(R.id.citnam);
        listView = (ListView) v.findViewById(R.id.listy);



        SharedPreferences prefs1 = getContext().getSharedPreferences(unitId, MODE_PRIVATE);
        final String unitString = prefs1.getString(unitId, null);

        if (unitString != null) units = unitString;

        SharedPreferences.Editor editor = getContext().getSharedPreferences(unitId, MODE_PRIVATE).edit();
        editor.putString(unitId, units);
        editor.apply();




        // To load the data at a later time
        SharedPreferences prefs = getContext().getSharedPreferences(id, MODE_PRIVATE);
        final String loadedString = prefs.getString(id, null);



//            visi.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (relay.getVisibility() == View.VISIBLE) {
//                        relay.setVisibility(View.GONE);
//                    }
//
//                    else  if (relay.getVisibility() == View.GONE) {
//                        relay.setVisibility(View.VISIBLE);
//                    }
//                }
//            });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

       final WeatherInterface api = retrofit.create(WeatherInterface.class);



        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setCity(cityExt.getText().toString());
                Log.d("test",city);



                Call<WeatherData> call = api.getCurrentWeather(city,units);
                Call<ForcastData> call2 = api.getForcastWeather(city,units);
                try {
                    call.enqueue(new Callback<WeatherData>() {
                        @Override
                        public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                            if (response.body() == null) {
                                Toast.makeText(getContext(), "Please Enter a Valid Location", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                SharedPreferences.Editor editor = getContext().getSharedPreferences(id, MODE_PRIVATE).edit();
                                editor.putString(id, city);
                                editor.apply();

                                getData.setVisibility(View.GONE);
                                cityExt.setVisibility(View.GONE);
                                location.setText(response.body().getName());
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
                                Log.d("message",response.toString());

                                for (int i = 0; i < 5; i++) {
                                    days.get(i).setDayOfWeek(strDays[(dayOfWeek + i) % 7]);
                                    days.get(i).setTemp(Math.round(response.body().getList().get(tempCount).getMain().getTemp()));
                                    days.get(i).setCondition(response.body().getList().get(tempCount).getWeather().get(0).getMain());
                                    tempCount += 8;

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
                }

                catch(Exception E){
                    Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT);

                }
            }

        });

        if(loadedString!=null) {
            setCity(loadedString);

            Call<WeatherData> call = api.getCurrentWeather(loadedString,units);
            Call<ForcastData> call2 = api.getForcastWeather(loadedString,units);

            try{
            call.enqueue(new Callback<WeatherData>() {
                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                    if (response.body() == null) {
                        Toast.makeText(getContext(), "Please Enter a Valid Location", Toast.LENGTH_SHORT).show();
                    } else {
                        getData.setVisibility(View.GONE);
                        cityExt.setVisibility(View.GONE);

                        location.setText(response.body().getName());
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
                        Toast.makeText(getContext(), "Please Enter a Valid Location", Toast.LENGTH_SHORT).show();
                    } else {
                        tempCount = 0;
                        for (int i = 0; i < 5; i++) {
                            days.get(i).setDayOfWeek(strDays[(dayOfWeek + i) % 7]);
                            days.get(i).setTemp(Math.round(response.body().getList().get(tempCount).getMain().getTemp()));
                            days.get(i).setCondition(response.body().getList().get(tempCount).getWeather().get(0).getMain());
                            tempCount += 7;
                        }
                        dAdapter = new ForcastAdapter(getContext(), days);
                        listView.setAdapter(dAdapter);

                    }

                    //Log.d("message",String.valueOf(response.body().getList().size()));

                }

                @Override
                public void onFailure(Call<ForcastData> call, Throwable t) {
                    Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT);

                }
            });
        }


            catch(Exception E){
            Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT);

        }



        }

            return v;

    }




public void refresh(){
tempCount=0;
    if(city == null) {
        Toast.makeText(getContext(),"Please enter a location first",Toast.LENGTH_LONG).show();
        return;
    }
    SharedPreferences.Editor editor = getContext().getSharedPreferences(unitId, MODE_PRIVATE).edit();
    editor.putString(unitId, units);
    editor.apply();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(WeatherInterface.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    final WeatherInterface api = retrofit.create(WeatherInterface.class);

    Call<WeatherData> call = api.getCurrentWeather(city,units);
    Call<ForcastData> call2 = api.getForcastWeather(city,units);

    try {
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.body() == null) {
                    Toast.makeText(getContext(), "Please Enter a Valid Location", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    SharedPreferences.Editor editor = getContext().getSharedPreferences(id, MODE_PRIVATE).edit();
                    editor.putString(id, city);
                    editor.apply();

                    getData.setVisibility(View.GONE);
                    cityExt.setVisibility(View.GONE);
                    location.setText(response.body().getName());
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
                    Log.d("message",response.toString());
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
    }

    catch(Exception E){
        Toast.makeText(getContext(), "FAILURE", Toast.LENGTH_SHORT);

    }


}


    public void setCity(String city) {
        this.city = city;
    }
}

