package apoorvazachmobileapps.safenights;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import apoorvazachmobileapps.safenights.LocationHistory.Fields;
import apoorvazachmobileapps.safenights.LocationHistory.LocTable;
import apoorvazachmobileapps.safenights.LocationHistory.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastNight extends AppCompatActivity {

    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private ArrayList<Fields> locations = new ArrayList<Fields>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_night);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View v = this.findViewById(android.R.id.content);
        //Note: We add to the nights array list in this method call
        callLastNightAPI(v);
    }

    public void callLastNightAPI (View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");
        String id = settings.getString("id", "");

        Call<Location> call = apiService.getnight(username, password, id);


        call.enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                Location table  = response.body();
                //Parse response.body() and add to nights
                for(int i = 0; i < table.getLocTable().size(); i++) {
                    LocTable trial = table.getLocTable().get(i);
                    Fields fields = trial.getFields();
                    locations.add(fields);
                }
                //For testing purposes
                String courseDisplay = "";
                for(Fields s : locations) {
                    Log.d("Field", "Received: " + s.getAdventureID());
                    courseDisplay += s + "adventureID:" + s.getAdventureID() + "time:" + s.getTime() +  "\n" +  "x:" + s.getXcord() + "    y:" + s.getYcord() +  "\n" + "\n";
                }
                TextView display = (TextView)findViewById(R.id.textview);
                display.setText(courseDisplay);
            }

            @Override
            public void onFailure(Call<Location> call, Throwable t) {
                // Log error here since request failed
                Log.e("API Call:", t.toString());
            }
        });
    }

}
