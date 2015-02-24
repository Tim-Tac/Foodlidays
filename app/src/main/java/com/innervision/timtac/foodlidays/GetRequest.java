package com.innervision.timtac.foodlidays;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GetRequest extends AsyncTask<String, String, String> {
    private String result;
    public AsyncResponse delegate = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(params[0]);
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
    protected void onPostExecute(String ligne)
    {
        //delegate.processFinish(ligne);
        super.onPostExecute(ligne);
    }


}
