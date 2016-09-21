package com.hussienalrubaye.familyfinder;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;

/**
 * Created by hussienalrubaye on 9/17/15.
 */
public class Operations {

    Context context;
    public  Operations(Context context){
       this.context=context;
    }
    public static String ConvertInputToStringNoChange(InputStream inputStream) {

        BufferedReader bureader=new BufferedReader( new InputStreamReader(inputStream));
        String line ;
        String linereultcal="";

        try{
            while((line=bureader.readLine())!=null) {

                    linereultcal+=line;

            }
            inputStream.close();


        }catch (Exception ex){}

        return linereultcal;
    }

    public   boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                        return isConnected;
                 

        }
        return false;
    }

    public Location getLocation( ) {

        Location myLocationListener = MyLocationListener.location;
        if (MyLocationListener.location == null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                criteria.setCostAllowed(false);

                String provider = locationManager.getBestProvider(criteria, false);

                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(provider); //(LocationManager.GPS_PROVIDER);
                
                // Ensure the previous location value
                if (lastKnownLocationGPS != null) {
                    return lastKnownLocationGPS;
                } else {
                    Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    return loc;
                }
            } else {
                return null;
            }
        }

        return  myLocationListener;

    }

}
