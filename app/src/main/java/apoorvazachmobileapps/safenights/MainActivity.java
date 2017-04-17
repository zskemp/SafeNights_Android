package apoorvazachmobileapps.safenights;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {

    private TextView appname;
    Button getStarted;
    Button addDrinks;
    Button History;
    Button lastNight;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appname = (TextView)findViewById(R.id.appname);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Arciform.otf");
        appname.setTypeface(tf);

        getStarted = (Button)findViewById(R.id.getStarted);
        addDrinks = (Button)findViewById(R.id.addDrinks);
        History = (Button)findViewById(R.id.History);
        lastNight = (Button)findViewById(R.id.lastNight);

        getStarted.setTypeface(tf);
        addDrinks.setTypeface(tf);
        History.setTypeface(tf);
        lastNight.setTypeface(tf);

        /** Permissions **/
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);

    }

    //Four Main Buttons
    public void getStarted(View view) {
        Intent intent = new Intent(this, GetStarted.class);
        startActivity(intent);
    }
    public void addDrinks(View view) {
        Intent intent = new Intent(this, AddDrinks.class);
        startActivity(intent);
    }
    public void History(View view) {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }
    public void lastNight(View view) {
        Intent intent = new Intent(this, LastNight.class);
        startActivity(intent);
    }
    public void testwebservice(View view) {
        Intent intent = new Intent(this, testwebservice.class);
        startActivity(intent);
    }
}
