package com.innervision.timtac.foodlidays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import java.util.ArrayList;

public class FragmentProfil extends Fragment implements AdapterView.OnItemSelectedListener {

    public static ArrayList<UtilitiesClass.Order> myOrders = new ArrayList<>();
    private ArrayList<String> order_status = new ArrayList<>();
    private JSONArray allOrders;
    SharedPreferences prefs;


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
    private TextView title_command;
    private LinearLayout info_command;
    private ListView list_command;
    private Spinner spinner_order;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        order_status.add(getString(R.string.order_pending));
        order_status.add(getString(R.string.order_processed));
        order_status.add(getString(R.string.order_delivered));
        order_status.add(getString(R.string.order_canceled));

        RetrieveOrders();
    }


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
        title_command = (TextView)v.findViewById(R.id.display_command);
        info_command = (LinearLayout)v.findViewById(R.id.info_command);
        list_command = (ListView)v.findViewById(R.id.list_command);
        spinner_order = (Spinner)v.findViewById(R.id.spinner_order);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, order_status);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_order.setAdapter(adapter);
        spinner_order.setOnItemSelectedListener(this);

        FillFields();

        AdapterOrderToList ad = new AdapterOrderToList();
        list_command.setAdapter(ad);

        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeconnectUser();
            }
        });


        list_command.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ShowResume(position);
            }
        });

        return v;
    }


    public void ShowResume(int pos)
    {
        Toast.makeText(getActivity(),"résumé de la commande" + String.valueOf(pos),Toast.LENGTH_SHORT).show();

    }


    public void RetrieveOrders()
    {
        //scrip pour récup sur le serveur
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


    public void DeconnectUser()
    {
        prefs.edit().clear().apply();
        Intent intent= new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String type = parent.getItemAtPosition(position).toString();
        Toast.makeText(getActivity(),type,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        //nothing to do
    }


    public class AdapterOrderToList extends BaseAdapter
    {
        @Override
        public int getCount() {
            return myOrders.size();
        }

        @Override
        public Object getItem(int position) {
            return myOrders.get(position);
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

            final UtilitiesClass.Order ord = myOrders.get(position);

            command_id.setText(String.valueOf(ord.id));
            command_time.setText(ord.time);
            command_status.setText(ord.status);

            return convertView;

        }
    }
}
