package apoorvazachmobileapps.safenights;

import android.content.Intent;
import android.graphics.Typeface;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appname = (TextView)findViewById(R.id.appname);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/angelina.TTF");
        appname.setTypeface(tf);
        getStarted = (Button)findViewById(R.id.getStarted);
        addDrinks = (Button)findViewById(R.id.addDrinks);
        History = (Button)findViewById(R.id.History);
        lastNight = (Button)findViewById(R.id.lastNight);
        getStarted.setTypeface(tf);
        addDrinks.setTypeface(tf);
        History.setTypeface(tf);

        lastNight.setTypeface(tf);





    }

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
