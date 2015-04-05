package com.innervision.timtac.foodlidays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.innervision.timtac.foodlidays.UtilitiesClass.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentCard extends Fragment {

    public static ArrayList<Order_Article> myOrderArticles = new ArrayList<>();

    //UI declaration
    private TextView empty;
    private Button command;
    private TextView order_total;
    private TextView manag_fee;
    private TextView sub_total;
    private static ListView orderList;
    private ImageView delete;
    private LinearLayout order_ligne_info;
    private View separator;

    float fee = 0;
    private String url_fee = UtilitiesConfig.url_base + UtilitiesConfig.URL_GET_MANAGEMENT;
    private String url_order = UtilitiesConfig.url_base + UtilitiesConfig.URL_ORDER;
    private JSONObject order;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new GetCatFromServer().execute();
    }


    public class GetCatFromServer extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try{

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(url_fee);
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
            if(ligne != null) fee = Float.parseFloat(ligne);
        }
    }


    /****************************** Construction du panier *****************************************/


    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
    {
        View v = inflater.inflate(R.layout.activity_card, group, false);

        empty = (TextView)v.findViewById(R.id.card_empty);
        orderList = (ListView)v.findViewById(R.id.order_list);
        command = (Button)v.findViewById(R.id.order_button);
        order_total = (TextView)v.findViewById(R.id.order_total);
        manag_fee = (TextView)v.findViewById(R.id.fee_price);
        sub_total = (TextView)v.findViewById(R.id.sub_total);
        delete = (ImageView)v.findViewById(R.id.delete);
        order_ligne_info = (LinearLayout)v.findViewById(R.id.order_ligne_info);
        separator = v.findViewById(R.id.sep);

        if(myOrderArticles.isEmpty()) ShowEmptyCard();
        else FillCard();


        command.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowDialogPayement();
            }
        });


        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                ShowDialogQuantity(position);
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowConfirm();
            }
        });

        return v;
    }


    public void ShowEmptyCard()
    {
        orderList.setVisibility(View.GONE);
        command.setVisibility(View.GONE);
        order_total.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        order_ligne_info.setVisibility(View.GONE);
        separator.setVisibility(View.GONE);
        sub_total.setVisibility(View.GONE);
        manag_fee.setVisibility(View.GONE);
    }


    public void FillCard()
    {
        empty.setVisibility(View.GONE);

        AdapterOrderArticleToList ad = new AdapterOrderArticleToList();
        orderList.setAdapter(ad);

        float Total = 0;
        for(int i = 0; i < myOrderArticles.size() ; i++)
        {
            Total = Total + (Float.parseFloat(myOrderArticles.get(i).prix)*myOrderArticles.get(i).quantity);
        }

        Float sub = UtilitiesFunctions.round(Total, 3);
        Float tot = UtilitiesFunctions.round(sub + fee,3);

        sub_total.setText(getString(R.string.sub_total) + " " + String.valueOf(sub) + " €");
        manag_fee.setText(getString(R.string.manag_fee) + " " + String.valueOf(fee) + " €");
        order_total.setText(getString(R.string.total_order) + " " + String.valueOf(tot) + " €");
    }


    public void ConstructCommand(Boolean cash)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //récup des infos pour la commande
        String type_room = prefs.getString("session_type","");
        String email = prefs.getString("session_email","");
        String room_number = prefs.getString("session_room_number","");
        String id_room = prefs.getString("session_id","");
        String city = prefs.getString("session_city","");
        String zip = prefs.getString("session_zip","");
        String country = prefs.getString("session_country","");
        String address = prefs.getString("session_street_address","");
        String id_user = prefs.getString("session_user_id","");
        String floor = prefs.getString("session_floor","");
        String room = prefs.getString("session_room","");
        String method;
        if(cash) method = "cash";
        else method = "card";
        String language = Locale.getDefault().getLanguage();

        JSONArray food = new JSONArray();
        for(Order_Article a : myOrderArticles)
        {
            JSONObject j = new JSONObject();
            try {
                j.put("id",a.id);
                j.put("quantity",a.quantity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            food.put(j);
        }

        order = new JSONObject();
        try {
            order.put("type_room", type_room);
            order.put("email", email);
            order.put("room_number", room_number);
            order.put("id_room", id_room);
            order.put("city", city);
            order.put("zip", zip);
            order.put("country", country);
            order.put("address", address);
            order.put("id_user", id_user);
            order.put("floor", floor);
            order.put("room", room);
            order.put("plats", food);
            order.put("language",language);
            order.put("method_payement",method);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void MakeCommand(Boolean cash)
    {
        ConstructCommand(cash);

        new SendCommandToServer().execute(url_order, order.toString());
    }


    public class SendCommandToServer extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try{

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("order", params[1]));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                result = in.readLine();
                in.close();

            }catch(Exception e){
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String ligne)
        {
            super.onPostExecute(ligne);
            GetStatus(ligne);
        }
    }


    public void GetStatus(String s)
    {
        if(s != null)
        {
            if(s.length() > 30)
            {
                try
                {
                    JSONObject command = new JSONObject(s);

                    if(command.has("id") && command.has("status"))
                    {
                        myOrderArticles.clear();
                        Disposer.mViewPager.setCurrentItem(2);
                        FragmentProfil.Refresh(getActivity(),getString(R.string.error_network));
                        Toast.makeText(getActivity(),getString(R.string.access_order),Toast.LENGTH_LONG).show();
                    }
                    else Toast.makeText(getActivity(),getString(R.string.error_order),Toast.LENGTH_LONG).show();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else Toast.makeText(getActivity(),getString(R.string.error_network),Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(getActivity(),getString(R.string.error_network),Toast.LENGTH_SHORT).show();

    }


    public void ShowDialogQuantity(int p)
    {
        final int position = p;
        final Order_Article article = (Order_Article)orderList.getItemAtPosition(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View quantity_view = inflater.inflate(R.layout.dialog_quantity, null);

        final NumberPicker pick = (NumberPicker)quantity_view.findViewById(R.id.numberPicker);
        pick.setMaxValue(99);
        pick.setMinValue(1);
        pick.setValue(article.quantity);
        UtilitiesFunctions.setNumberPickerTextColor(pick, 0xff000000);


        builder.setTitle(getString(R.string.quantity) + " "+ article.name);
        builder.setView(quantity_view);

        builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //ne rien faire
            }
        });

        builder.setPositiveButton(getString(R.string.modify), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                article.quantity = pick.getValue();
                Disposer.mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        builder.setNeutralButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                myOrderArticles.remove(position);
                Disposer.mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void ShowDialogPayement()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View payement = inflater.inflate(R.layout.dialog_payement, null);

        final RadioButton cash = (RadioButton)payement.findViewById(R.id.payement_cash);
        cash.setChecked(true);

        builder.setTitle(getString(R.string.payment_method));
        builder.setView(payement);

        builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //ne rien faire
            }
        });

        builder.setPositiveButton(R.string.ok_action, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MakeCommand(cash.isChecked());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void ShowConfirm()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.confirm));
        builder.setTitle(getString(R.string.delete_card));

        builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //ne rien faire
            }
        });

        builder.setPositiveButton(R.string.ok_action, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                myOrderArticles.clear();
                Disposer.mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public class AdapterOrderArticleToList extends BaseAdapter
    {
        @Override
        public int getCount() {
            return myOrderArticles.size();
        }

        @Override
        public Object getItem(int position) {
            return myOrderArticles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_order,parent,false);

            TextView nom_plat = (TextView)convertView.findViewById(R.id.order_nom_plat);
            TextView prix_pc = (TextView)convertView.findViewById(R.id.order_prix_pc);
            TextView quantite = (TextView)convertView.findViewById(R.id.order_quantity);
            TextView prix_st = (TextView)convertView.findViewById(R.id.order_prix_st);
            ImageView order_pic = (ImageView)convertView.findViewById(R.id.order_img);

            final Order_Article art = myOrderArticles.get(position);

            Picasso.with(getActivity()).load(UtilitiesConfig.url_base + "/uploads/" + art.image).into(order_pic);
            nom_plat.setText(art.name);
            prix_pc.setText(art.prix + " €");
            quantite.setText(String.valueOf(art.quantity));
            prix_st.setText(String.valueOf(UtilitiesFunctions.round(art.quantity * (Float.parseFloat(art.prix)), 2)));

            return convertView;

        }
    }
}
