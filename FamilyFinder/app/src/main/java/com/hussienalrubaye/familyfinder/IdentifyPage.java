package com.hussienalrubaye.familyfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class IdentifyPage extends AppCompatActivity {
EditText EDTName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_page);
        EDTName=(EditText)findViewById(R.id.EDTName);
        EDTName.setText(GlobalClass.UserName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            return(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void BuNext(View view) {

        if ((EDTName.getText().toString().length() <3)  ){
            String Message=  "Please Complete all your  information first .At lest 5 character in every field";
            ShowAlert( Message,ErrorNumbers.ErrorFound);
            return;
        }

        finish();
        GlobalClass.UserName=EDTName.getText().toString();
        //start from login page
        Intent Login = new Intent(this, Login.class);
         startActivity(Login);

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

}
