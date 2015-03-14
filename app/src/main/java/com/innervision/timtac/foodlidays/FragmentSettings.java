package com.innervision.timtac.foodlidays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentSettings extends Fragment {

    //UI declaration
    private TextView identifiant;
    private TextView email;
    private TextView adresse;
    private TextView zip;
    private TextView ville;
    private TextView etage;
    private TextView numero;
    private Button deco;


    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
    {
        View v = inflater.inflate(R.layout.activity_settings, group, false);

        identifiant = (TextView)v.findViewById(R.id.indentifiant);
        email = (TextView)v.findViewById(R.id.email);
        adresse = (TextView)v.findViewById(R.id.adresse);
        zip = (TextView)v.findViewById(R.id.zip);
        ville = (TextView)v.findViewById(R.id.ville);
        etage = (TextView)v.findViewById(R.id.etage);
        numero = (TextView)v.findViewById(R.id.numero);
        deco = (Button)v.findViewById(R.id.deco);

        FillFields();

        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeconnectUser();
            }
        });

        return v;
    }


    public void FillFields()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().clear().apply();
        Intent intent= new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

}
