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

        String is_co;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Start.this);
        is_co = prefs.getString("session_room_number","");

        if(!is_co.equals(""))
        {
            MainActivity.session_room_number = is_co;
            MainActivity.session_email = prefs.getString("session_email","email");
            MainActivity.session_room = prefs.getString("session_room","number");
            MainActivity.session_city = prefs.getString("session_city","city");
            MainActivity.session_country = prefs.getString("session_country","country");
            MainActivity.session_floor = prefs.getString("session_floor","floor");
            MainActivity.session_id = prefs.getString("session_id","id");
            MainActivity.session_street_address = prefs.getString("session_street_address","address");
            MainActivity.session_type = prefs.getString("session_type","type");
            MainActivity.session_zip = prefs.getString("session_zip","zip");
            MainActivity.session_user_id = prefs.getString("session_user_id","user_id");     

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
