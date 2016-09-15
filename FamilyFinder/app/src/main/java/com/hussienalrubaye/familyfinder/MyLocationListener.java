package com.hussienalrubaye.familyfinder;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ASUS S550C on 31/12/2014.
 */
public class MyLocationListener implements LocationListener {
    Context context;
    public  static  Location location;
 public  static boolean IsServiceRunning=false;
    Operations op;
    private boolean IsSending=false;
    public  MyLocationListener( Context context){
        this.context=context;
        IsServiceRunning=true;
        op= new Operations(context);
    }

    public void onLocationChanged(Location location) {
       // Toast.makeText(context,"ring",Toast.LENGTH_LONG).show();
       this. location=location;
        if(!op.isConnectingToInternet()) return ; // do not counine if there is internet service

        //only one send at time
        if (IsSending==true)
      return;

        IsSending=true;
        String url=GlobalClass.WebURL + "UserTracking?PhoneUID" +
                "="+ GlobalClass.PhoneUID +"&Latitude="+
                String.valueOf(location.getLatitude()) +
                "&longitude="+ String.valueOf(location.getLongitude()) +
                "&BatteryLevel="+String.valueOf((int)getBatteryLevel());
        new AsyTaskTracking().execute(url);
    }

    public void onStatusChanged(String s, int i, Bundle b) {
      //  Toast.makeText(context, "Provider status changed",
       //         Toast.LENGTH_LONG).show();
    }

    public void onProviderDisabled(String s) {
        Toast.makeText(context,
                "  GPS turned off . you cannot follow your locations",
                Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String s) {
        Toast.makeText(context,
                " GPS turned on. you can follow your locations",
                Toast.LENGTH_LONG).show();
    }

    public double getBatteryLevel() {
        Intent batteryIntent =context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0;
        }

        return ((double)level / (double)scale) * 100.0f;
    }
    //send gps to the server
    public class AsyTaskTracking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String  doInBackground(String... params) {
            // TODO Auto-generated method stub

            //String result;
            try {
                //String query =new String( params[0].getBytes(), "UTF-8");
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    //result = Operations.ConvertInputToStringNoChange(in);
                }finally {
                    urlConnection.disconnect();
                }

               // publishProgress(result);

            } catch (Exception e) {
                // TODO Auto-generated catch block

            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // pb.setVisibility(View.GONE);
            //   Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try{

               // JSONObject js=new JSONObject(progress[0]);
            //    String  ErrorID=js.getString("IsDeliver");

            }
            catch (Exception ex){}

        }
        protected void onPostExecute(String  result){
            IsSending=false;

        }




    }

}
