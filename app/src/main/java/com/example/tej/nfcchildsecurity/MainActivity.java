package com.example.tej.nfcchildsecurity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
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
import android.widget.ToggleButton;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    ToggleButton tglReadWrite;
    EditText txtTagContent;
    EditText et2;
    String record[]=new String[2];
    String phone,smss;
    Button b1,b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        tglReadWrite=(ToggleButton)findViewById(R.id.tglReadWrite);
        txtTagContent=(EditText) findViewById(R.id.txtTagContent);
        et2=(EditText)findViewById(R.id.editText);
        b1=(Button)findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button2);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, Login.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
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


    }


    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "Back Press",Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
        {
            Toast.makeText(this,"NFC Intent!",Toast.LENGTH_SHORT).show();

            if(tglReadWrite.isChecked())
            {

                Parcelable[] parcelable=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);


                if(parcelable!=null && parcelable.length > 0 )
                {

                    readTextFromMessage((NdefMessage)parcelable[0]);
                    b1.performClick();
                    txtTagContent.setText("");
                    et2.setText("");



                }
                else
                {
                    Toast.makeText(this,"No NDEF Messages Found!",Toast.LENGTH_SHORT).show();
                }

            }
            else
            {

                record[0]=new String(txtTagContent.getText().toString());
                record[1]=new String(et2.getText().toString());
                Tag tag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                NdefMessage ndefMessage=createNdefMessage(record);

                writeNdefMessage(tag,ndefMessage);


            }

        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords=ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0)
        {

            NdefRecord ndefRecord=ndefRecords[0];
            NdefRecord ndefRecord1=ndefRecords[1];


            String tagContent=getTextFromNdefRecord(ndefRecord);
            String tagContent1=getTextFromNdefRecord(ndefRecord1);


            txtTagContent.setText(tagContent);
            et2.setText(tagContent1);

            phone=tagContent;
            smss=tagContent1;

        }
        else
        {

            Toast.makeText(this,"No NDEF Messages Found!",Toast.LENGTH_SHORT).show();

        }
    }


    private void formatTag(Tag tag, NdefMessage ndefMessage){

        try
        {

            NdefFormatable ndefFormatable=NdefFormatable.get(tag);

            if(ndefFormatable == null)
            {
                Toast.makeText(this,"Tag is not ndef Formatable!",Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this,"Tag written!",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e("formatTag",e.getMessage());


        }
    }

    private void writeNdefMessage(Tag tag,NdefMessage ndefMessage)
    {

        try
        {

            if(tag == null)
            {
                Toast.makeText(this,"Tag object cannot be null",Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef=Ndef.get(tag);

            if(ndef == null)
            {
                formatTag(tag,ndefMessage);
            }
            else
            {
                ndef.connect();


                if(!ndef.isWritable() || txtTagContent.equals(null) || et2.equals(null) || txtTagContent.length()!=10)
                {
                    Toast.makeText(this,"Tag is not writable! Enter Valid Credentials.",Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this,"Tag written!",Toast.LENGTH_SHORT).show();

            }


        }
        catch (Exception e)
        {

            Log.e("writeNdefMessage",e.getMessage());
        }

    }

    private NdefRecord createTextRecord(String  content) {
        try {
            byte[] language;
            language= Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text=content.getBytes("UTF-8");
            final int languageSize=language.length;
            final int textLength=text.length;
            final ByteArrayOutputStream payload=new ByteArrayOutputStream(1+languageSize+textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language,0,languageSize);
            payload.write(text,0,textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT,new byte[0],payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord",e.getMessage());

        }
        return null;
    }

    private NdefMessage createNdefMessage(String [] content){

        NdefRecord ndefRecord=createTextRecord(content[0]);
        NdefRecord ndefRecord1=createTextRecord(content[1]);

        NdefMessage ndefMessage=new NdefMessage(new NdefRecord[]{ndefRecord,ndefRecord1});

        return ndefMessage;

    }

    public void tglReadWriteOnClick(View view){

        txtTagContent.setText("");
        et2.setText("");

    }

    public String getTextFromNdefRecord(NdefRecord ndefRecords)
    {

        String tagContent=null;
        try
        {
            byte[] payload=ndefRecords.getPayload();
            String textEncoding=((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent=new String(payload,languageSize+1,payload.length-languageSize-1,textEncoding);

        }
        catch(UnsupportedEncodingException e)
        {
            Log.e("getTextFromNdefRecord",e.getMessage());

        }

        return tagContent;


    }


    private void enableForegroundDispatchSystem(){

        try {
            Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            IntentFilter[] intentFilters = new IntentFilter[]{};

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    private void disableForegroundDispatchSystem(){

        try {
            nfcAdapter.disableForegroundDispatch(this);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    protected void sendSMS(){

        SmsManager manager=SmsManager.getDefault();
        manager.sendTextMessage(phone,null,smss,null,null);
        Toast.makeText(getApplicationContext(),"Send Successfully",Toast.LENGTH_LONG).show();
    }


}
