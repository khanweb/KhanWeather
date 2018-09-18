package com.example.shaza.khanweather;

import java.util.List;

class Lists {

    private Main main;
    private List<Weather> weather;
    private String dt_txt;


    public Lists(Main main, List<Weather> weather,String dt_txt) {
        this.main = main;
        this.weather = weather;
        this.dt_txt = dt_txt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }
}

