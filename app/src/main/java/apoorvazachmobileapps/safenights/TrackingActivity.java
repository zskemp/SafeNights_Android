package apoorvazachmobileapps.safenights;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    private static final int GPS_PERMISSION = 1;
    double latitude;
    double longitude;
    private String location;
    private String phone_number;
    private String name;
    TextView latTextView;
    TextView lonTextView;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";

    Double currentLat;
    Double currentLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        name = settings.getString("firstname", "");

        Intent intent = getIntent();
        location = intent.getExtras().getString("location");
        phone_number = intent.getExtras().getString("pNum");
        latTextView = (TextView)findViewById(R.id.latTextView);
        lonTextView = (TextView)findViewById(R.id.lonTextView);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = new ArrayList<Address>();
        try {
            addresses = geocoder.getFromLocationName(location, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses.size() > 0) {
            latitude= addresses.get(0).getLatitude();
            longitude= addresses.get(0).getLongitude();
            latTextView.setText("" + latitude);
            lonTextView.setText("" + longitude);
        }
        final View v = this.findViewById(android.R.id.content);
        Timer timer = new Timer ();
        final double[] lonArray = {0,0,0,0};
        final double[] latArray = {0,0,0,0};
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                // CALL METHOD HERE FOR API pushLocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( TrackingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( TrackingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TrackingActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, GPS_PERMISSION);
                }
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                currentLon = BigDecimal.valueOf(location.getLongitude())
                        .setScale(5, RoundingMode.HALF_UP)
                        .doubleValue();
                currentLat = BigDecimal.valueOf(location.getLatitude())
                        .setScale(5, RoundingMode.HALF_UP)
                        .doubleValue();
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryPct = level / (float)scale;
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                if(hour > 2 && ((latArray[0]==latArray[1] && latArray[0]==latArray[2] && latArray[0]==latArray[3])||
                        (lonArray[0]==lonArray[1]&&lonArray[0]==lonArray[2]&&lonArray[0]==lonArray[3])) &&
                        ((Math.abs(latitude-currentLat)>0.00001)||Math.abs(longitude-currentLon)>0.00001)){
                    sendSMSMessage("location");
                } else {
                    latArray[0] = latArray[1];
                    latArray[1] = latArray[2];
                    latArray[2] = latArray[3];
                    latArray[3] = location.getLatitude();
                    lonArray[0] = lonArray[1];
                    lonArray[1] = lonArray[2];
                    lonArray[2] = lonArray[3];
                    lonArray[3] = location.getLongitude();


                }
                if(batteryPct < 10){
                    sendSMSMessage("battery");
                }
                callAddLocationAPI(v);
            }
        };


        timer.schedule (hourlyTask, 0l, 1000*1*10);



    }

    protected void sendSMSMessage(String scenario) {
        String text = "";
        if(scenario.equals("battery")) {
            text = "Hey, " + name + " went out for a fun night tonight " +
                    "but his phone battery is almost dead! He said he was going to " + location + ", and his last " +
                    "known location was at " + currentLat + ", " + currentLon + ".";
        }
        else if(scenario.equals("destroy")){
            text = "Hey, " + name + " went out for a fun night tonight " +
                    "but our app just got destroyed! He said he was going to " + location + ", and his last " +
                    "known location was at " + currentLat + ", " + currentLon + ".";
        }
        else if(scenario.equals("location")){
            text = "Hey, " + name + " went out for a fun night tonight " +
                    "but he hasn't moved around for a while! He said he was going to " + location + ", and his last " +
                    "known location was at " + currentLat + ", " + currentLon + ".";
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone_number, null, text, null, null);
//            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    public void callAddLocationAPI(View view){
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");
        String id = settings.getString("id", "");
        Call<User> call = apiService.addlocation(username, password, id, currentLat, currentLon);
        Log.i("u", id + username + password +  currentLat + currentLon);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u  = response.body();
                if(u.getPassed().equals("y")){
                    //bring them to home page, let them know a problem
                    Toast.makeText(getApplicationContext(), "You uploaded another location successfully :)", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "There has been a problem uploading your location!\nDo you have service?", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Log error here since request failed
                Log.e("API Call:", t.toString());
            }
        });
    }


    public void startGPS(View view) {

        // Here is the code to handle permissions - you should not need to edit this.
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, GPS_PERMISSION);
        }

        // Add code here to register the listener with the Location Manager to receive location updates
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


    }


    @Override
    public void onLocationChanged(Location location) {
//        // Add code here to do stuff when the location changes
//        currentLon = location.getLongitude();
//        currentLat = location.getLatitude();
//        //Change the views
//        lonTextView.setText(currentLon.toString());
//        latTextView.setText(currentLat.toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    @Override
    public void onDestroy() {
        sendSMSMessage("destroy");
        super.onDestroy();
    }
}