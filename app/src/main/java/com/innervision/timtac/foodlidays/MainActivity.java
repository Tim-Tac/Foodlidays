package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            case R.id.profil:
                Toast.makeText(getApplicationContext(),"Profil", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
