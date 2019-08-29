package com.example.tej.nfcchildsecurity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;

public class Conductor extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener{
    NfcAdapter nfcAdapter;
    EditText edt1;
    EditText edt2;
    Button b1,b2,b3,b4,b5;
    String phones,smsss;
    int ress=0;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor);


        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        edt1=(EditText) findViewById(R.id.editText4);
        edt2=(EditText) findViewById(R.id.editText5);
        b1=(Button)findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button4);
        b4=(Button)findViewById(R.id.button3);
        b5=(Button)findViewById(R.id.button8);
        b5.setVisibility(View.INVISIBLE);

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senddata mt=new senddata(Conductor.this);
                mt.execute("http://amberphotos.in/senddata.php?Lat="+lat+"&Lon="+lon);

            }

        });

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                b5.performClick();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();




        buildGoogleApiClient();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1);



        edt1.setVisibility(View.INVISIBLE);
        edt2.setVisibility(View.INVISIBLE);

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),""+ress,Toast.LENGTH_SHORT).show();

            }
        });



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        b1.setVisibility(View.INVISIBLE);


       b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Conductor.this, Login.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), Help.class);
                Bundle bd = new Bundle();
                it.putExtras(bd);
                startActivity(it);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "Back Press",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }

    @Override
    protected void onResume(){
        super.onResume();

        enableForegroundDispatchSystem();

    }

    private void enableForegroundDispatchSystem() {

        try {
            Intent intents = new Intent(this, Conductor.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

            PendingIntent pendingIntents = PendingIntent.getActivity(this, 0, intents, 0);

            IntentFilter[] intentFilterss = new IntentFilter[]{};

            nfcAdapter.enableForegroundDispatch(this, pendingIntents, intentFilterss, null);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        disableForegroundDispatchSystem();

    }

    private void disableForegroundDispatchSystem() {
        try {
            nfcAdapter.disableForegroundDispatch(this);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this,"NfcIntent",Toast.LENGTH_SHORT).show();

            Parcelable[] parcelables=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(parcelables !=null && parcelables.length>0)
            {
                readTextFromMessage((NdefMessage)parcelables[0]);
                b1.performClick();
                edt1.setText("");
                edt2.setText("");


            }
            else
            {
                Toast.makeText(this,"No NDEF Message Found",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage ) {

        NdefRecord[] ndefRecords=ndefMessage.getRecords();

        if(ndefRecords !=null && ndefRecords.length>0){

            NdefRecord ndefRecord=ndefRecords[0];
            NdefRecord ndefRecordss=ndefRecords[1];

            String et1=getTextFromNdefRecordss(ndefRecord);
            String et2=getTextFromNdefRecords(ndefRecordss);


            edt1.setText(et1);
            edt2.setText(et2);

            phones=et1;
            smsss=et2;


        }
        else
        {

        }
    }

    private String getTextFromNdefRecordss(NdefRecord ndefRecord) {

        String et1=null;
        try
        {
            byte[] payloads=ndefRecord.getPayload();
            String textEncodings=((payloads[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSizes = payloads[0] & 0063;
            et1=new String(payloads,languageSizes+1,payloads.length-languageSizes-1,textEncodings);

        }
        catch(UnsupportedEncodingException e)
        {
            Log.e("getTextFromNdefRecords",e.getMessage());

        }

        return et1;


    }



    public String getTextFromNdefRecords(NdefRecord ndefRecordss)
    {

        String et2=null;
        try
        {
            byte[] payloads=ndefRecordss.getPayload();
            String textEncodings=((payloads[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSizes = payloads[0] & 0063;
            et2=new String(payloads,languageSizes+1,payloads.length-languageSizes-1,textEncodings);

        }
        catch(UnsupportedEncodingException e)
        {
            Log.e("getTextFromNdefRecords",e.getMessage());

        }

        return et2;


    }

    protected void sendSMS(){

        SmsManager manager=SmsManager.getDefault();
        manager.sendTextMessage(phones,null,smsss,null,null);
        Toast.makeText(getApplicationContext(),"Send Successfully",Toast.LENGTH_LONG).show();
        ress++;
    }


    @Override
    public void onConnected(Bundle bundle) {


        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = Double.valueOf(mLastLocation.getLatitude());
            lon = Double.valueOf(mLastLocation.getLongitude());

        }
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = Double.valueOf(location.getLatitude());
        lon = Double.valueOf(location.getLongitude());
        updateUI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    void updateUI() {

    }

    }

