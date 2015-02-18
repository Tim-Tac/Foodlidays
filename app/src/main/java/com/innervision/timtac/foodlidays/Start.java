package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Start extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        String rn;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Start.this);
        rn = prefs.getString("room_number","");

        if(!rn.equals(""))
        {
            Intent intent = new Intent(Start.this, FoodCard.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(Start.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
