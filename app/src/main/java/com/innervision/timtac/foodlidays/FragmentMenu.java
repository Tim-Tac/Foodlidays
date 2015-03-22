package com.innervision.timtac.foodlidays;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.github.johnpersano.supertoasts.SuperToast;
import com.squareup.picasso.Picasso;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.innervision.timtac.foodlidays.UtilitiesClass.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class FragmentMenu extends Fragment implements AdapterView.OnItemSelectedListener {

    private static String result = "";
    private static String result_cat = "";
    private int spinner_selected = 0;

    //UI delcaration
    private static Spinner spinner;
    private static ListView myList;
    private static TextView any_restaurants;

    //result from request to server
    public static JSONArray jArray_articles = new JSONArray();
    public static JSONArray jArray_cat = new JSONArray();

    public static ArrayList<String> all_cat = new ArrayList<>();
    public static ArrayList<String> cat = new ArrayList<>();
    public static ArrayList<Article> liste_articles = new ArrayList<>();

    private String url_cat = UtilitiesConfig.url_base + UtilitiesConfig.URL_CAT;
    String zip_code_temp = "1435";
    private String url = UtilitiesConfig.url_base + UtilitiesConfig.URL_CAT_ZIPCODE + zip_code_temp;


    /**
    Récupère toutes les catégories pésentes sur le serveur, une seule fois au lacement de l'app
     **/


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Si pas de réseau
        if(!UtilitiesFunctions.isNetworkConnected(getActivity().getApplicationContext())) DisplayText(getString(R.string.no_connection));

        //récupère toutes les catégories au lancement
        RetrieveCat();
    }


    public void RetrieveCat()
    {

        new GetCatFromServer().execute(url_cat);
    }


    public class GetCatFromServer extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try{

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(url_cat);
                HttpResponse response = httpclient.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                res = in.readLine();
                in.close();

            }catch(Exception e){
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            return res;
        }

        @Override
        protected void onPostExecute(String ligne)
        {
            super.onPostExecute(ligne);
            result_cat = ligne;
            FillCat();
        }
    }


    public void FillCat()
    {
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
        }
    }




    /**
    Récupère les catégories disponibles dans la région de l'utilisateur
     **/


    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
    {
        View v = inflater.inflate(R.layout.activity_menu, group, false);

        myList = (ListView) v.findViewById(R.id.list);
        any_restaurants = (TextView) v.findViewById(R.id.any_restaurant);
        spinner = (Spinner) v.findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);

        // Si pas de réseau
        if(!UtilitiesFunctions.isNetworkConnected(getActivity().getApplicationContext())) return v;

        //rempli le spinner des catégories existantes
        RetrieveExistingCat();

        return v;
    }


    public void RetrieveExistingCat()
    {

        new GetExistingCatFromServer().execute();
    }


    public class GetExistingCatFromServer extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            String res = null;

            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                HttpResponse response = httpclient.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                res = in.readLine();
                in.close();

            }catch(Exception e){
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            return res;
        }

        @Override
        protected void onPostExecute(String ligne)
        {
            super.onPostExecute(ligne);
            result = ligne;
            FillExistingCat();
        }
    }


    public void FillExistingCat()
    {
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cat);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(spinner_selected);

            } else any_restaurants.setVisibility(View.VISIBLE);

        } else Toast.makeText(getActivity(), getString(R.string.errror_network), Toast.LENGTH_LONG).show();
    }




    /**
    Sur base de la catégorie choisie, établi une liste des articles de cette catégorie
     **/


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {

       String item = parent.getItemAtPosition(pos).toString();
       int id_cat = 0;
       final int selected = pos;

        //*** on récupère l'id de la catégorie **
       for(int i=0;i<jArray_cat.length();i++)
       {
           try {
               JSONObject jObj = jArray_cat.getJSONObject(i);

               if(jObj.getString("name").equals(item))
               {
                   id_cat = jObj.getInt("id");
               }

           } catch (JSONException e) {
               e.printStackTrace();
           }

       }

        liste_articles.clear();


        //*** on liste les articles de la catégorie ***
        for(int i=0;i<jArray_articles.length();i++)
        {
            try {

                JSONObject js = jArray_articles.getJSONObject(i);

                if(id_cat == js.getInt("category_id"))
                {
                    Article myArt = new Article();
                    myArt.name = js.getString("name");
                    myArt.detail = js.getString("note");
                    myArt.prix = js.getString("price");
                    myArt.image = js.getString("image");
                    myArt.id = js.getInt("id");

                    liste_articles.add(myArt);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AdapterArticleToList ad = new AdapterArticleToList();
        myList.setAdapter(ad);


        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Article article = (Article)myList.getItemAtPosition(position);


                //********************* on lance un dialog pour commander ***************************
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View dialog_view = inflater.inflate(R.layout.dialog_article, null);


                ImageView im = (ImageView) dialog_view.findViewById(R.id.big_pic);
                Picasso.with(getActivity()).load(UtilitiesConfig.url_base + "/uploads/" + article.image).into(im);
                final NumberPicker pick = (NumberPicker)dialog_view.findViewById(R.id.numberPicker);
                pick.setMaxValue(25);
                pick.setMinValue(1);
                UtilitiesFunctions.setNumberPickerTextColor(pick, 0xff000000);

                builder.setTitle(article.name);
                builder.setView(dialog_view);

                builder.setPositiveButton(R.string.ok_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Boolean isAlreadyIn = false;
                        for(Order_Article a : FragmentCard.myOrderArticles)
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
                            Order_Article order = new Order_Article();
                            order.name = article.name;
                            order.quantity = pick.getValue();
                            order.image = article.image;
                            order.prix = article.prix;
                            order.id = article.id;
                            FragmentCard.myOrderArticles.add(order);
                        }

                        Disposer.mSectionsPagerAdapter.notifyDataSetChanged();
                        spinner_selected = selected;
                        Toast.makeText(getActivity(),article.name + " " + getString(R.string.added_cart) ,Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //nothing to do
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }


    public void onNothingSelected(AdapterView<?> parent)
    {
        // nothing to do
    }


    public class AdapterArticleToList extends BaseAdapter
    {

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
            convertView = inflater.inflate(R.layout.list_detail,parent,false);

            TextView name = (TextView)convertView.findViewById(R.id.titre);
            TextView descr = (TextView)convertView.findViewById(R.id.description);
            TextView prix = (TextView)convertView.findViewById(R.id.prix);
            ImageView pic = (ImageView)convertView.findViewById(R.id.img);

            Article art = liste_articles.get(position);

            Picasso.with(getActivity()).load(UtilitiesConfig.url_base + "/uploads/" + art.image).into(pic);
            name.setText(art.name);
            descr.setText(art.detail);
            prix.setText(art.prix + " €");

            return convertView;

        }
    }


    public void DisplayText(String s)
    {
        SuperToast toast = new SuperToast(getActivity());
        toast.setTextColor(Color.WHITE);
        toast.setBackground(SuperToast.Background.RED);
        toast.setDuration(SuperToast.Duration.LONG);
        toast.setAnimations(SuperToast.Animations.FLYIN);
        toast.setText(s);
        toast.show();
    }

}