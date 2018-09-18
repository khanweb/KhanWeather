package com.example.shaza.khanweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherInterface {



        String BASE_URL = "https://api.openweathermap.org/data/2.5/";


        @GET("weather?appid=4c303cfe706bb27c15eb345edda34281")
        Call<WeatherData> getCurrentWeather(@Query("q")String q,
                                            @Query("units")String units);

        @GET("forecast?mode=json&appid=4c303cfe706bb27c15eb345edda34281")
        Call<ForcastData> getForcastWeather(@Query("q")String q,
                                            @Query("units")String units);

        @GET("weather?&appid=4c303cfe706bb27c15eb345edda34281")
        Call<WeatherData> getLocalWeather(@Query("lat")double lat,
                                          @Query("lon")double lon,
                                          @Query("units")String units);

        @GET("forecast?appid=4c303cfe706bb27c15eb345edda34281")
        Call<ForcastData> getCurrentForcast(@Query("lat")double lat,
                                            @Query("lon")double lon,
                                            @Query("units")String units);
    }



