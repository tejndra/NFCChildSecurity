package com.example.tej.nfcchildsecurity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    EditText et1,et2;
    Button b1,b2;
    Object e1,e2;
    TextView tv1,tv2,tv3;
    Intent it;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        b1=(Button)findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button7);
        et1=(EditText)findViewById(R.id.editText);
        et2=(EditText)findViewById(R.id.editText2);
        tv1=(TextView)findViewById(R.id.textView);
        tv2=(TextView)findViewById(R.id.textView2);
        tv3=(TextView)findViewById(R.id.textView3);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    e1 = et1.getText().toString();
                    e2 = et2.getText().toString();

                    if (e1.equals("admin") && e2.equals("admin")) {
                        it = new Intent(getApplicationContext(), MainActivity.class);
                        Bundle bd = new Bundle();
                        it.putExtras(bd);
                        startActivity(it);

                    }
                    else if(e1.equals("conductor")&& e2.equals("conductor"))
                    {
                        it = new Intent(getApplicationContext(), Conductor.class);
                        Bundle bd = new Bundle();
                        it.putExtras(bd);
                        startActivity(it);
                    }
                    else if(e1.equals("parent")&& e2.equals("parent"))
                    {
                        it = new Intent(getApplicationContext(), Parent.class);
                        Bundle bd = new Bundle();
                        it.putExtras(bd);
                        startActivity(it);
                    }
                    else {

                        Toast.makeText(getApplicationContext(), "Enter Valid Credentials", Toast.LENGTH_LONG).show();

                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Enter Valid Credentials", Toast.LENGTH_LONG).show();

                }


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

}
