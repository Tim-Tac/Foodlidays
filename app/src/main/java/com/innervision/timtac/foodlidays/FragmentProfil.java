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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentProfil extends Fragment {

    public static ArrayList<UtilitiesClass.Order> myOrders = new ArrayList<>();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

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

        FillFields();

        if(myOrders.isEmpty())
        {
            ShowEmptyCommand();
            return v;
        }

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

                ShowResume();
            }
        });

        return v;
    }


    public void ShowResume()
    {
        Toast.makeText(getActivity(),"ok",Toast.LENGTH_SHORT).show();
    }


    public void ShowEmptyCommand()
    {
        list_command.setVisibility(View.GONE);
        info_command.setVisibility(View.GONE);
        title_command.setVisibility(View.GONE);
        any_order.setVisibility(View.VISIBLE);
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
