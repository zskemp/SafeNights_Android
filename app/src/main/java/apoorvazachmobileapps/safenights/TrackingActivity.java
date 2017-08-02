package apoorvazachmobileapps.safenights;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingActivity extends Service implements LocationListener {
    //SensorEventListener (also implements this for sensor*******)

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
    boolean notifiedAlready;
//    boolean recentlyMoved;
    boolean tempMoved;
    boolean isRunning;
    boolean nestedTimerRunning;

    //Global parameters for setting timer rates****************
    private static final int retryLocationRate = 10; //in seconds
    private static final int timerRate = 15; //in minutes
    private static final double gpsDifference = 0.0006; //.0006 = 61m difference
    private static final double gpsDistance = 0.0015; //.001 = 111m difference
//    private static final float SHAKE_THRESHOLD = 8.00f; // m/S**2
//    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 200;

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
                Toast.makeText(this, "Starting Your Night...", Toast.LENGTH_SHORT).show();
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

                Log.i("track", userLocation);

//                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//                mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//                sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//                recentlyMoved = false;
                feelingLucky = false;
                notifiedAlready = false;

                //Gets coordinates from Address String
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = new ArrayList<Address>();
                if(userLocation.equals("I'm Feeling Lucky ;)")) {
                    feelingLucky = true;
                } else {
                    //In this method get the userLocation and parse into global latitude and longitude
                    Log.i("loc", userLocation);
                    new DataLongOperationAsynchTask().execute(userLocation);
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
                            if (tempLat != null) {
                                currentLat = tempLat;
                                currentLon = tempLon;
                                currentLon = (double)Math.round(currentLon * 1000d) / 1000d;
                                currentLat = (double)Math.round(currentLat * 1000d) / 1000d;
                            } else {
                                currentLon = BigDecimal.valueOf(location.getLongitude()).doubleValue();
                                currentLat = BigDecimal.valueOf(location.getLatitude())
                                        .doubleValue();
                                currentLon = (double)Math.round(currentLon * 1000d) / 1000d;
                                currentLat = (double)Math.round(currentLat * 1000d) / 1000d;
                            }
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Something went wrong getting your location! Location will be pulled again in 60 seconds. Please be patient :) \nMake sure your GPS is turned on and you have service", Toast.LENGTH_LONG).show();
                                }
                            });
                            //Rerun the hourlyTask run() method in a minute and try to get location again
                            //Note: Logic is such that if it doesn't work again will keep trying every minute instead of every 15 minutes
                            final ExecutorService executor = Executors.newFixedThreadPool(1);
                            executor.submit(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(retryLocationRate * 1000);
                                        hourlyTask.run();
                                    } catch (InterruptedException e) {
                                        Log.i("Catch ExecutorService", "Broken second run. Uh-oh...");
                                    }
                                }
                            });
                            //Return out of run() method. Timer still going, we ran a second timer in retryLocationRate seconds
                            return;
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

                        Log.i("tracklat0:", "" +  (latitude));
                        Log.i("tracklon0:", "" +  (longitude));
                        Log.i("tracklat0:", "" +  (currentLat));
                        Log.i("tracklon0:", "" +  (currentLon));
                        Log.i("tracklat:", "" +  Math.abs(latitude - currentLat));
                        Log.i("tracklon:", "" +  Math.abs(longitude - currentLon));

                        //If it's between 2-7am, and the latitude and longitude is the same for all spots, send a message
                        //Otherwise, it means they moved locations, so update the positions in the array
                        //NOTE: I AM LEAVING OUT ACCELEROMETER FOR FIRST ITERATION
                        if (hour >= 0 && hour < 25 && (((Math.abs(latArray[0] - latArray[1]) < gpsDifference) && (Math.abs(latArray[0] - latArray[2]) < gpsDifference) && (Math.abs(latArray[0] - latArray[3]) < gpsDifference)) ||
                                (((Math.abs(lonArray[0] - lonArray[1]) < gpsDifference) && (Math.abs(lonArray[0] - lonArray[2]) < gpsDifference) && (Math.abs(lonArray[0] - lonArray[3]) < gpsDifference)))) && feelingLucky && !notifiedAlready) {
                            try {
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
                                String address = addresses.get(0).getAddressLine(0);
                                String city = addresses.get(0).getLocality();
                                SmsManager smsManager = SmsManager.getDefault();
                                String message = "Hey " + cName + ", " + fname + " went out for a " +
                                        "fun night hasn't moved for a while! It seems they were okay with ending up anywhere, " +
                                        "but as their Guardian Angel we wanted you to know where they ended up. They were last at " + address + " in " + city + ". (For more information, visit Gabriel's Iris on the website)";
                                ArrayList<String> parts = smsManager.divideMessage(message);
                                smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
                                notifiedAlready = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (hour >= 0 && hour < 25 && (((Math.abs(latArray[0] - latArray[1]) < gpsDifference) && (Math.abs(latArray[0] - latArray[2]) < gpsDifference) && (Math.abs(latArray[0] - latArray[3]) < gpsDifference)) ||
                                (((Math.abs(lonArray[0] - lonArray[1]) < gpsDifference) && (Math.abs(lonArray[0] - lonArray[2]) < gpsDifference) && (Math.abs(lonArray[0] - lonArray[3]) < gpsDifference)))) && !feelingLucky &&
                                ((Math.abs(latitude - currentLat) + Math.abs(longitude - currentLon)) > gpsDistance) && !notifiedAlready) {
                            try {
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
                                String address = addresses.get(0).getAddressLine(0);
                                String city = addresses.get(0).getLocality();
                                SmsManager smsManager = SmsManager.getDefault();
                                String message = "Hey " + cName + ", " + fname + " went out for a " +
                                        "fun night but didn't reach their final location and hasn't moved for a while! They said they were going to " +
                                        userLocation + ", and their last known location was at " + address + " in " + city + ". (For more information, visit Gabriel's Iris on the website)";
                                ArrayList<String> parts = smsManager.divideMessage(message);
                                smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
                                notifiedAlready = true;
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
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
                                String address = addresses.get(0).getAddressLine(0);
                                String city = addresses.get(0).getLocality();
                                SmsManager smsManager = SmsManager.getDefault();
                                String message = "Hey " + cName + ", " + fname + " went out for a " +
                                        "fun night tonight but their phone battery is almost dead! They said they were going to " +
                                        userLocation + ", and their last known location was at " + address + " in " + city + ". (For more information, visit Gabriel's Iris on the website)";
                                ArrayList<String> parts = smsManager.divideMessage(message);
                                smsManager.sendMultipartTextMessage(phone_number, null, parts, null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //Push location data point
                        callAddLocationAPI();
//                        recentlyMoved = false;
                    }
                };

                //Set to run every so often (10 min) EDIT TO 10 SECONDS::::
                //1000ms * MIN/timerRate * 60 should be actual values
                timer.schedule(hourlyTask, 0l, 1000 * timerRate * 1);

                //Send Text to Friend
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    String message = "Hello " + cName + ", " + fname + " " + lname + " went out for a " +
                            "fun night tonight and has entrusted you as their Guardian Angel. We will send you an update if they fail to reach their destination; " +
                            "However, as their Angel you can always head to safe-nights.com and login with these credentials to keep a protective watch over your adventurer..." +
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
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Hey " + cName + ", " + fname + " went out for a " +
                    "fun night tonight but their tracking app SafeNights just crashed! They said they was going to " +
                    userLocation + ", and their last known location was at " + address + " in " + city + ". (For more information, visit Gabriel's Iris on the website)";
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
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Hey " + cName + ", " + fname + " went out for a " +
                    "fun night tonight and appears to have finished! They said they were going to " +
                    userLocation + ", and their last known location was at " + address + " in " + city + ". " +
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

    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            Log.i("params", params[0]);
            try {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address="+params[0]+"&sensor=false");
                Log.d("response",""+response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                latitude = lat;
                longitude = lng;

                Log.d("latitude", "" + lat);
                Log.d("longitude", "" + lng);
            } catch (JSONException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Something went wrong parsing your address. Please restart!!!", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }
        }
    }


    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Nothing needs to be added here.
//    }

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            double acceleration = Math.sqrt(Math.pow(x, 2) +
//                    Math.pow(y, 2) +
//                    Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
//
//            if (acceleration > SHAKE_THRESHOLD) {
//                recentlyMoved = true;
//                counter++;
//            }
//        }
//    }

}