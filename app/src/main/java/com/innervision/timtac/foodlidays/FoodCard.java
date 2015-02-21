package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

public class FoodCard extends Activity implements AdapterView.OnItemSelectedListener {

    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_card);

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayList<String> types = new ArrayList<>();
        types.add("entrées");
        types.add("plats");
        types.add("desserts");
        types.add("formages");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //String url = "http://foodlidays.dev.innervisiongroup.com/api/v1/food/cat/all/1050";
        String url = "http://192.168.1.13:8000/api/v1/food/cat/all/1050";

        /*try {
            result = new GetRequest().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/

        //Toast.makeText(getApplicationContext(),"ici : "+ result,Toast.LENGTH_SHORT).show();

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
                Intent intent = new Intent(FoodCard.this, Settings.class);
                startActivity(intent);
                return true;
            case R.id.panier:
                if(Card.panier == null) Toast.makeText(getApplicationContext(),R.string.empty_card,Toast.LENGTH_SHORT).show();
                else
                {
                    Intent intent2 = new Intent(FoodCard.this, Card.class);
                    startActivity(intent2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String item = parent.getItemAtPosition(pos).toString();
        //Toast.makeText(getApplicationContext(),"sélectionné : " + item,Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}