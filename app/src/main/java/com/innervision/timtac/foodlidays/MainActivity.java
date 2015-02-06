package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
    private EditText number;
    private EditText email;
    private String semail;
    private String snumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = (EditText)findViewById(R.id.number);
        email = (EditText)findViewById(R.id.email);
        Button connexion= (Button)findViewById(R.id.connexion);
        Button connexionqr = (Button)findViewById(R.id.connexionQR);


        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                semail = email.getText().toString();
                snumber = number.getText().toString();

                Intent intent = new Intent(MainActivity.this, FoodCard.class);
                intent.putExtra("email",semail);
                intent.putExtra("number",snumber);
                startActivity(intent);
            }
        });

        connexionqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
                }
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
            case R.id.panier:
                Toast.makeText(getApplicationContext(),"Votre panier", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                snumber = data.getStringExtra("SCAN_RESULT");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Entrez votre email");

                final EditText input = new EditText(MainActivity.this);
                input.setHint("votre email");
                input.setPadding(25, 25, 25, 25);
                input.setTextSize(20);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);


                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        semail = input.getText().toString();

                        Intent intent = new Intent(MainActivity.this, FoodCard.class);
                        intent.putExtra("email",semail);
                        intent.putExtra("number",snumber);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }
}
