package apoorvazachmobileapps.safenights;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import apoorvazachmobileapps.safenights.LocationHistory.Fields;
import apoorvazachmobileapps.safenights.LocationHistory.Locationtable;
import apoorvazachmobileapps.safenights.LocationHistory.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastNight extends AppCompatActivity implements OnMapReadyCallback {

    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private ArrayList<Fields> locations = new ArrayList<Fields>();
    private ArrayList<String> times;
    private ArrayList<Marker> markers;
    private GoogleMap mMap;
    private ListView listview;

    //Pick the colors for the markers (if more than 8 need to add change in logic below)
    private String[] colors = {"#0ABFBC", "#480048", "#5f2c82", "#658B92", "#837A84", "#A16976", "#EC6F66", "#DE465A", "#FC354C"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_night);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Map fragment setup occurs in callLastNightAPI call-
        //Needs the data to set up so need to call after data arrives
        listview = (ListView) findViewById(R.id.listview);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

//        final ArrayList<String> list = new ArrayList<String>();
//        for (int i = 0; i < values.length; ++i) {
//            list.add(values[i]);
//        }
//        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.timeline, R.id.textView1, list);
//        listview.setAdapter(adapter);
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
//                        .show();
//            }
//        });

        View v = this.findViewById(android.R.id.content);
        //Note: We add to the nights array list in this method call
        callLastNightAPI(v);
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
        //Need both because to display has to be string, and to get the marker we need marker.
        //Was lazy and didn't want to make a custom object
        times = new ArrayList<String>();
        markers = new ArrayList<Marker>();

        //Style the map
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e("LastNightMap", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("LastNightMap", "Can't find style. Error: ", e);
        }

        //Contour line
        PolylineOptions line= new PolylineOptions();

        LatLng loc = new LatLng(-34, 151);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //Go through locations and add a new marker
        for(int i = 0; i < locations.size(); i++) {
            Fields l = locations.get(i);
            //Just a little math to get a gradient of colors
            int colorNum = (int)Math.ceil(((double)i/(double)locations.size())*8);

            //Get the time in correct format
            Date date;
            String time;
            //ToDo: Make sure this works with other dates. I don't think this works locally (aka only east coast)
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            parser.setTimeZone(TimeZone.getTimeZone("CEST"));
            try {
                date = parser.parse(l.getTime());
                String formattedTime = formatter.format(date);
                time = formattedTime;
            } catch (ParseException e) {
                time = l.getTime();
                e.printStackTrace();
            }

            //Get location place
            Double lat = Double.parseDouble(l.getXcord());
            Double lon = Double.parseDouble(l.getYcord());
            List<Address> addresses = null;
            String address;
            try {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                address = addresses.get(0).getAddressLine(0);
            } catch (IOException e) {
                address = "Lat: " + l.getXcord() + " Lon: " + l.getYcord();
                e.printStackTrace();
            }

            loc = new LatLng(lat, lon);
            Marker mLoc = mMap.addMarker(new MarkerOptions().position(loc).title(time).snippet(address).icon(getMarkerIcon(colors[colorNum])));
            //mLoc.setTag(i);

            //Add to timeline
            times.add(time);
            markers.add(mLoc);

            //Add point to drawing line
            line.add(loc);
        }

        //Set up timeline
        //final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.timeline, R.id.textView1, times);
        //Uncommenting line above still works, this allows smaller amount of uploads to tak
        final TimelineAdapter adapter = new TimelineAdapter(this, times);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(markers.get(position).getPosition()));
            }
        });

        //Color Line
        line.width(5).color(Color.RED);
        mMap.addPolyline(line);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12F));

    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void callLastNightAPI (View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");
        String id = settings.getString("id", "");
        Log.i("Id", id);

        Call<Location> call = apiService.getnight(username, password, id);


        call.enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                Location table  = response.body();
                //Parse response.body() and add to nights
                if(table != null) {
                    for(int i = 0; i < table.getLocationtable().size(); i++) {
                        Locationtable trial = table.getLocationtable().get(i);
                        Fields fields = trial.getFields();
                        locations.add(fields);
                    }

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(LastNight.this);
                    //For testing purposes
                }
                else {
                    Toast.makeText(getApplicationContext(), "You have no data for last night! Please check the website or begin a new night", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Location> call, Throwable t) {
                // Log error here since request failed
                Log.e("API Call:", t.toString());
            }
        });
    }

}
