package com.innervision.timtac.foodlidays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity {

    private EditText number;
    private EditText email;
    private String semail;
    private String snumber;

    // résultats pour les scripts serveur
    private String result;
    private String result2;

    // toutes les variables de session
    private static String session_type;
    private static String session_email;
    private static String session_room;
    private static String session_id;
    private static String session_user_id;
    private static String session_room_number;
    private static String session_floor;
    private static String session_street_address;
    private static String session_city;
    private static String session_zip;
    private static String session_country;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = (EditText)findViewById(R.id.number);
        email = (EditText)findViewById(R.id.email);
        Button connexion= (Button)findViewById(R.id.connexion);
        ImageView imageqr = (ImageView)findViewById(R.id.imageqr);

        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                semail = email.getText().toString();
                snumber = number.getText().toString();

                if((!semail.matches("")) && !snumber.matches(""))
                {
                    if(isEmailValid(semail))
                    {
                        String url = "http://foodlidays.dev.innervisiongroup.com/api/v1/login";
                        try {
                            new Script().execute(url,snumber,semail).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

                        if(result.length() < 30)                       {
                            Toast.makeText(getApplicationContext(),R.string.bad_room_nuber,Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            try {

                                JSONObject object = new JSONObject(result);
                                session_email = object.getString("email");
                                session_type = object.getString("place_type");
                                result2 = object.getString("room");
                                JSONObject object2 = new JSONObject(result2);
                                session_id = object2.getString("id");
                                session_room_number = object2.getString("room_number");
                                session_floor = object2.getString("floor");
                                session_room = object2.getString("room");
                                session_user_id= object2.getString("user_id");
                                session_street_address= object2.getString("street_address");
                                session_city= object2.getString("zip");
                                session_country= object2.getString("city");
                                session_zip= object2.getString("country");

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("log_tag", "error in parsing " + e.toString());
                            }

                            Toast.makeText(getApplicationContext(),R.string.signed_in,Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, FoodCard.class);
                            startActivity(intent);
                        }
                    }
                    else Toast.makeText(getApplicationContext(),R.string.email_invalid,Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getApplicationContext(),R.string.need_two_field,Toast.LENGTH_SHORT).show();
            }
        });

        imageqr.setOnClickListener(new View.OnClickListener()
        {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null)
            {
                number.setText(result.getContents());
            }
            else
            {
                Toast.makeText(getApplicationContext(),R.string.qr_code_failed,Toast.LENGTH_SHORT).show();
            }
        }

    private boolean isEmailValid(String email)
    {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?" +
                "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])" +
                "\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4]" +
                "[0-9])){1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern;
        pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    public class Script extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try{

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost request = new HttpPost(params[0]);

                List<NameValuePair> nameValuePairs = new ArrayList<>(3);
                nameValuePairs.add(new BasicNameValuePair("room_number", params[1]));
                nameValuePairs.add(new BasicNameValuePair("email", params[2]));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(request);

                BufferedReader in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));
                result = in.readLine();
                in.close();

            }catch(Exception e){
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String ligne){
        }

    }

    public static String getSession_type()
    {
        return session_type;
    }

    public static String getSession_email()
    {
        return session_email;
    }

    public static String getSession_room()
    {
        return session_room;
    }

    public static String getSession_id()
    {
        return session_id;
    }

    public static String getSession_user_id()
    {
        return session_user_id;
    }

    public static String getSession_room_number()
    {
        return session_room_number;
    }

    public static String getSession_floor()
    {
        return session_floor;
    }

    public static String getSession_street_address() {
        return session_street_address;
    }

    public static String getSession_city() {
        return session_city;
    }

    public static String getSession_zip() {
        return session_zip;
    }

    public static String getSession_country() {
        return session_country;
    }

}