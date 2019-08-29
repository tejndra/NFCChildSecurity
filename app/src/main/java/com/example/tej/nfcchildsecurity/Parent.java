package com.example.tej.nfcchildsecurity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Parent extends AppCompatActivity implements OnMapReadyCallback {

    LatLng LOCATION_HOME =new LatLng(0.0,0.0);
    private GoogleMap map;
    Button b1,b2,b3,b4,b5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.tej.nfcchildsecurity.R.layout.activity_parent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        b1=(Button)findViewById(R.id.terrain);
        b2=(Button)findViewById(R.id.satellite);
        b3=(Button)findViewById(R.id.normals);
        b4=(Button)findViewById(R.id.logout);
        b5=(Button)findViewById(R.id.button9);
        b5.setVisibility(View.INVISIBLE);



        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1);


        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Parent.this, Login.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PHPConnction mt=new PHPConnction (Parent.this);
                mt.execute("http://amberphotos.in/fetchdata_2.php");
                onMapReady(map);
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
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        map.addMarker(new MarkerOptions().position(LOCATION_HOME).title("Marker at Home").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcherf)));
        CameraUpdate update=CameraUpdateFactory.newLatLngZoom(LOCATION_HOME,18);
        map.animateCamera(update);
        map.getUiSettings().setZoomGesturesEnabled(true);

        CircleOptions options=new CircleOptions().center(LOCATION_HOME).radius(100).fillColor(0x33FF0000).strokeColor(Color.BLUE).strokeWidth(3);
        map.addCircle(options);



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                map.setTrafficEnabled(true);
                map.getFocusedBuilding();
                map.isBuildingsEnabled();



            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.setTrafficEnabled(true);
                map.getFocusedBuilding();
                map.isBuildingsEnabled();

            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.setTrafficEnabled(false);
                map.getFocusedBuilding();
                map.isBuildingsEnabled();
            }
        });

    }




}

