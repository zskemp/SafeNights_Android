package apoorvazachmobileapps.safenights;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class testwebservice extends AppCompatActivity {

    //public static final String BASE_URL = "http://stardock.cs.virginia.edu/louslist/Courses/view/";

    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    //Add something about your DatePicker thingy :)
    private EditText mBeer;
    private EditText mWine;
    private EditText mShots;
    private EditText mLiquor;
    private EditText mMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testwebservice);

//        mBeer   = (EditText)findViewById(R.id.beer);
//        mWine   = (EditText)findViewById(R.id.wine);
//        mShots   = (EditText)findViewById(R.id.shots);
//        mLiquor   = (EditText)findViewById(R.id.liquor);
//        mMoney   = (EditText)findViewById(R.id.money);
    }

//    public void callAddDrinksAPI(View view) {
//        SafeNightsAPIInterface apiService =
//                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);
//
//        //Get the strings you need for the api
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        String username = settings.getString("username", "");
//        String password = settings.getString("password", "");
//        Date day = new Date();
//        Double beer = Double.parseDouble(mBeer.getText().toString());
//        Double wine = Double.parseDouble(mWine.getText().toString());
//        Double shots = Double.parseDouble(mShots.getText().toString());
//        Double liquor = Double.parseDouble(mLiquor.getText().toString());
//        int money = Integer.parseInt(mMoney.getText().toString());
//
//        Call<User> call = apiService.adddrinks(username, password, day, beer, wine, shots, liquor, money);
//
//
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                User u  = response.body();
//                if(u.getPassed().equals('y')){
//                    //bring them to home page, let them know a problem
//                    Intent intent = new Intent(AddDrinks.this, MainActivity.class);
//                    startActivity(intent);
//                }
//                else {
//                    //return them to the page with an error
//                    Toast.makeText(getApplicationContext(), "There has been a problem adding your night of drinking\n Please use correct formatting and check login credentials", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                // Log error here since request failed
//                Log.e("API Call:", t.toString());
//            }
//        });
//    }
}
