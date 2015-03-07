package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
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

import java.lang.reflect.Field;
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FoodCard.this);
        String is_co = prefs.getString("session_room_number","");

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
        }
        else
        {
            Intent intent = new Intent(FoodCard.this, MainActivity.class);
            startActivity(intent);
        }


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

                final Articles article = (Articles)myList.getItemAtPosition(position);


                /********************* on lance un dialog pour commander ***************************/
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodCard.this);
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View dialog_view = inflater.inflate(R.layout.dialog_article, null);


                ImageView im = (ImageView) dialog_view.findViewById(R.id.big_pic);
                Picasso.with(getApplicationContext()).load("http://foodlidays.dev.innervisiongroup.com/uploads/" + article.image).into(im);
                final NumberPicker pick = (NumberPicker)dialog_view.findViewById(R.id.numberPicker);
                pick.setMaxValue(25);
                pick.setMinValue(1);
                setNumberPickerTextColor(pick,0xff000000);

                builder.setTitle(article.name);
                builder.setView(dialog_view);

                builder.setPositiveButton(R.string.ok_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Boolean isAlreadyIn = false;
                        for(Order_Articles a : Card.myOrderArticles)
                        {
                            // Si un item dans la liste possède le même nom, on augmente juste la quantité
                            if(a.name.equals(article.name))
                            {
                                isAlreadyIn = true;
                                a.quantity += pick.getValue();
                            }
                        }


                        if(!isAlreadyIn) // Si pas déjà dedans, on rajoute l'item
                        {
                            Order_Articles order = new Order_Articles();
                            order.name = article.name;
                            order.quantity = pick.getValue();
                            order.image = article.image;
                            order.prix = article.prix;
                            Card.myOrderArticles.add(order);
                        }

                        Toast.makeText(getApplicationContext(),"article ajouté au panier !",Toast.LENGTH_LONG).show();
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

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPicker", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPicker", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPicker", e);
                }
            }
        }
        return false;
    }

}