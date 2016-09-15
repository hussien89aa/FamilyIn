package com.hussienalrubaye.familyfinder;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    ArrayList<UserInfo> UsersList= new ArrayList<UserInfo>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle b =getIntent().getExtras();
        String url=GlobalClass.WebURL+ "UserLocationHistory?PhoneUID="+b.getString("PhoneUID") +"&RecordNumners="+GlobalClass.RecordNumbers ;
        new AsyTaskReloadLocations().execute(url);
        Ads();
    }

    InterstitialAd mInterstitialAd;
    void Ads(){
        try{
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.Pop_ad_unit_id));
            AdRequest adRequest = new AdRequest.Builder()
                    //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    //  requestNewInterstitial();
                    //  beginPlayingGame();
                }
                @Override
                public void onAdLoaded() {
                    DisplayAdmob();
                }
            });
        }
        catch (Exception ex){}
    }

    private void DisplayAdmob() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.goback) {
          finish();
            CheckRate();
        }



        return super.onOptionsItemSelected(item);
    }
    // @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            CheckRate();

        }

        return super.onKeyDown(keyCode, event);
    }
    void  CheckRate(){
         // rate app
        if(FileLoad.IsRated==0) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            //Try Google play
                            intent.setData(Uri.parse("market://details?id="+ GlobalClass.APPURL));
                            if (!MyStartActivity(intent)) {
                                //Market (Google play) app seems not installed, let's try to open a webbrowser
                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+GlobalClass.APPURL));
                                MyStartActivity(intent) ;

                            }
                            FileLoad.IsRated = 1;
                            FileLoad sv = new FileLoad(getApplicationContext());
                            sv.SaveData();
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            finish();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.RateDesc)).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("later", dialogClickListener).show();
        }
        else
            this.finish();
    }
    // rating app
    private boolean MyStartActivity(Intent aIntent) {
        try
        {
            startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }

    public class AsyTaskReloadLocations extends AsyncTask<String, String, String> {
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
                UsersList.clear();

                if (ErrorID.equals( ErrorNumbers.NoError)){
                    LatLng sydney = new LatLng(-34, 151);
                    JSONArray UsersPhonesInfo=js.getJSONArray("UsersPhonesInfo");

                    for (int i = 0; i <UsersPhonesInfo.length() ; i++) {
                        JSONObject user=UsersPhonesInfo.getJSONObject(i);
                        UsersList.add( new UserInfo(
                                user.getString("PhoneUID"),
                                user.getString("PhoneName"),
                                user.getInt("BatteryLevel"),
                                user.getDouble("Latitude"),
                                user.getDouble("longitude"),
                                user.getString("DateRecord")));
                        sydney = new LatLng(user.getDouble("Latitude"),user.getDouble("longitude"));
                        mMap.addMarker(new MarkerOptions()
                                .position(sydney)
                                .title("Battery "+user.getInt("BatteryLevel") +"%")
                                .snippet("Date:"+ user.getString("DateRecord"))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_flag)));

                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                }





            }
            catch (Exception ex){}

        }
        protected void onPostExecute(String  result){

        }




    }


}
