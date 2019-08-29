package com.example.tej.nfcchildsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class Help extends YouTubeBaseActivity {

    Button b,b1;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        b = (Button) findViewById(R.id.button5);
        b1 = (Button) findViewById(R.id.button6);
        b1.setVisibility(View.INVISIBLE);

            Thread timer = new Thread() {
                public void run() {
                    Boolean c = true;
                    try
                    {
                    do {
                        sleep(1000);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                b1.performClick();
                            }
                        });


                    }
                    while (c == false);

                } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                    }
                }
            };
            timer.start();




    youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtube);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("xR_GB7WtH8s");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize("AIzaSyBveEc3V_o9XuqIg9icGZPhdQNZFKqShc8",onInitializedListener);

            }
        });



        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Conductor.class);
                Bundle bd = new Bundle();
                intent.putExtras(bd);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(), "Back Press",Toast.LENGTH_LONG).show();
        }
        return false;

        // Disable back button..............
    }
}
