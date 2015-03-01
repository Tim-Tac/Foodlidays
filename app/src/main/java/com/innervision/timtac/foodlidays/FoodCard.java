package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FoodCard extends Activity implements AdapterView.OnItemSelectedListener, AsyncResponse {

    public static String result = "";
    public static String result_cat = "";

    public static Spinner spinner;
    public static ListView myList;
    public static TextView any_restaurants;

    public static JSONArray jArray_articles = new JSONArray();
    public static JSONArray jArray_cat = new JSONArray();

    public static ArrayList<String> all_cat = new ArrayList<>();
    public static ArrayList<String> cat = new ArrayList<>();
    public static ArrayList<Articles> liste_articles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_card);

        myList = (ListView)findViewById(R.id.list);
        any_restaurants = (TextView)findViewById(R.id.any_restaurant);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);



        if(isNetworkAvailable())
        {

            /*************************** Récupération des catégories *******************************/

            String url_cat = "http://foodlidays.dev.innervisiongroup.com/api/v1/category";
            try {

                result_cat = new GetRequest().execute(url_cat).get();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


            if (result_cat != null)
            {
                try {

                    jArray_cat = new JSONArray(result_cat);

                    for (int i = 0; i < jArray_cat.length(); i++) {

                        JSONObject jsonObject = jArray_cat.getJSONObject(i);
                        all_cat.add(jsonObject.getString("name"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else Toast.makeText(getApplicationContext(), "Erreur lors de l'accès au réseau, veuillez réessayer plus tard", Toast.LENGTH_LONG).show();



            /********************* Création de la liste des catégories disponibles *****************/

            String zip_code_temp = "1435";
            String url = "http://foodlidays.dev.innervisiongroup.com/api/v1/food/cat/all/" + zip_code_temp;

            try {
                result = new GetRequest().execute(url).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (result != null)
            {
                if(result.length() > 70)
                {

                    try {

                        jArray_articles = new JSONArray(result);

                        for (int i = 0; i < jArray_articles.length(); i++) {
                            JSONObject jsonObject = jArray_articles.getJSONObject(i);

                            int n_cat = jsonObject.getInt("category_id");

                            for(int j = 0;j<jArray_cat.length();j++)
                            {
                                JSONObject jObj = jArray_cat.getJSONObject(j);

                                if(n_cat == jObj.getInt("id"))
                                {
                                    if(!cat.contains(jObj.getString("name")))
                                    cat.add(jObj.getString("name"));
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cat);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                } else any_restaurants.setVisibility(View.VISIBLE);

            } else Toast.makeText(getApplicationContext(), "Erreur lors de l'accès au réseau, veuillez réessayer plus tard", Toast.LENGTH_LONG).show();

        } else Toast.makeText(getApplicationContext(), "Pas de connexion internet valide", Toast.LENGTH_LONG).show();
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
                Intent intent2 = new Intent(FoodCard.this, Card.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

       String item = parent.getItemAtPosition(pos).toString();
       int index = 0;

        /*** on récupère l'id de la catégorie ***/
       for(int i=0;i<jArray_cat.length();i++)
       {
           try {
               JSONObject jObj = jArray_cat.getJSONObject(i);

               if(jObj.getString("name").equals(item))
               {
                   index = jObj.getInt("id");
               }

           } catch (JSONException e) {
               e.printStackTrace();
           }

       }

        liste_articles.clear();


        /*** on liste les articles de la catégorie ***/
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
                    myArt.image = js.getString("image");

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


                /********************* on lance un dialog pour commander ***************************/
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodCard.this);

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext()); //FoodCard.this.getLayoutInflater();
                View dialog_view = inflater.inflate(R.layout.dialog_email, null);


                ImageView im = (ImageView) dialog_view.findViewById(R.id.big_pic);
                Picasso.with(getApplicationContext()).load("http://foodlidays.dev.innervisiongroup.com/uploads/" + article.image).into(im);
                final NumberPicker pick = (NumberPicker)dialog_view.findViewById(R.id.numberPicker);
                pick.setMaxValue(50);
                pick.setMinValue(1);

                builder.setTitle(article.name);
                builder.setView(dialog_view);

                builder.setPositiveButton(R.string.ok_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(getApplicationContext(),String.valueOf(pick.getValue()), Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    @Override
    public void processFinish(String output) {
        Toast.makeText(getApplicationContext(), "ici : " + output, Toast.LENGTH_SHORT).show();
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

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.detail_list,parent,false);

            TextView name = (TextView)convertView.findViewById(R.id.titre);
            TextView descr = (TextView)convertView.findViewById(R.id.description);
            TextView prix = (TextView)convertView.findViewById(R.id.prix);
            ImageView pic = (ImageView)convertView.findViewById(R.id.img);

            Articles art = liste_articles.get(position);

            Picasso.with(getApplicationContext()).load("http://foodlidays.dev.innervisiongroup.com/uploads/" + art.image).into(pic);
            name.setText(art.name);
            descr.setText(art.detail);
            prix.setText(art.prix + " €");

            return convertView;

        }
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    private boolean isNetworkAvailable() {
        getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}