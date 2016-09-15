package com.hussienalrubaye.familyfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;

public class Login extends AppCompatActivity {
    EditText Username;
    EditText  Userpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Userpassword =(EditText)findViewById(R.id.EDTpassword);
        Username =(EditText)findViewById(R.id.EDTUser);

    }

    //login page
    public void BuLogin(View view){
        if ((Username.getText().toString().length() <3)||(Userpassword.getText().toString().length() <3) ){
            String Message=  "Please Complete all your  information first .At lest 5 character in every field";
            ShowAlert( Message,ErrorNumbers.ErrorFound);
            return;
        }

        CheckUserPermsions();
    }
    //register page
    public void BuRegister(View view){
        Intent intent=new Intent( this,Registration.class);
        startActivity(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            return(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    public class AsyTaskLogin extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String  doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result;
            try {
                //String query =new String( params[0].getBytes(), "UTF-8");
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    result = Operations.ConvertInputToStringNoChange(in);
                }finally {
                    urlConnection.disconnect();
                }

                publishProgress(result);

            } catch (Exception e) {
                // TODO Auto-generated catch block

            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // pb.setVisibility(View.GONE);
            //   Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try{

                JSONObject js=new JSONObject(progress[0]);
                String  ErrorID=js.getString("ErrorID");
                String  Message=js.getString("Message");
                String  UserUID=js.getString("UserUID");
                String  PhoneUID=js.getString("PhoneUID");
                if (ErrorID.equals( ErrorNumbers.NoError)){
                    GlobalClass.UserUID=UserUID;
                    GlobalClass.PhoneUID=PhoneUID;
                    FileLoad fileLoad=new FileLoad(getApplicationContext());
                    fileLoad.SaveData();


                }

                ShowAlert(Message, ErrorID);



            }
            catch (Exception ex){}

        }
        protected void onPostExecute(String  result){

        }




    }


    void  ShowAlert(String Message,String ErrorID){
        final String ErrorIDFinal=ErrorID;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Message)
                .setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.Close),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (ErrorIDFinal.equals( ErrorNumbers.NoError)){
                                    //finish();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });





        final AlertDialog alert = builder.create();
        alert.show();

    }

    void  UserLogin(){

        String url= GlobalClass.WebURL + "UserLogin?EmailAdrress="+ Username.getText().toString()
                +"&Password="+ Userpassword.getText().toString()+
                "&PhoneMac="+ getUniqueID()+"&PhoneName="+ GlobalClass.UserName;
        new AsyTaskLogin().execute(url);
    }

    public   String getUniqueID(){
        String myAndroidDeviceId = "";
        try{
            TelephonyManager mTelephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null){
                myAndroidDeviceId = mTelephony.getDeviceId();
            }else{
                myAndroidDeviceId = Settings.Secure.getString( getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }

        }
        catch (Exception ex){
            long DateNumber = (long) new Date().getTime();
            Random r = new Random();
            myAndroidDeviceId= String.valueOf(DateNumber)+"F"+ String.valueOf(  r.nextInt(90000 - 65) + 65);

        }
        return myAndroidDeviceId;
    }

    //access to permsions
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) !=
                    PackageManager.PERMISSION_GRANTED  ) {

                    requestPermissions(new String[]{
                                    android.Manifest.permission.READ_PHONE_STATE},
                            REQUEST_CODE_ASK_PERMISSIONS);

                return ;
            }
        }

        UserLogin();// init the contact list

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UserLogin();// init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText( this,"Cannot create account for your, accept the permmison please", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
