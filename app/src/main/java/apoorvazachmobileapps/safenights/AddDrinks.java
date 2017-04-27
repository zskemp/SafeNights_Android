package apoorvazachmobileapps.safenights;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;import java.text.DateFormatSymbols;

public class AddDrinks extends Fragment {
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private static View rootview;

    private TextView moneycount;
    private TextView calendarDay;
    private TextView calendarMonth;

    private Date date;
    private ImageView datePicker;
    private Button submitDrink;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int money;


    private HoloCircleSeekBar beerPicker;
    private HoloCircleSeekBar winePicker;
    private HoloCircleSeekBar shotPicker;
    private HoloCircleSeekBar liquorPicker;
    AppCompatSeekBar seekbar;

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        int seekMoney = money;
        int year = mYear;
        int month = mMonth;
        int day = mDay;

        savedState.putInt("money", seekMoney);
        savedState.putInt("year", year);
        savedState.putInt("month", month);
        savedState.putInt("day", day);
    }

    public static AddDrinks newInstance() {
        AddDrinks fragment = new AddDrinks();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final View rootview = inflater.inflate(R.layout.activity_add_drinks, container, false);

        if (rootview != null) {
            ViewGroup parent = (ViewGroup) rootview.getParent();
            if (parent != null)
                parent.removeView(rootview);
        }
        try {
            rootview = inflater.inflate(R.layout.activity_add_drinks, container, false);
        } catch (InflateException e) {
            Toast.makeText(getActivity(), "An error occured loading this screen. Please try again.", Toast.LENGTH_SHORT);
        }

        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        money = 0;

        beerPicker = (HoloCircleSeekBar) rootview.findViewById(R.id.beerPicker);
        liquorPicker = (HoloCircleSeekBar) rootview.findViewById(R.id.liquorPicker);
        winePicker = (HoloCircleSeekBar) rootview.findViewById(R.id.winePicker);
        shotPicker = (HoloCircleSeekBar) rootview.findViewById(R.id.shotPicker);

        submitDrink = (Button)rootview.findViewById(R.id.submitDrink);
        submitDrink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callAddDrinksAPI(rootview);
            }
        });

        moneycount = (TextView) rootview.findViewById(R.id.moneycount);
        moneycount.setText("Money Spent: $0");
        calendarDay = (TextView) rootview.findViewById(R.id.calendarDay);
        calendarMonth = (TextView) rootview.findViewById(R.id.calendarMonth);
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        SimpleDateFormat day_date = new SimpleDateFormat("dddd");
        calendarDay.setText("" + mDay);
        calendarMonth.setText("" + getMonthForInt(mMonth));
        date = new GregorianCalendar(mYear, mMonth, mDay).getTime();


        /** Money Seek Bar Logic **/
        // get seekbar from view
        seekbar = (AppCompatSeekBar) rootview.findViewById(R.id.seekbar);

        // set listener
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                moneycount.setText("Money Spent: $" + String.valueOf(progress));
                money = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        datePicker = (ImageView)  rootview.findViewById(R.id.calendar);
        datePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();

                final DatePickerDialog mDatePicker=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    /*      Your code   to get date and time    */
                        date = new GregorianCalendar(selectedyear, selectedmonth, selectedday).getTime();
//                        datePicker.setText(selectedmonth+1 + "/" + selectedday + "/" + selectedyear);
                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                        calendarDay.setText(""+mDay);
                        calendarMonth.setText(getMonthForInt(mMonth));
                    }
                },mYear, mMonth, mDay);
                mDatePicker.getDatePicker().setMaxDate(new Date().getTime());
                mDatePicker.setTitle("");
                mDatePicker.show();  }
        });

        if (savedInstanceState != null) {
            money = savedInstanceState.getInt("money");
            mDay = savedInstanceState.getInt("day");
            mYear = savedInstanceState.getInt("year");
            mMonth = savedInstanceState.getInt("month");

            date = new GregorianCalendar(mYear, mMonth, mDay).getTime();
            moneycount.setText("Money Spent: $" + money);
//            datePicker.setText(mMonth+1 + "/" + mDay + "/" + mYear);
            calendarDay.setText(""+mDay);
            calendarMonth.setText(getMonthForInt(mMonth));
            seekbar.setProgress(money);

        }
        return rootview;
    }


    String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    public void callAddDrinksAPI(View view) {
        SafeNightsAPIInterface apiService = SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
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
                    //bring them to history page
                  Fragment fragment = History.newInstance();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    Toast.makeText(getActivity(), "Successfully entered data!", Toast.LENGTH_SHORT).show();
                } else {
                    //return them to the page with an error
                    Toast.makeText(getActivity(), "There has been a problem adding your night of drinking\n Please use correct formatting and check login credentials", Toast.LENGTH_LONG).show();
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
