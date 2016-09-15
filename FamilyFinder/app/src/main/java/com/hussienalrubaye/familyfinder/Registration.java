package com.hussienalrubaye.familyfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Registration extends AppCompatActivity {
    EditText Username;
    EditText  Userpassword;
    Button BuSignUp;
    Button BuNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Userpassword =(EditText)findViewById(R.id.EDTpassword);
        Username =(EditText)findViewById(R.id.EDTUser);
        BuSignUp=(Button)findViewById(R.id.BuSignUp);
        BuNext=(Button)findViewById(R.id.BuNext);
        BuNext.setVisibility(View.GONE);
    }

    public void BuRegister(View view) {
        if ((Username.getText().toString().length() <3)||(Userpassword.getText().toString().length() <3) ){
            String Message=  "Please Complete all your  information first .At lest 5 character in every field";
            ShowAlert( Message,ErrorNumbers.ErrorFound);
            return;
        }
        String url= GlobalClass.WebURL + "OpenAccount?EmailAdrress="+ Username.getText().toString()
                +"&Password="+ Userpassword.getText().toString();
        new  AsyTaskLogin().execute(url);
    }

    public void BuNext(View view) {
        finish();
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

                if (ErrorID.equals( ErrorNumbers.NoError)){
                    GlobalClass.UserUID=UserUID;
                    //BuSignUp.setEnabled(false);
                    BuSignUp.setVisibility(View.GONE);
                    BuNext.setVisibility(View.VISIBLE);
                   // Toast.makeText(getApplicationContext(),Message,Toast.LENGTH_LONG).show();
                }

                    ShowAlert(Message,ErrorID);



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
                           finish();
                        }
                    }
                });





        final AlertDialog alert = builder.create();
        alert.show();

    }

}
