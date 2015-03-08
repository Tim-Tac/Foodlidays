package com.innervision.timtac.foodlidays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FragmentSettings extends Fragment {

    //UI declaration
    TextView identifiant;
    TextView email;
    TextView adresse;
    TextView zip;
    TextView ville;
    TextView etage;
    TextView numero;
    Button deco;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        identifiant.setText(MainActivity.session_room_number);
        email.setText(MainActivity.session_email);
        adresse.setText(MainActivity.session_street_address);
        zip.setText(MainActivity.session_zip);
        ville.setText(MainActivity.session_city);
        etage.setText(MainActivity.session_floor + getString(R.string.nd_floor_room) );
        numero.setText(MainActivity.session_room);

        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().clear().apply();
                Intent intent= new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {

        View v = inflater.inflate(R.layout.activity_settings, group, false);

        identifiant = (TextView)v.findViewById(R.id.indentifiant);
        email = (TextView)v.findViewById(R.id.email);
        adresse = (TextView)v.findViewById(R.id.adresse);
        zip = (TextView)v.findViewById(R.id.zip);
        ville = (TextView)v.findViewById(R.id.ville);
        etage = (TextView)v.findViewById(R.id.etage);
        numero = (TextView)v.findViewById(R.id.numero);
        deco = (Button)v.findViewById(R.id.deco);

        return v;
    }


    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.panier:
                Intent intent = new Intent(getActivity(), Card.class);
                startActivity(intent);
                return true;
            case R.id.pizza:
                Intent intent2 = new Intent(getActivity(), FoodCard.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

}
