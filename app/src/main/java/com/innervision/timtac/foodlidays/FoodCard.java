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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FoodCard extends Activity implements AdapterView.OnItemSelectedListener {

    private String result;
    public ArrayList<String> cat = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_card);

        final ProgressBar myProgress = (ProgressBar)findViewById(R.id.progress);
        ListView mylist = (ListView)findViewById(R.id.list);

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        String url = "http://foodlidays.dev.innervisiongroup.com/api/v1/food/cat/all/1050";
        //String url = "http://192.168.1.13:8000/api/v1/food/cat/all/1050";

        try {
            result = new GetRequest().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        if(result != null)
        {
            Toast.makeText(getApplicationContext(),"res : "+ result,Toast.LENGTH_SHORT).show();

            try {

                JSONObject json_object = new JSONObject(result);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            cat.add("entrées");
            cat.add("plats");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,cat);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);


        }
        else Toast.makeText(getApplicationContext(),"Pas d'accès à internet", Toast.LENGTH_LONG).show();


        myProgress.setVisibility(View.GONE);

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


        //TODO mettre dans la listView tout les articles de cette catégorie et mettre un listener dessu




    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}