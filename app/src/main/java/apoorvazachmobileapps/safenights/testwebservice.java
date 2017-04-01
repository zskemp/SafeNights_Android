package apoorvazachmobileapps.safenights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class testwebservice extends AppCompatActivity {

    //public static final String BASE_URL = "http://stardock.cs.virginia.edu/louslist/Courses/view/";

    private EditText mUsername;
    private EditText mFname;
    private EditText mLname;
    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testwebservice);

        mUsername   = (EditText)findViewById(R.id.username);
        mFname   = (EditText)findViewById(R.id.fname);
        mLname   = (EditText)findViewById(R.id.lname);
        mEmail   = (EditText)findViewById(R.id.email);
        mPassword   = (EditText)findViewById(R.id.password);
    }

    public void callAPI(View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        //Get the strings you need for the api
        String username = mUsername.getText().toString();
        String fname = mFname.getText().toString();
        String lname = mLname.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        Call<User> call = apiService.signup(username, fname, lname, email, password);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u  = response.body();
                if(u.getPassed().equals('y')){
                    //bring them to login page
                }
                else {
                    //return them to the page with an error
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Log error here since request failed
                Log.e("API Call:", t.toString());
            }
        });
    }
}
