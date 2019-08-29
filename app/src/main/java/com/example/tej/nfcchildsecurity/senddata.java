package com.example.tej.nfcchildsecurity;

import android.os.AsyncTask;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tej on 09-02-2017.
 */

public class senddata extends AsyncTask<String,Void,String> {
    Conductor ob;
    URL url;
    HttpURLConnection httpu;
    senddata(Conductor ob){
        this.ob=ob;
    }
    @Override
    protected String doInBackground(String... params) {
        StringBuffer sb=new StringBuffer("");
        try {
            url = new URL(params[0]);
            httpu=(HttpURLConnection)url.openConnection();
            InputStream ic=httpu.getInputStream();
            InputStreamReader isr=new InputStreamReader(ic);
            BufferedReader br=new BufferedReader(isr);
            String ln;
            while((ln=br.readLine())!=null)
            {
                sb.append(ln);
            }
        }
        catch(Exception e){e.printStackTrace();}
        return sb.toString();
    }

    public void onPostExecute(String msg) {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(ob.getApplicationContext(), "Back Press",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }
}
