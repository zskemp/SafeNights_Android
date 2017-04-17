package apoorvazachmobileapps.safenights;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
    private Button StartStopButton;
    private Button title;
    private TextView contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        StartStopButton = (Button)findViewById(R.id.start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Set<String> h = settings.getStringSet("locations", new HashSet<String>());
        a = h.toArray(new CharSequence[h.size()]);
        started = false;
        contactNumber = (TextView)findViewById(R.id.contactnumber);
        title = (Button)findViewById(R.id.title);
    }

    public void pickLocation(final View view) {

        final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
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
                        title.setText(p);
                    }
                }).setNeutralButton("Add a new location", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addNewLocation(view);
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

    public void addNewLocation(View view){
        final EditText address = new EditText(GetStarted.this);
        final EditText city = new EditText(GetStarted.this);
        final EditText state = new EditText(GetStarted.this);
        final EditText zip = new EditText(GetStarted.this);

        TextView addr = new TextView(GetStarted.this);
        TextView cityy = new TextView(GetStarted.this);
        TextView statee = new TextView(GetStarted.this);
        TextView zipp = new TextView(GetStarted.this);

        addr.setText("Address");
        cityy.setText("City");
        statee.setText("State");
        zipp.setText("Zip");

        LinearLayout lp = new LinearLayout(this);
        lp.setOrientation(LinearLayout.VERTICAL);
        lp.addView(addr);
        lp.addView(address);
        lp.addView(cityy);
        lp.addView(city);
        lp.addView(statee);
        lp.addView(state);
        lp.addView(zipp);
        lp.addView(zip);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(lp);
        builder.setTitle("Add a new location")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected

                // Set the action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        String p = ""+address.getText() + " " + city.getText() + " " + state.getText() + " " + zip.getText();
                        a = Arrays.copyOf(a, a.length+1);
                        a[a.length -1] = p;
                        title.setText(p);
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        String[] locations = new String[a.length];
                        int i=0;
                        for(CharSequence ch: a){
                            locations[i++] = ch.toString();
                        }
                        Set<String> mySet = new HashSet<String>(Arrays.asList(locations));
                        editor.putStringSet("locations", mySet);
                        editor.commit();                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void pickContact(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
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
                        contactNumber.setText(number);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }


    public void callStartNightAPI(View view){
        if (!started) {
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
                        editor.commit();
                        Intent intent = new Intent(GetStarted.this, TrackingActivity.class);
                        intent.putExtra("location", title.getText());
                        intent.putExtra("pNum", contactNumber.getText());
                        started = true;
                        StartStopButton.setText("Stop Night");
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
        else {
            Toast.makeText(getApplicationContext(), "Your night has finished!", Toast.LENGTH_LONG).show();
            started = false;
            Intent intent = new Intent(GetStarted.this, MainActivity.class);
            StartStopButton.setText("Start Night");
            stopService(intent);
        }
    }
}
