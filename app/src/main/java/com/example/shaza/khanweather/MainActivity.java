package com.example.shaza.khanweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Fragment> fragList = new ArrayList<>();
    static double longitude;
    static double latitude;
    static boolean locPerm = false;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocationPermission();
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        vpPager.setOffscreenPageLimit(5);
        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);


    }

    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private static int NUM_ITEMS = 6;


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Parcelable saveState()
        {
            return null;
        }
        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return CurrentWeatherFragment.newInstance(latitude,longitude);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return weatherFragment.newInstance("Location2");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return weatherFragment.newInstance("Location3");
                case 3: // Fragment # 1 - This will show SecondFragment
                    return weatherFragment.newInstance("Location4");
                case 4: // Fragment # 1 - This will show SecondFragment
                    return weatherFragment.newInstance("Location5");
                case 5: // Fragment # 1 - This will show SecondFragment
                    return weatherFragment.newInstance("Location6");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return "Current Location Weather";

            return "Location " + position;
        }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            locPerm=true;


            }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }

    }

    }




