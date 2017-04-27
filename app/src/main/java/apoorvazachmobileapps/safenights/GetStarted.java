package apoorvazachmobileapps.safenights;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GetStarted extends AppCompatActivity  {
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private boolean started;
    private CharSequence[] a;
    CircleMenu circleMenu;
    private Button StartStopButton;
    private Button title;
    private String contactNumber;
    private EditText contactName;
    private EditText contactEmail;
    private TextView startstop;
    private TextView locationTitle;
    private TextView contactnumber;
    private String emerContactName;
    private TextView finalLocation;
    private Boolean locationWasSet;
    private Boolean nameWasSet;
    private String locationAddress;
    Set<String> h;
    private ArrayList mSelectedItems; // Where we track the selected items

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        boolean test  = started;
        String saveLocation = locationAddress;
        String saveNumber = contactNumber;
        String saveName = contactName.getText().toString();
        String saveEmail = contactEmail.getText().toString();

        savedState.putBoolean("test", test);
        savedState.putBoolean("nameSet", nameWasSet);
        savedState.putBoolean("locationSet", locationWasSet);
        savedState.putString("email", saveEmail);
        savedState.putString("location", saveLocation);
        savedState.putString("number", saveNumber);
        savedState.putString("name", saveName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameWasSet = false;
        locationWasSet = false;
        contactNumber = "";
        emerContactName = "";
        mSelectedItems = new ArrayList();


        //For remembering saved user locations
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        h = settings.getStringSet("locations", new HashSet<String>());
        a = h.toArray(new CharSequence[h.size()]);

        StartStopButton = (Button)findViewById(R.id.start);
        contactName = (EditText)findViewById(R.id.contactName);
        contactEmail = (EditText)findViewById(R.id.contactEmail);
        title = (Button)findViewById(R.id.title);
        startstop = (TextView)findViewById(R.id.startstop);
        locationTitle = (TextView)findViewById(R.id.locationTitle);
        //contactnumber = (TextView)findViewById(R.id.contactnumber);
        finalLocation = (TextView)findViewById(R.id.finalLocation);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Arciform.otf");
        startstop.setTypeface(tf);

        contactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    contactName.setHint("");
                else
                    contactName.setHint("Enter Name");
            }
        });
        contactEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    contactEmail.setHint("");
                else
                    contactEmail.setHint("Enter Email");
            }
        });

        //Search for places logic
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.getView().setFocusable(true);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                finalLocation.setText("Final Location: " + place.getAddress());
                locationAddress = "" + place.getAddress();
                a  = Arrays.copyOf(a, a.length + 1);
                a[a.length - 1] = place.getAddress();
                locationWasSet = true;

                final SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
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
            contactName.setText(savedInstanceState.getString("name"));
            contactNumber = savedInstanceState.getString("number");
            started = savedInstanceState.getBoolean("test");
            contactEmail.setText(savedInstanceState.getString("email"));

            if (started) {
                started = true;
                startstop.setText("Your Night Is Underway!");
                StartStopButton.setText("Stop Night");
            }

            nameWasSet = savedInstanceState.getBoolean("nameSet");
            if (nameWasSet){
                contactnumber.setText("Contact: " + emerContactName);
            }

            locationWasSet = savedInstanceState.getBoolean("locationSet");
            if (locationWasSet){
                finalLocation.setText("Final Location: " + locationAddress);
            }
        }
        started = isMyServiceRunning(TrackingActivity.class);
        if(started){
            StartStopButton.setText("Stop Night");
            startstop.setText("Your Night Is Underway!");

            finalLocation.setText("Final Location: " + settings.getString("nightLocation", ""));
            contactEmail.setText(settings.getString("nightEmail",""));
            contactName.setText(settings.getString("nightName",""));
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    //Dialog pop-up for picking from saved locations
    public void pickLocation(final View view) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        h = settings.getStringSet("locations", new HashSet<String>());
        a = h.toArray(new CharSequence[h.size()]);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a location")
                .setMultiChoiceItems(a, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(a[which]);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        String p = "";
                        for(Object a : mSelectedItems){
                            p+=a;
                        }
                        finalLocation.setText("Final Location: " + p);
                        locationWasSet = true;
                        locationAddress = "" + p;
                        final SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE,
                            },
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);
                        contactNumber = number;
                        emerContactName = getContactName(getApplicationContext(), number);
                        contactnumber.setText("Contact: " + emerContactName);
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


    public void callStartNightAPI(View view){

        Log.i("beep","sippy");
        if (!started) {
            Log.i("inthe", "bang");
            if(locationAddress == null || (contactEmail.getText().toString().equals(""))||
                    (contactName.getText().toString().equals(""))){
                Toast.makeText(getApplicationContext(), "Please fill out all fields!", Toast.LENGTH_SHORT).show();
            } else {
                SafeNightsAPIInterface apiService =
                        SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);
                final SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
                String username = settings.getString("username", "");
                String password = settings.getString("password", "");
                Call<User> call = apiService.startnight(username, password);
                Log.i("u", username + password);


                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User u = response.body();
                        if (u.getPassed().equals("n")) {
                            //bring them to home page, let them know a problem
                            Toast.makeText(getApplicationContext(), "There has been a problem starting your night! Please try again", Toast.LENGTH_LONG).show();

                        } else {
                            SharedPreferences.Editor editor = settings.edit();
                            String uniqueID = u.getPassed();
                            editor.putString("id", uniqueID);
                            editor.putString("nightLocation", locationAddress);
                            editor.putString("nightName", contactName.getText().toString());
                            editor.putString("nightEmail", contactEmail.getText().toString());
                            editor.commit();
                            Intent intent = new Intent(GetStarted.this, TrackingActivity.class);
                            intent.putExtra("location", locationAddress);
                            intent.putExtra("pNum", contactNumber);
                            intent.putExtra("email", contactEmail.getText().toString());
                            intent.putExtra("cName", contactName.getText().toString());
                            //                        intent.putExtra("cName", emerContactName);
                            started = true;
                            StartStopButton.setText("Stop Night");
                            startstop.setText("Your Night Is Underway!");
                            startService(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Log error here since request failed
                        Log.e("API Call:", t.toString());
                    }
                });
            }
            } else {
                Toast.makeText(getApplicationContext(), "Your night has finished!", Toast.LENGTH_LONG).show();
                stopService(new Intent(GetStarted.this, TrackingActivity.class));
                started = false;
                Intent intent = new Intent(this, TrackingActivity.class);
                intent.putExtra("click", true);
                StartStopButton.setText("Start Night");
                stopService(intent);
                Intent i = new Intent(GetStarted.this, MainActivity.class);
                startActivity(i);

            }
        }
    }

