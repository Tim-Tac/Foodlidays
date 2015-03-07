package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView identifiant = (TextView)findViewById(R.id.indentifiant);
        TextView email = (TextView)findViewById(R.id.email);
        TextView adresse = (TextView)findViewById(R.id.adresse);
        TextView zip = (TextView)findViewById(R.id.zip);
        TextView ville = (TextView)findViewById(R.id.ville);
        TextView etage = (TextView)findViewById(R.id.etage);
        TextView numero = (TextView)findViewById(R.id.numero);
        Button deco = (Button)findViewById(R.id.deco);

        identifiant.setText(MainActivity.session_room_number);
        email.setText(MainActivity.session_email);
        adresse.setText(MainActivity.session_street_address);
        zip.setText(MainActivity.session_zip);
        ville.setText(MainActivity.session_city);
        etage.setText(MainActivity.session_floor + getString(R.string.nd_floor_room) );
        numero.setText(MainActivity.session_room);

        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                prefs.edit().clear().apply();
                Intent intent= new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.panier:
                Intent intent = new Intent(Settings.this, Card.class);
                startActivity(intent);
                return true;
            case R.id.pizza:
                Intent intent2 = new Intent(Settings.this, FoodCard.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
