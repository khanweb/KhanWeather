package com.example.shaza.khanweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ForcastAdapter extends ArrayAdapter<Day> {


    private Context mContext;
    private List<Day> dayList = new ArrayList<>();

    public ForcastAdapter(Context context,ArrayList<Day> list) {
        super(context, 0 , list);
        mContext = context;
        dayList = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.forcast_list_item,parent,false);

        Day currentDay = dayList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.temp);
        name.setText(String.valueOf((int)currentDay.getTemp())+"°");

        TextView release = (TextView) listItem.findViewById(R.id.Day_of_week);
        release.setText(currentDay.getDayOfWeek());

        TextView condition = (TextView) listItem.findViewById(R.id.weatherCond);
        condition.setText(currentDay.getCondition());

        return listItem;
    }
}


