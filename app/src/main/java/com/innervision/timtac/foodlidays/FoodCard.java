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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FoodCard extends Activity implements AdapterView.OnItemSelectedListener {

    private Menu mMenu;
    private String result;
    public static String[] string_cat = {"Boissons","Burgers et sandwich","Desserts","Sushis et makis","Salades","Pizza","Végétarien","Pâtes","Asiatique","Déjeuner","Libanais","Appéritif"};
    public ArrayList<String> cat = new ArrayList<>();
    public static JSONArray jArray;
    public static JSONObject jsonObject;
    public static ListView myList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_card);

        final ProgressBar myProgress = (ProgressBar)findViewById(R.id.progress);
        myList = (ListView)findViewById(R.id.list);

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        cat.add("Toutes");


        String zip_code_temp = "1435";
        String url = "http://foodlidays.dev.innervisiongroup.com/api/v1/food/cat/all/"+zip_code_temp;

        try {
            result = new GetRequest().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if(result != null)
        {
            try {

                jArray = new JSONArray(result);

                for(int i=0;i<jArray.length();i++) {
                    jsonObject = jArray.getJSONObject(i);

                    if (!cat.contains(string_cat[jsonObject.getInt("category_id")-1]))
                        cat.add(string_cat[jsonObject.getInt("category_id")-1]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,cat);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

        }
        else Toast.makeText(getApplicationContext(),"Pas de connexion internet", Toast.LENGTH_LONG).show();

        myProgress.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                //TODO changer les icones ?
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

        Toast.makeText(getApplicationContext(),"sélectionné : " + item,Toast.LENGTH_SHORT).show();



        //TODO afficher les articles correspondants









        ArrayAdapter<String> adapt = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,string_cat);
        myList.setAdapter(adapt);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String article = parent.getItemAtPosition(position).toString();

                Toast.makeText(getApplicationContext(),"choisi : " + article,Toast.LENGTH_SHORT).show();
            }
        });

    }














    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}