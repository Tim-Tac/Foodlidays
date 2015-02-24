package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FoodCard extends Activity implements AdapterView.OnItemSelectedListener {

    private String result;
    private String result_cat;

    public static Spinner spinner;
    public static ListView myList;

    public static JSONArray jArray_articles = new JSONArray();
    public static JSONArray jArray_cat = new JSONArray();

    public static ArrayList<String> all_cat = new ArrayList<>();
    public static ArrayList<String> cat = new ArrayList<>();
    public static ArrayList<Articles> liste_articles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_card);

        final ProgressBar myProgress = (ProgressBar)findViewById(R.id.progress);
        myList = (ListView)findViewById(R.id.list);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);





        /*** Récupération des catégories ***/

        String url_cat = "http://foodlidays.dev.innervisiongroup.com/api/v1/category";
        try {
            result_cat = new GetRequest().execute(url_cat).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        if(result_cat != null)
        {
            try {

                jArray_cat = new JSONArray(result_cat);

                for(int i=0;i<jArray_cat.length();i++) {

                    JSONObject jsonObject = jArray_cat.getJSONObject(i);
                    all_cat.add(jsonObject.getString("name"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else Toast.makeText(getApplicationContext(),"Pas de connexion internet", Toast.LENGTH_LONG).show();





        /*** Création de la liste des catégories disponibles ***/

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

                jArray_articles = new JSONArray(result);

                for(int i=0;i<jArray_articles.length();i++) {
                    JSONObject jsonObject = jArray_articles.getJSONObject(i);

                    if (!cat.contains(all_cat.get(jsonObject.getInt("category_id")-1)))
                        cat.add(all_cat.get(jsonObject.getInt("category_id")-1));
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

        return true;
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

        //int index = java.util.Arrays.asList(string_cat).indexOf(item);
        int index = all_cat.indexOf(item);
        index = index +1;

        Toast.makeText(getApplicationContext(),item + " " + index,Toast.LENGTH_SHORT).show();

        liste_articles.clear();

        for(int i=0;i<jArray_articles.length();i++)
        {
            try {

                JSONObject js = jArray_articles.getJSONObject(i);

                if(index == js.getInt("category_id"))
                {
                    Articles myArt = new Articles();
                    myArt.name = js.getString("name");
                    myArt.detail = js.getString("note");
                    myArt.prix = js.getString("price");

                    liste_articles.add(myArt);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CustomArrayAdapter ad = new CustomArrayAdapter();
        myList.setAdapter(ad);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Articles article = (Articles)myList.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(), "choisi : " + article.name, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public class CustomArrayAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return liste_articles.size();
        }

        @Override
        public Object getItem(int position) {
            return liste_articles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.detail_list,parent,false);

            TextView name = (TextView)convertView.findViewById(R.id.titre);
            TextView descr = (TextView)convertView.findViewById(R.id.description);
            TextView prix = (TextView)convertView.findViewById(R.id.prix);
            ImageView pic = (ImageView)convertView.findViewById(R.id.img);

            Articles art = liste_articles.get(position);

            Picasso.with(getApplicationContext()).load("http://i.imgur.com/DvpvklR.png").into(pic);
            name.setText(art.name);
            descr.setText(art.detail);
            prix.setText(art.prix + "/pc");

            return convertView;

        }
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}