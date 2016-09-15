package com.hussienalrubaye.familyfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hussienalrubaye on 9/17/15.
 */

public class FileLoad {

    Context context;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs3" ;
    public static   String UserUID = "UserUID";
    public static   String PhoneUID = "PhoneUID";
    public static   String UserName = "UserName";
    public static int IsRated=0;//app rate 0 not rate 1 is rate
    public  FileLoad(Context context) {
        this.context=context;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }
    public void SaveData()  {

        try

        {

            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString(UserUID, String.valueOf(  GlobalClass.UserUID));
            editor.putString(PhoneUID, String.valueOf( GlobalClass.PhoneUID));
            editor.putString(UserName, String.valueOf( GlobalClass.UserName));
            editor.putInt("IsRated", IsRated);
            editor.commit();
            LoadData( );
        }

        catch( Exception e)

        {

           // Toast.makeText(context, "Unable to write to the SettingFile file.", Toast.LENGTH_LONG).show();
        }
    }
    public   void LoadData()
    {

        GlobalClass.PhoneUID=sharedpreferences.getString("PhoneUID","empty");
        if(!GlobalClass.PhoneUID.equals("empty"))
       {
              //  GlobalClass.PhoneUID=  PhoneUID;// load user name
                GlobalClass.UserUID=  sharedpreferences.getString("UserUID","0") ;// load last news
                IsRated=sharedpreferences.getInt("IsRated", 0);
               UserName=  sharedpreferences.getString("UserName","No name") ;

       }
        else
        {
            //start from login page
            Intent Login = new Intent(context, IdentifyPage.class);
            Login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Login);
        }

    }


}
