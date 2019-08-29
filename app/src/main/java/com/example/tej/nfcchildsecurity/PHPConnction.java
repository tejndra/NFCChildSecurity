package com.example.tej.nfcchildsecurity;

import android.os.AsyncTask;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tej on 09-02-2017.
 */


public class PHPConnction extends AsyncTask<String,Void,String> {
    URL url;
    HttpURLConnection htc;
    Parent ob;

    PHPConnction(Parent ob) {
        this.ob = ob;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuffer sb = new StringBuffer("");
        try {
            url = new URL(params[0]);
            htc = (HttpURLConnection) url.openConnection();
            InputStream ic = htc.getInputStream();
            InputStreamReader isr = new InputStreamReader(ic);
            BufferedReader br = new BufferedReader(isr);

            String ln;
            while ((ln = br.readLine()) != null) {
                sb.append(ln);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(ob.getApplicationContext(), "Back Press",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }

    @Override
    public void onPostExecute(String result) {
        String[] res=result.split(",");
        double lt=0.0,ln=0.0;
        for(int x=0;x<res.length; x++)
        {
            lt=Double.parseDouble(res[0]);
            ln=Double.parseDouble(res[1]);


        }
        ob.LOCATION_HOME=new LatLng(lt,ln);

    }
}




