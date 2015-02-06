package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class FoodCard extends Activity {
    private String email;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_card);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        number = intent.getStringExtra("number");

        Toast.makeText(getApplicationContext(),"identifiant : " + number + " email : " + email,Toast.LENGTH_LONG).show();
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
            case R.id.settings:
                Toast.makeText(getApplicationContext(),"Paramètres", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.panier:
                Toast.makeText(getApplicationContext(),"Votre panier", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
