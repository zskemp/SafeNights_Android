package apoorvazachmobileapps.safenights;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;


public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView display;
    private HashMap<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username   = (EditText)findViewById(R.id.username);
        password   = (EditText)findViewById(R.id.password);
        display = (TextView)findViewById(R.id.textview);

    }

    public void getStarted(View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        String uname = username.getText().toString();
        String pword = password.getText().toString();


        Call<User> call = apiService.signin(uname, pword);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                    User u  = response.body();

                    display = (TextView) findViewById(R.id.textview);
                    display.setText(u.getPassed());

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Log error here since request failed
                Log.e("LousList", t.toString());
            }
        });
//        Intent intent = new Intent(this, GetStarted.class);
//        startActivity(intent);
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
