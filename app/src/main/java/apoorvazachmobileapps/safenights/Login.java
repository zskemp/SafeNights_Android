package apoorvazachmobileapps.safenights;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username   = (EditText)findViewById(R.id.username);
        password   = (EditText)findViewById(R.id.password);
    }

    public void Login (View view){
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        final String uname = username.getText().toString();
        final String pword = password.getText().toString();


        Call<User> call = apiService.signin(uname, pword);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u  = response.body();
                if(u.getPassed().equals("y")){
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username", uname);
                    editor.putString("password", pword);
                    editor.commit();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect Credentials!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Log error here since request failed
                Log.e("LousList", t.toString());
            }
        });
    }
}
