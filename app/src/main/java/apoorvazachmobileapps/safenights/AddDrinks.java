package apoorvazachmobileapps.safenights;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private SeekBar seekBar;
    private EditText datePicker;
    private TextView moneycount;
    private int money;
    private Date date;
    private DatePickerDialog datePickerDialog;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private int mYear;
    private int mMonth;
    private int mDay;

    private Calendar dateSelected;
    NumberPicker beer;
    NumberPicker wine;
    NumberPicker liquor;
    NumberPicker shots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drinks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        money = 0;
        Calendar mcurrentDate=Calendar.getInstance();
        mYear=mcurrentDate.get(Calendar.YEAR);
        mMonth=mcurrentDate.get(Calendar.MONTH);
        mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

//        Calendar mcurrentDate=Calendar.getInstance();
//        int mYear=mcurrentDate.get(Calendar.YEAR);
//        int mMonth=mcurrentDate.get(Calendar.MONTH);
//        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog mDatePicker=new DatePickerDialog(AddDrinks.this, new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
//                // TODO Auto-generated method stub
//                    /*      Your code   to get date and time    */
//                date = new GregorianCalendar(selectedyear, selectedmonth, selectedday).getTime();
//                datePicker.setText(selectedmonth + "/" + selectedday + "/" + selectedyear);
//            }
//        },mYear, mMonth, mDay);
//        mDatePicker.setTitle("Select date");
//        mDatePicker.show();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        datePicker = (EditText) findViewById(R.id.datepicker);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        moneycount = (TextView)findViewById(R.id.moneycount);
        beer = (NumberPicker)findViewById(R.id.beercount);
        wine = (NumberPicker)findViewById(R.id.winecount);
        liquor = (NumberPicker)findViewById(R.id.liquorcount);
        shots = (NumberPicker)findViewById(R.id.shotcout);

        beer.setMinValue(0);
        beer.setMaxValue(10);
        beer.setWrapSelectorWheel(true);

        wine.setMinValue(0);
        wine.setMaxValue(10);
        wine.setWrapSelectorWheel(true);

        liquor.setMinValue(0);
        liquor.setMaxValue(10);
        liquor.setWrapSelectorWheel(true);

        shots.setMinValue(0);
        shots.setMaxValue(10);
        shots.setWrapSelectorWheel(true);
        shots.getValue();

        moneycount.setText("Covered: " + seekBar.getProgress() + "/" + seekBar.getMax());
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
                moneycount.setText("Covered: " + progress + "/" + seekBar.getMax());
                money = progress;
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog mDatePicker=new DatePickerDialog(AddDrinks.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        mYear = selectedyear;
                        mMonth = selectedmonth;
                        mDay = selectedday;
                        date = new GregorianCalendar(selectedyear, selectedmonth, selectedday).getTime();
                        datePicker.setText(selectedmonth + "/" + selectedday + "/" + selectedyear);
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();  }
        });
    }

    public void callAddDrinksAPI(View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);
        //Get the strings you need for the api
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");
        //Date day = date;
        SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd");
        String mday = sdfr.format( date );
        int mbeer = beer.getValue();
        int mwine = wine.getValue();
        int mshots = shots.getValue();
        int mliquor = liquor.getValue();
        int Mmoney = money;

        Call<User> call = apiService.adddrinks(username, password, mday, mbeer, mwine, mshots, mliquor, Mmoney);
        Log.i("u", username + password + mday + mbeer + mwine + mshots+ mliquor + Mmoney);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u  = response.body();
                if(u.getPassed().equals("y")){
                    //bring them to home page, let them know a problem
                    Intent intent = new Intent(AddDrinks.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
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
