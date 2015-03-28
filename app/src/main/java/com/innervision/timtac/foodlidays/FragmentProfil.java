package com.innervision.timtac.foodlidays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FragmentProfil extends Fragment implements AdapterView.OnItemSelectedListener {

    private static ArrayList<UtilitiesClass.Order> ordersWanted = new ArrayList<>();
    private static ArrayList<UtilitiesClass.Order> allOrder = new ArrayList<>();
    private ArrayList<String> order_status = new ArrayList<>();
    private JSONArray jArrayOrder;
    private SharedPreferences prefs;

    //UI declaration
    private TextView identifiant;
    private TextView email;
    private TextView adresse;
    private TextView zip;
    private TextView ville;
    private TextView etage;
    private TextView numero;
    private Button deco;
    private TextView any_order;
    private LinearLayout info_command;
    private ListView list_command;
    private Spinner spinner_order;
    private ImageView reload;


    /*******************  Initialisation et récup des commandes du serveur ************************/

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        order_status.add(getString(R.string.last));
        order_status.add(getString(R.string.order_pending));
        order_status.add(getString(R.string.order_processed));
        order_status.add(getString(R.string.order_delivered));
        order_status.add(getString(R.string.order_canceled));

        RetrieveOrders();
    }


    public void RetrieveOrders()
    {

        new GetAllOrdersForEmailFromServer().execute();
    }


    public class GetAllOrdersForEmailFromServer extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            String res = null;
            String url = UtilitiesConfig.url_base + UtilitiesConfig.URL_GET_ORDER + prefs.getString("session_email","");

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
            FillAllOrders(ligne);
        }
    }


    public void FillAllOrders(String s)
    {
        if(s != null)
        {
            if(s.length() > 50)
            {
                try {
                    jArrayOrder = new JSONArray(s);

                    for (int i = 0; i < jArrayOrder.length(); i++)
                    {
                        JSONObject j = jArrayOrder.getJSONObject(i);
                        UtilitiesClass.Order ord = new UtilitiesClass.Order();
                        ord.id = j.getInt("id");
                        ord.status = j.getString("status");
                        ord.time = j.getString("created_at");
                        ord.method_payement = j.getString("payment_mode");
                        ord.prix = j.getString("total_price");
                        ord.recap = j.getJSONArray("foods");
                        allOrder.add(ord);
                    }
                    Disposer.mSectionsPagerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else Toast.makeText(getActivity(),getString(R.string.errror_network),Toast.LENGTH_LONG).show();
    }



    /************************ création de la liste des commandes voulues **************************/


    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
    {
        View v = inflater.inflate(R.layout.activity_profil, group, false);

        identifiant = (TextView)v.findViewById(R.id.indentifiant);
        email = (TextView)v.findViewById(R.id.email);
        adresse = (TextView)v.findViewById(R.id.adresse);
        zip = (TextView)v.findViewById(R.id.zip);
        ville = (TextView)v.findViewById(R.id.ville);
        etage = (TextView)v.findViewById(R.id.etage);
        numero = (TextView)v.findViewById(R.id.numero);
        deco = (Button)v.findViewById(R.id.deco);
        any_order = (TextView)v.findViewById(R.id.any_command);
        info_command = (LinearLayout)v.findViewById(R.id.info_command);
        list_command = (ListView)v.findViewById(R.id.list_command);
        spinner_order = (Spinner)v.findViewById(R.id.spinner_order);
        reload = (ImageView)v.findViewById(R.id.reload);

        //fill spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, order_status);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_order.setAdapter(adapter);
        spinner_order.setOnItemSelectedListener(this);

        FillFields();

        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeconnectUser();
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GetAllOrdersForEmailFromServer().execute();
                Disposer.mSectionsPagerAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(),getString(R.string.reloaded),Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }


    public void FillFields()
    {
        identifiant.setText(prefs.getString("session_room_number",""));
        email.setText(prefs.getString("session_email",""));
        adresse.setText(prefs.getString("session_street_address",""));
        zip.setText(prefs.getString("session_zip",""));
        ville.setText(prefs.getString("session_city",""));
        etage.setText(prefs.getString("session_floor","") + getString(R.string.nd_floor_room) );
        numero.setText(prefs.getString("session_room",""));
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        ordersWanted.clear();

        if(!allOrder.isEmpty())
        {
            any_order.setVisibility(View.GONE);
            info_command.setVisibility(View.VISIBLE);
            list_command.setVisibility(View.VISIBLE);
            switch (position)
            {
                case 0 : ordersWanted.add(allOrder.get(allOrder.size() - 1));
                    break;
                case 1 :
                    for (UtilitiesClass.Order o : allOrder)
                    {
                        if(o.status.equals("pending"))
                            ordersWanted.add(o);
                    }
                    break;
                case 2 :
                    for (UtilitiesClass.Order o : allOrder)
                    {
                        if(o.status.equals("processed"))
                            ordersWanted.add(o);
                    }
                    break;
                case 3 :
                    for (UtilitiesClass.Order o : allOrder)
                    {
                        if(o.status.equals("delivered"))
                            ordersWanted.add(o);
                    }
                    break;
                case 4 :
                    for (UtilitiesClass.Order o : allOrder)
                    {
                        if(o.status.equals("canceled"))
                            ordersWanted.add(o);
                    }
                    break;
            }
        }
        else
        {
            any_order.setVisibility(View.VISIBLE);
            info_command.setVisibility(View.GONE);
            list_command.setVisibility(View.GONE);
        }

        AdapterOrderToList ad = new AdapterOrderToList();
        list_command.setAdapter(ad);

        list_command.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ShowResume(position);
            }
        });
    }


    public void ShowResume(int pos)
    {
        final UtilitiesClass.Order ord = (UtilitiesClass.Order)list_command.getItemAtPosition(pos);

        for(UtilitiesClass.Order o : allOrder)
        {
            if(o.id == ord.id)
            {
                String msg = "";

                for(int i=0;i<o.recap.length();i++)
                {
                    try {
                        JSONObject jObj = o.recap.getJSONObject(i);
                        msg = msg + jObj.getString("ordered_quantity") + " x " + jObj.getString("food_name") + "\n\n";

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                msg = msg + "------------------------" + "\n\n" + o.method_payement + ", " + o.prix + "€";

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("info");
                builder.setMessage(msg);


                builder.setPositiveButton(R.string.ok_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //nothing to o
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }


    public class AdapterOrderToList extends BaseAdapter
    {
        @Override
        public int getCount() {
            return ordersWanted.size();
        }

        @Override
        public Object getItem(int position) {
            return ordersWanted.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_command,parent,false);

            TextView command_id = (TextView)convertView.findViewById(R.id.command_id);
            TextView command_status = (TextView)convertView.findViewById(R.id.command_status);
            TextView command_time = (TextView)convertView.findViewById(R.id.command_time);

            final UtilitiesClass.Order ord = ordersWanted.get(position);

            command_id.setText(String.valueOf(ord.id));
            command_time.setText(ord.time);
            command_status.setText(ord.status);

            return convertView;

        }
    }


    public void DeconnectUser()
    {
        prefs.edit().clear().apply();
        Intent intent= new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        //nothing to do
    }


}
