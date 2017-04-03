package apoorvazachmobileapps.safenights;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.security.AccessController.getContext;

public class GetStarted extends AppCompatActivity  {
    private TextView contactNumber;
    private Button title;
    private CharSequence[] a;
    private TextView latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        a = new CharSequence[]{};

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactNumber = (TextView)findViewById(R.id.contactnumber);
        title = (Button)findViewById(R.id.title);
        latitude = (TextView)findViewById(R.id.latlong);


    }
    public void pickLocation(final View view) {


        final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Select a location")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
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
        final EditText input = new EditText(GetStarted.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(input);
        builder.setTitle("Add a new location")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected

                // Set the action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        String p = input.getText().toString();
                        a = Arrays.copyOf(a, a.length+1);
                        a[a.length -1] = p;
                        title.setText(p);
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
                            ContactsContract.CommonDataKinds.Phone.SEARCH_DISPLAY_NAME_KEY
                            },
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);
                        String name = c.getString(2);
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


    public void startNight(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);
        intent.putExtra("location", title.getText());
        intent.putExtra("pNum", contactNumber.getText());
        startActivity(intent);
    }
}
