package apoorvazachmobileapps.safenights;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import android.telephony.SmsManager;
import android.util.Log;

import android.widget.Toast;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingActivity extends Service implements LocationListener, SensorEventListener{

    private String userLocation;
    private String phone_number;
    private String name;
    private String cName;
    private String email;
    private Handler handler = new Handler();
    private Timer timer;
    private TimerTask hourlyTask;

    SensorManager sensorManager;
    private Sensor mAccelerometer;
    private static final float SHAKE_THRESHOLD = 6.25f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;

    //Current params
    Double currentLat;
    Double currentLon;
    //Temp params for location delay
    Double tempLat;
    Double tempLon;
    //FinalDest params
    double latitude;
    double longitude;
    boolean recentlyMoved;
    boolean tempMoved;

    public static final String PREFS_NAME = "CoreSkillsPrefsFile";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                tempLat = location.getLatitude();
                tempLon = location.getLongitude();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        final SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        name = settings.getString("firstname", "");
        userLocation = intent.getExtras().getString("location");
        phone_number = intent.getExtras().getString("pNum");
        cName = intent.getExtras().getString("cName");
        email = intent.getExtras().getString("email");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        recentlyMoved = false;

        //Gets coordinates from Address String
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = new ArrayList<Address>();
        try {
            addresses = geocoder.getFromLocationName(userLocation, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            latitude = addresses.get(0).getLatitude();
            longitude = addresses.get(0).getLongitude();
        } else {
            Toast.makeText(getApplicationContext(), "The address didn't parse correctly!", Toast.LENGTH_SHORT);
        }

        //Timer task to run every 10 minutes
        timer = new Timer();
        final double[] lonArray = {0, 0, 0, 0};
        final double[] latArray = {0, 0, 0, 0};
        hourlyTask = new TimerTask() {
            @Override
            public void run() {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(TrackingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(TrackingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //If location isn't null, we can update the current coordinates to where we actually are.
                //Otherwise, we have to use lastKnownLocation
                if(location!=null) {
                    if(tempLat != null){
                        currentLat = tempLat;
                        currentLon = tempLon;
                    } else {
                        currentLon = BigDecimal.valueOf(location.getLongitude()).doubleValue();
                        currentLat = BigDecimal.valueOf(location.getLatitude())
                                .doubleValue();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong getting your location!", Toast.LENGTH_SHORT);
                }

                //Set the final spot in the array to current location
                latArray[3] = currentLat;
                lonArray[3] = currentLon;

                //Get battery level
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryPct = level / (float) scale;
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);


                //If it's between 2-6am, and the latitude and longitude is the same for all spots, send a message
                //Otherwise, it means they moved locations, so update the positions in the array
                if (!recentlyMoved && hour > 2 && hour < 6 && ((latArray[0] == latArray[1] && latArray[0] == latArray[2] && latArray[0] == latArray[3]) ||
                        (lonArray[0] == lonArray[1] && lonArray[0] == lonArray[2] && lonArray[0] == lonArray[3])) &&
                        ((Math.abs(latitude - currentLat) > 0.0001) || Math.abs(longitude - currentLon) > 0.0001)) {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        String message = "Hey " + cName + ", " + name + " went out for a " +
                                "fun night but didn't reach his final location and hasn't moved for a while! He said he was going to " +
                                userLocation + ", and his last known location was at " + currentLat + ", " + currentLon + ".";
                        ArrayList<String> parts = smsManager.divideMessage(message);
                        smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    latArray[0] = latArray[1];
                    latArray[1] = latArray[2];
                    latArray[2] = latArray[3];
                    latArray[3] = currentLat;
                    lonArray[0] = lonArray[1];
                    lonArray[1] = lonArray[2];
                    lonArray[2] = lonArray[3];
                    lonArray[3] = currentLon;
                }

                //If their battery is below 10%, send a warning message
                if (batteryPct * 100 < 10) {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        String message = "Hey " + cName + ", " + name + " went out for a " +
                                "fun night tonight but his phone battery is almost dead! He said he was going to " +
                                userLocation + ", and his last known location was at " + currentLat + ", " + currentLon + ".";
                        ArrayList<String> parts = smsManager.divideMessage(message);
                        smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //Push location data point
                callAddLocationAPI();
                recentlyMoved = false;
            }
        };

        //Set to run every so often (10 min)
        timer.schedule(hourlyTask, 0l, 1000 * 1 * 05);

        return super.onStartCommand(intent, flags, startId);
    }


    public void callAddLocationAPI() {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");
        String id = settings.getString("id", "");
        Call<User> call = apiService.addlocation(username, password, id, currentLat, currentLon);
        Log.i("u", id + username + password + currentLat + currentLon);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u = response.body();
                if (u.getPassed().equals("y")) {
                    //bring them to home page, let them know a problem
                    Toast.makeText(getApplicationContext(), "You uploaded another location successfully :)", Toast.LENGTH_LONG).show();
                } else {
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


    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        timer.cancel();
        super.onTaskRemoved(rootIntent);
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Hey " + cName + ", " + name + " went out for a " +
                    "fun night tonight but his tracking app SafeNights just crashed! He said he was going to " +
                    userLocation + ", and his last known location was at " + currentLat + ", " + currentLon + ".";
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        timer.cancel();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Hey " + cName + ", " + name + " went out for a " +
                    "fun night tonight but his tracking app SafeNights just crashed! He said he was going to " +
                    userLocation + ", and his last known location was at " + currentLat + ", " + currentLon + ".";
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
            stopSelf();
            Intent i = new Intent(this, TrackingActivity.class);
            stopService(i);
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.stopSelf();
        super.onDestroy();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nothing needs to be added here.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(Math.pow(x, 2) +
                    Math.pow(y, 2) +
                    Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

            if (acceleration > SHAKE_THRESHOLD) {
                recentlyMoved = true;
            }
        }
    }

}