package com.hussienalrubaye.familyfinder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MyCustomAdapter myadapter;
    ArrayList<UserInfo> UsersList= new ArrayList<UserInfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // load file settings
        FileLoad fileinfo=new FileLoad(this);
        fileinfo.LoadData();

            //start the service
            CheckUserPermsions();



          myadapter=new MyCustomAdapter(UsersList);
        ListView lsNews=(ListView)findViewById(R.id.LVNews);
        lsNews.setAdapter(myadapter);//intisal with data


        try
        {
            //Hide if it is boot hide activity
            Bundle b=getIntent().getExtras();
            String IsBoot=b.getString("IsBoot","No");
            if (IsBoot.equals("Yes")){
                HideActivity();
            }
        }catch (Exception ex){}
    }



  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            HideActivity();

        }

        return super.onKeyDown(keyCode, event);
    }

    void HideActivity(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //access to permsions
    void CheckUserPermsions(){
        if (GlobalClass.PhoneUID.equals("empty"))
            return;

        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    requestPermissions(new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    }
                return ;
            }
        }

        StartTheService();// init the contact list

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartTheService( );// init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText( this," cannot access it denaied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private  int TimeToSendLocation=500000; //send every 10 minutes
    private  int DistanceToSendLocation=50;
    void StartTheService( ){

        if (MyLocationListener.IsServiceRunning==false) {
            MyLocationListener myLocationListener = new MyLocationListener(this);
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, TimeToSendLocation, DistanceToSendLocation, myLocationListener);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        ReloadLocations();

    }
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Home:
                ReloadLocations();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void ReloadLocations(){
        // loading_ticket
        UsersList.add(0, new UserInfo(null, null,0, 0, 0, "loading_ticket"));
        myadapter.notifyDataSetChanged();

        String url= GlobalClass.WebURL + "UsersPhoneLocations?UserUID=" + GlobalClass.UserUID ;
        new  AsyTaskReloadLocations().execute(url);
    }

    //display news list
    private class MyCustomAdapter extends BaseAdapter {
        public  ArrayList<UserInfo>  listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<UserInfo>  listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();

            final   UserInfo s = listnewsDataAdpater.get(position);

            if(s.DateRecord.equals("loading_ticket")) { //it is loading ticket

                View myView = mInflater.inflate(R.layout.news_ticket_loading, null);


                return myView;
            }
            else if( s.DateRecord.equals("No_new_data")){ //no more news
                View myView = mInflater.inflate(R.layout.news_ticket_no_news, null);
                TextView txtMessage=( TextView)myView.findViewById(R.id.txtMessage);
                txtMessage.setText(getResources().getString(R.string.NoTrackerList));
                return myView;
            }

            else if(s.DateRecord.equals("googleads"))
            { // for google ads

                View myView = mInflater.inflate(R.layout.news_ticket_ads, null);
                AdView mAdView = (AdView) myView.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                return myView;
            }
            else
            {
                View myView = mInflater.inflate(R.layout.news_ticket, null);


            TextView txt_user_name=( TextView)myView.findViewById(R.id.txt_user_name);
            txt_user_name.setText(s.PhoneName);
            TextView txt_news_date=( TextView)myView.findViewById(R.id.txt_news_date);
            txt_news_date.setText(  s.DateRecord);
            TextView txt_progress=( TextView)myView.findViewById(R.id.txt_progress);
            txt_progress.setText(String.valueOf( "(" + s.BatteryLevel +"%)"));
            ProgressBar pbBatteryLevel=(ProgressBar)myView.findViewById(R.id.pbBatteryLevel);
            pbBatteryLevel.setProgress( s.BatteryLevel);
            //pbBatteryLevel.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            if (s.BatteryLevel<30)
            pbBatteryLevel.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            if (s.BatteryLevel>30 && s.BatteryLevel<70)
                pbBatteryLevel.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
            if (s.BatteryLevel>70)
                pbBatteryLevel.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);


            WebView wv_map=(WebView)myView.findViewById(R.id.wv_map);
            //user map
            //https://developers.google.com/maps/documentation/static-maps/intro
            String url="https://maps.googleapis.com/maps/api/staticmap?" +
                    "center="+ String.valueOf( s.Latitude) +","+ String.valueOf( s.Longitude) +
                    "&zoom=15&size=600x200&" +
                    "maptype=roadmap&"+
                    "&markers=color:red%7Clabel:"+ String.valueOf( s.PhoneName.charAt(0))
                     +"%7C"+ String.valueOf( s.Latitude) +","+ String.valueOf( s.Longitude) +
                    "&key="+ getResources().getString(R.string.Map_API_Key);
            wv_map.loadUrl(url);


            ImageView iv_route= (ImageView)myView.findViewById(R.id.iv_route);
            iv_route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Operations settingSaved=new Operations(getApplicationContext());
                    Location lastloc=settingSaved.getLocation();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+lastloc.getLatitude()+"," + lastloc.getLongitude() + "&daddr=" + s.Latitude + "," + s.Longitude));
                    startActivity(i);
                }
            });


                ImageView iv_history= (ImageView)myView.findViewById(R.id.iv_history);
                iv_history.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                        i.putExtra("PhoneUID",s.PhoneUID);
                        startActivity(i);
                    }
                });

            return myView;
            }
        }

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
                    }
                    UsersList.add(1, new UserInfo(null, null,0, 0, 0, "googleads"));

                }
                else {

                    // loading_ticket
                    UsersList.add(0, new UserInfo(null, null,0, 0, 0, "No_new_data"));
                }
                myadapter.notifyDataSetChanged();




            }
            catch (Exception ex){}

        }
        protected void onPostExecute(String  result){

        }




    }




}
