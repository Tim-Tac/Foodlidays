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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

                if((!semail.matches("")) && !snumber.matches(""))
                {
                    if(isEmailValid(semail))
                    {
                        Intent intent = new Intent(MainActivity.this, FoodCard.class);
                        intent.putExtra("email", semail);
                        intent.putExtra("number", snumber);
                        startActivity(intent);
                    }
                    else Toast.makeText(getApplicationContext(),"Email invalide",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Vous devez remplir les 2 champs",Toast.LENGTH_SHORT).show();
                }


            }
        });

        connexionqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setResultDisplayDuration(0);
                integrator.setCameraId(0);
                integrator.setPrompt(" ");
                integrator.initiateScan();
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null) {
                snumber = result.getContents();

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

                        if((!semail.matches("")) && !snumber.matches(""))
                        {
                            Intent intent = new Intent(MainActivity.this, FoodCard.class);
                            intent.putExtra("email",semail);
                            intent.putExtra("number",snumber);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Vous devez remplir l'adresse email",Toast.LENGTH_SHORT).show();
                        }


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


    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern;
        pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }





}
