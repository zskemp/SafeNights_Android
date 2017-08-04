package apoorvazachmobileapps.safenights;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.hitomi.cmlibrary.CircleMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GetStarted extends Fragment {
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private boolean started;
    private CharSequence[] a;
    CircleMenu circleMenu;
    private Button mStartStopButton;
    private Button mLocationsButton;
    private Button mContactButton;
    //String for emergency contact's number
    private String contactNumber;
    //String for emergency contact's name
    private String contactName;
    //Title Text - "Start Adventure!". Unused in Java
    private TextView startstop;

    //Logical private fields
    private Boolean locationWasSet;
    private Boolean nameWasSet;
    private static View rootview;
    private String locationAddress;
    Set<String> h;
    private ArrayList mSelectedItems; // Where we track the selected items
    private SupportMapFragment placepickerFragment;

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        boolean test  = started;
        String saveLocation = locationAddress;
        String saveNumber = contactNumber;
        String saveName = contactName;

        savedState.putBoolean("test", test);
        savedState.putBoolean("nameSet", nameWasSet);
        savedState.putBoolean("locationSet", locationWasSet);
        //TODO: Figure out what to do with this
        savedState.putString("location", saveLocation);
        savedState.putString("number", saveNumber);
        savedState.putString("name", saveName);
    }

    public static GetStarted newInstance() {
        GetStarted fragment = new GetStarted();
        return fragment;
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
        if (rootview != null) {
            ViewGroup parent = (ViewGroup) rootview.getParent();
            if (parent != null)
                parent.removeView(rootview);
        }
        try {
            rootview = inflater.inflate(R.layout.activity_get_started, container, false);
        } catch (InflateException e) {
            Toast.makeText(getActivity(), "An error occured loading this screen. Please try again.", Toast.LENGTH_SHORT);
        }

        nameWasSet = false;
        locationWasSet = false;
        contactNumber = "";
        contactName = "";
        mSelectedItems = new ArrayList();


        //For remembering saved user locations
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
        h = settings.getStringSet("locations", new HashSet<String>());
        if(h.size() == 0) {
            SharedPreferences.Editor editor = settings.edit();
            h.add("I'm Feeling Lucky ;)");
            editor.putStringSet("locations", h);
            editor.commit();
        }
        a = h.toArray(new CharSequence[h.size()]);

        mStartStopButton = (Button)rootview.findViewById(R.id.start);
        mStartStopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Start manager for check if location currently on
                final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

                // Check to make sure they have given proper permissions!
                if ((ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);

                } else if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    buildAlertMessageNoGps();
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] { android.Manifest.permission.SEND_SMS }, 1);
                    if((ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED)) {
                        mStartStopButton.setEnabled(false);
                        callStartNightAPI(rootview);
                    }
                }
            }
        });
        mContactButton = (Button) rootview.findViewById(R.id.contactName);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                ActivityCompat.requestPermissions(getActivity(), new String[] { android.Manifest.permission.READ_CONTACTS }, 1);
                if((ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED)) {
                    pickContact(v);
                }
            }
        });
        mLocationsButton = (Button)rootview.findViewById(R.id.title);
        mLocationsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pickLocation(rootview);
            }
        });

        startstop = (TextView)rootview.findViewById(R.id.startstop);
        //Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Arciform.otf");
        //startstop.setTypeface(tf);

        //Search for places logic
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.getView().setFocusable(true);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mLocationsButton.setText("" + place.getAddress());
                locationAddress = ("" + place.getAddress());
                a  = Arrays.copyOf(a, a.length + 1);
                a[a.length - 1] = place.getAddress();
                locationWasSet = true;

                final SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                h.add(locationAddress);
                editor.putStringSet("locations", h);
                editor.commit();
            }

            @Override
            public void onError(Status status) {
                Log.i("hi", "An error occurred: " + status);
            }
        });

        if (savedInstanceState != null) {
            locationAddress = savedInstanceState.getString("location");
            contactName = savedInstanceState.getString("name");
            contactNumber = savedInstanceState.getString("number");
            started = savedInstanceState.getBoolean("test");
            if (started) {
                started = true;
                startstop.setText("Adventure Underway!");
                mStartStopButton.setText("Finish");
            }
            nameWasSet = savedInstanceState.getBoolean("nameSet");
            if (nameWasSet){
                mContactButton.setText(contactName);
            }
            locationWasSet = savedInstanceState.getBoolean("locationSet");
            if (locationWasSet){
                mLocationsButton.setText(locationAddress);
            }
        }
        started = isMyServiceRunning(TrackingActivity.class);
        if(started){
            mStartStopButton.setText("Finish");
            startstop.setText("Adventure Underway!");

            mLocationsButton.setText(settings.getString("nightLocation", ""));
            //contactEmail.setText(settings.getString("nightEmail",""));
            mContactButton.setText(settings.getString("nightName",""));
        }
        return rootview;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Dialog pop-up for picking from saved locations
    public void pickLocation(final View view) {
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
        h = settings.getStringSet("locations", new HashSet<String>());
        a = h.toArray(new CharSequence[h.size()]);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a location")
                .setSingleChoiceItems(a, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                mSelectedItems.clear();
                                mSelectedItems.add(a[which]);
//                                if (selectedPosition ) {
//                                    // If the user checked the item, add it to the selected items
//                                    mSelectedItems.add(a[which]);
//                                } else if (mSelectedItems.contains(which)) {
//                                    // Else, if the item is already in the array, remove it
//                                    mSelectedItems.remove(Integer.valueOf(which));
//                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User isRunning OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        String p = "";
                        for(Object a : mSelectedItems){
                            p+=a;
                        }
                        if(p.equals("")) {
                            p = "I'm Feeling Lucky ;)";
                        }
                        mLocationsButton.setText("" + p);
                        locationWasSet = true;
                        locationAddress = "" + p;
                        final SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        h.add(locationAddress);
                        editor.putStringSet("locations", h);
                        editor.commit();
                        mSelectedItems.clear();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Below 3 methods are for getting contact information
    public void pickContact(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getActivity().getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE,
                            },
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);
                        contactNumber = number;
                        contactName = getContactName(getContext(), number);
                        mContactButton.setText(contactName);
                        nameWasSet = true;
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    //Method to restart the fragment UI and some of the logic without actually restarting Fragment
    public void restartFragment() {
        startstop.setText("Start Adventure!");
        mLocationsButton.setText("My Strongholds");
        mContactButton.setText("My Guardian Angels");
        mStartStopButton.setText("SET OFF");

        nameWasSet = false;
        locationWasSet = false;
        contactNumber = "";
        contactName = "";
        mSelectedItems.clear();
    }


    public void callStartNightAPI(View view){
        if (!started) {
            if(locationAddress == null || (contactNumber.equals(""))){
                Toast.makeText(getActivity(), "Please fill out all fields!", Toast.LENGTH_SHORT).show();
                mStartStopButton.setEnabled(true);
            } else {
                SafeNightsAPIInterface apiService =
                        SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);
                final SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                final String username = settings.getString("username", "");
                String password = settings.getString("password", "");
                Call<User> call = apiService.startnight(username, password);
                //Log.i("u", username + password);


                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        mStartStopButton.setEnabled(true);

                        User u = response.body();
                        if (u.getPassed().equals("n")) {
                            //bring them to home page, let them know a problem
                            Toast.makeText(getActivity(), "There has been a problem starting your night! Please try again", Toast.LENGTH_LONG).show();
                        } else {
                            //Get the adventureID
                            String adventureID = u.getPassed();
                            //Update preferences
                            SharedPreferences.Editor editor = settings.edit();
                            String uniqueID = u.getPassed();
                            editor.putString("id", uniqueID);
                            editor.putString("nightLocation", locationAddress);
                            editor.putString("nightName", contactName);
                            editor.commit();
                            //Create intent and start service
                            Intent intent = new Intent(getActivity(), TrackingActivity.class);
                            intent.putExtra("location", locationAddress);
                            intent.putExtra("pNum", contactNumber);
                            intent.putExtra("cName", contactName);
                            intent.putExtra("username", username);
                            intent.putExtra("adventureID", adventureID);
                            started = true;
                            mStartStopButton.setText("Finish");
                            startstop.setText("Adventure Underway!");
                            getActivity().startService(intent);
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        mStartStopButton.setEnabled(true);

                        // Log error here since request failed
                        Log.e("API Call:", t.toString());
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Your night has finished!", Toast.LENGTH_LONG).show();
//                stopService(new Intent(GetStarted.this, TrackingActivity.class));
            started = false;
            mStartStopButton.setEnabled(true);
            Intent intent = new Intent(getActivity(), TrackingActivity.class);
            //ToDo: Is this really necessary. Should look at how this works
            intent.putExtra("isRunning", true);
            mStartStopButton.setText("SET OFF");
            getActivity().stopService(intent);
            //Resets the logic and UI for the fragment
            restartFragment();
        }
    }
}

