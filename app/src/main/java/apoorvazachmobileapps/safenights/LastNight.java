package apoorvazachmobileapps.safenights;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wang.avi.AVLoadingIndicatorView;

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

public class LastNight extends Fragment {

    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private static View rootview;

    private ArrayList<Fields> locations = new ArrayList<Fields>();
    private ArrayList<String> times;
    private ArrayList<Marker> markers;
    private GoogleMap mMap;
    MapView mMapView;
    private ListView listview;

    //Pick the colors for the markers (if more than 8 need to add change in logic below)
    private String[] colors = {"#7C4799", "#A94991", "#CE1F82", "#658B92", "#837A84", "#DC216C", "#EC6F66", "#DE465A", "#FC354C"};

    public static LastNight newInstance() {
        LastNight fragment = new LastNight();
        return fragment;
    }
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putInt("key", 3);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final View rootview = inflater.inflate(R.layout.activity_last_night, container, false);

        //Preventing crashing if switch to different tab while this tries to load
        if (rootview != null) {
            ViewGroup parent = (ViewGroup) rootview.getParent();
            if (parent != null)
                parent.removeView(rootview);
        }
        try {
            rootview = inflater.inflate(R.layout.activity_last_night, container, false);
        } catch (InflateException e) {
            Toast.makeText(getActivity(), "An error occured loading this screen. Please try again.", Toast.LENGTH_SHORT);
        }
        setRetainInstance(true);

        listview = (ListView) rootview.findViewById(R.id.listview);

        mMapView = (MapView) rootview.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Note: We add to the nights array list in this method call
        callLastNightAPI(rootview, savedInstanceState);

        return rootview;
    }

    /**
     * Manipulates the lastnight once available.
     * This callback is triggered when the lastnight is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void setUpMap(GoogleMap googleMap) {
        mMap = googleMap;
        //Need both because to display has to be string, and to get the marker we need marker.
        //Was lazy and didn't want to make a custom object
        times = new ArrayList<String>();
        markers = new ArrayList<Marker>();

        //Style the lastnight
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));
            if (!success) {
                Log.e("LastNightMap", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("LastNightMap", "Can't find style. Error: ", e);
        }

        //Contour line
        PolylineOptions line= new PolylineOptions();

        LatLng loc = new LatLng(-34, 151);

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

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
            Marker mLoc = mMap.addMarker(new MarkerOptions().position(loc).title(address).snippet(time).icon(getMarkerIcon(colors[colorNum])));
//            mLoc.setTag(i);

            //Add to timeline
            times.add(time);
            markers.add(mLoc);

            //Add point to drawing line
            line.add(loc);
        }

        //Set up timeline
        //final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.timeline, R.id.textView1, times);
        //Uncommenting line above still works, this allows smaller amount of uploads to tak
        final TimelineAdapter adapter = new TimelineAdapter(getActivity(), times);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(markers.get(position).getPosition()));
                markers.get(position).showInfoWindow();
            }
        });

        //Color Line
        line.width(6).color(ResourcesCompat.getColor(getResources(), R.color.colorAlcoholLine, null));
        mMap.addPolyline(line);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13.5F));

    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void callLastNightAPI (final View view, final Bundle savedInstanceState) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
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

                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap mMap) {
                            setUpMap(mMap);
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "You have no data for last night! Please check the website or begin a new night", Toast.LENGTH_LONG).show();
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
