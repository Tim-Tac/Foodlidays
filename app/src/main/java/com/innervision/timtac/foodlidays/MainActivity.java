package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText login = (EditText)findViewById(R.id.login);
        EditText password= (EditText)findViewById(R.id.password);
        Button connexion= (Button)findViewById(R.id.connexion);
        Button connexionqr = (Button)findViewById(R.id.connexionQR);


        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"pas encore dispo",Toast.LENGTH_SHORT).show();
            }
        });

        connexionqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"pas encore dispo",Toast.LENGTH_SHORT).show();
            }
        });




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
                Toast.makeText(getApplicationContext(),"Paramètres", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.About:
                Toast.makeText(getApplicationContext(),"Contact", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.panier:
                Toast.makeText(getApplicationContext(),"Votre panier", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
