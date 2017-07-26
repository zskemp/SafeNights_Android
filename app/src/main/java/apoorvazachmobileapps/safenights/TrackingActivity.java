package apoorvazachmobileapps.safenights;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
    private String fname;
    private String lname;
    private String cName;
    private String adventureID;
    private String username;
    private Handler handler = new Handler();
    private Timer timer;
    private TimerTask hourlyTask;
    int counter;

    SensorManager sensorManager;
    private Sensor mAccelerometer;
    private static final float SHAKE_THRESHOLD = 6.00f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 200;

    //Current params
    Double currentLat;
    Double currentLon;
    //Temp params for location delay
    Double tempLat;
    Double tempLon;
    //FinalDest params
    double latitude;
    double longitude;
    boolean feelingLucky;
    boolean notifiedLucky;
    boolean recentlyMoved;
    boolean tempMoved;
    boolean isRunning;

    public static final String PREFS_NAME = "CoreSkillsPrefsFile";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null ) {
            Toast.makeText(this, "There has been an error starting the service! Please Restart and Try Again.", Toast.LENGTH_SHORT).show();
        } else {
            intent.hasExtra("isRunning");
            if (isRunning) {
                stopSelf();
            } else {
                counter = 0;
                //ToDo: Delete this
                Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.
                        tempLat = location.getLatitude();
                        tempLon = location.getLongitude();
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };

                // Register the listener with the Location Manager to receive location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                final SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
                fname = settings.getString("firstname", "");
                lname = settings.getString("lastname", "");
                userLocation = intent.getExtras().getString("location");
                phone_number = intent.getExtras().getString("pNum");
                cName = intent.getExtras().getString("cName");
                adventureID = intent.getExtras().getString("adventureID");
                username = intent.getExtras().getString("username");
                isRunning = intent.getBooleanExtra("isRunning", false);

                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                recentlyMoved = false;
                feelingLucky = false;
                notifiedLucky = false;

                //Gets coordinates from Address String
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = new ArrayList<Address>();
                if(userLocation.equals("I'm Feeling Lucky ;)")) {
                    feelingLucky = true;
                } else {
                    try {
                        addresses = geocoder.getFromLocationName(userLocation, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0) {
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    } else {
                        Toast.makeText(this, "The address didn't parse correctly!", Toast.LENGTH_SHORT);
                    }
                }

                //Timer task to run every 10 minutes
                timer = new Timer();
                final double[] lonArray = {0, 0, 0, 0};
                final double[] latArray = {0, 0, 0, 0};
                hourlyTask = new TimerTask() {
                    @Override
                    public void run() {
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        ContextCompat.checkSelfPermission(TrackingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
                        ContextCompat.checkSelfPermission(TrackingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //If location isn't null, we can update the current coordinates to where we actually are.
                        //Otherwise, we have to use lastKnownLocation
                        if (location != null) {
                            //ToDO: Look into tempLat and logic to see why crashes on first try everytime
                            //Note: This is the one updating everytime... Getting the last GPS sucks at working :(
                            if (tempLat != null) {
                                currentLat = tempLat;
                                currentLon = tempLon;
                                currentLon = (double)Math.round(currentLon * 10000d) / 10000d;
                                currentLat = (double)Math.round(currentLat * 10000d) / 10000d;
                            } else {
                                currentLon = BigDecimal.valueOf(location.getLongitude()).doubleValue();
                                currentLat = BigDecimal.valueOf(location.getLatitude())
                                        .doubleValue();
                                currentLon = (double)Math.round(currentLon * 10000d) / 10000d;
                                currentLat = (double)Math.round(currentLat * 10000d) / 10000d;
                            }
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Something went wrong getting your location! Trying again in 60 seconds... \n Please make sure your GPS is turned on.", Toast.LENGTH_SHORT).show();
                                }
                            });
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

//                        Log.i("thiscounter", "" + counter);
//                        Log.i("hi", "" + recentlyMoved);
                        //If it's between 2-6am, and the latitude and longitude is the same for all spots, send a message
                        //Otherwise, it means they moved locations, so update the positions in the array
                        if (!recentlyMoved && hour >= 2 && hour < 25 && ((latArray[0] == latArray[1] && latArray[0] == latArray[2] && latArray[0] == latArray[3]) ||
                                (lonArray[0] == lonArray[1] && lonArray[0] == lonArray[2] && lonArray[0] == lonArray[3])) && feelingLucky && !notifiedLucky) {
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                String message = "Hey " + cName + ", " + fname + " went out for a " +
                                        "fun night hasn't moved for a while! It seems they were okay with ending up anywhere, " +
                                        "but as their Guardian Angel we wanted you to know where they ended up. Their coordinates were " + currentLat + ", " + currentLon + ".";
                                ArrayList<String> parts = smsManager.divideMessage(message);
                                smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
                                notifiedLucky = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (!recentlyMoved && hour >= 2 && hour < 25 && ((latArray[0] == latArray[1] && latArray[0] == latArray[2] && latArray[0] == latArray[3]) ||
                                (lonArray[0] == lonArray[1] && lonArray[0] == lonArray[2] && lonArray[0] == lonArray[3])) && !feelingLucky &&

                                ((Math.abs(latitude - currentLat) > 0.0001) || Math.abs(longitude - currentLon) > 0.0001)) {
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                String message = "Hey " + cName + ", " + fname + " went out for a " +
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
                            Log.i("Iter1", "" + latArray[0] + lonArray[0]);
                            Log.i("Iter2", "" + latArray[1] + lonArray[1]);
                            Log.i("Iter3", "" + latArray[2] + lonArray[2]);
                            Log.i("Iter4", "" + latArray[3] + lonArray[3]);
                        }

                        //If their battery is below 10%, send a warning message
                        if (batteryPct * 100 < 10) {
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                String message = "Hey " + cName + ", " + fname + " went out for a " +
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

                //Set to run every so often (10 min) EDIT TO 10 SECONDS::::
                timer.schedule(hourlyTask, 0l, 1000 * 1 * 10);

                //Send Text to Friend
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    String message = "Hello " + cName + ", " + fname + " " + lname + " went out for a " +
                            "fun night tonight and has entrusted you as their Guardian Angel. We will send you an update if they fail to reach their destination; " +
                            "However, as their Angel you can always head to gentle-badlands-54918.herokuapp.com and login with these credentials to keep a protective watch over your adventurer..." +
                            "\nUsername: " + username + "\nSpecial Password: " + adventureID;
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY_COMPATIBILITY;
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
                    Toast.makeText(getApplication(), "You uploaded another location successfully :)", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplication(), "There has been a problem uploading your location!\nDo you have service?", Toast.LENGTH_LONG).show();
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
            String message = "Hey " + cName + ", " + fname + " went out for a " +
                    "fun night tonight but his tracking app SafeNights just crashed! He said he was going to " +
                    userLocation + ", and his last known location was at " + currentLat + ", " + currentLon + ".";
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //callSendEmailAPI(3);

        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        timer.cancel();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Hey " + cName + ", " + fname + " went out for a " +
                    "fun night tonight and appears to have finished! He said he was going to " +
                    userLocation + ", and his last known location was at " + currentLat + ", " + currentLon + ". " +
                    "If this does not look right you should give your friend a call :)";
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
            //ToDo: You kill this like 3 times... Look into calming down
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
                counter++;
            }
        }
    }

}