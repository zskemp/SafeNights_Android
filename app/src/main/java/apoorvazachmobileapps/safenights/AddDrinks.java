package apoorvazachmobileapps.safenights;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDrinks extends AppCompatActivity {
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private SeekBar seekBar;
    private DatePicker datePicker;
    private TextView moneycount;
    private Date date;
    private DatePickerDialog datePickerDialog;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int money;
    private Calendar dateSelected;

    NumberPicker beer;
    NumberPicker wine;
    NumberPicker liquor;
    NumberPicker shots;
    private HoloCircleSeekBar beerPicker;
    private HoloCircleSeekBar winePicker;
    private HoloCircleSeekBar shotPicker;
    private HoloCircleSeekBar liquorPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drinks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        money = 0;

        beerPicker = (HoloCircleSeekBar) findViewById(R.id.beerPicker);
        liquorPicker = (HoloCircleSeekBar) findViewById(R.id.liquorPicker);
        winePicker = (HoloCircleSeekBar) findViewById(R.id.winePicker);
        shotPicker = (HoloCircleSeekBar) findViewById(R.id.shotPicker);

        date = new GregorianCalendar(mYear, mMonth, mDay).getTime();
        datePicker = (DatePicker) findViewById(R.id.datepicker);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        moneycount = (TextView) findViewById(R.id.moneycount);

        /** Money Seek Bar Logic **/
        moneycount.setText("Money Spent: $" + seekBar.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                moneycount.setText("Money Spent: $" + progress);
                money = progress;
            }
        });

        datePicker.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener(){
            @Override
            public void onDateChanged(DatePicker v, int year, int month, int day) {
                mYear = year;
                mMonth = month;
                mDay = day;
                date = new GregorianCalendar(mYear, mMonth, mDay).getTime();
            }
        });


    }

    public void callAddDrinksAPI(View view) {
        SafeNightsAPIInterface apiService = SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");

        SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd");
        String mday = sdfr.format(date);

        int mbeer = beerPicker.getValue();
        int mwine = winePicker.getValue();
        int mshots = shotPicker.getValue();
        int mliquor = liquorPicker.getValue();
        int Mmoney = money;

        Call<User> call = apiService.adddrinks(username, password, mday, mbeer, mwine, mshots, mliquor, Mmoney);
        Log.i("u", username + password + mday + mbeer + mwine + mshots + mliquor + Mmoney);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u = response.body();
                if (u.getPassed().equals("y")) {
                    //bring them to home page, let them know a problem
                    Intent intent = new Intent(AddDrinks.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    //return them to the page with an error
                    Toast.makeText(getApplicationContext(), "There has been a problem adding your night of drinking\n Please use correct formatting and check login credentials", Toast.LENGTH_LONG).show();
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
