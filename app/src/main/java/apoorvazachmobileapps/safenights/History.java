package apoorvazachmobileapps.safenights;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import apoorvazachmobileapps.safenights.DrinkHistory.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class History extends AppCompatActivity {

    private LineChart mChart;
    private LineChart aChart;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private ArrayList<Fields> nights;
    private HashMap<String, ArrayList<Fields>> months;
    TextView titleMoney;
    TextView titleAlcohol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Arciform.otf");
        titleMoney = (TextView)findViewById(R.id.titleMoney);
        titleMoney.setTypeface(tf);
        titleAlcohol = (TextView)findViewById(R.id.titleAlcohol);
        titleAlcohol.setTypeface(tf);

        // Initialize Global Variables
        nights = new ArrayList<Fields>();
        months = new HashMap<>();

        View v = this.findViewById(android.R.id.content);
        //Note: We add to the nights array list in this method call
        callHistoryAPI(v);

        //NOTE: All the remaining calls needed to be handeled in the callHistoryAPI() because
        // they were dependent on the response call form the HTTP request
        // (these are handeled asychronously and so could not just call afterwards on main thread)

        //TODO: Make a button that toggles the months
    }

    //TODO:Make a good algorithm for calculating a persons drunkness (how much to weigh each drink)
    public float calculateDrunkness(Fields field) {
        float total = 0;
        total = total + Float.parseFloat(field.getBeer()) + Float.parseFloat(field.getWine()) + Float.parseFloat(field.getShots()) + Float.parseFloat(field.getHardliquor());
        return total;
    }

    public void populatechart() {

        List<Entry> alcohol = new ArrayList<Entry>();
        List<Entry> money = new ArrayList<Entry>();

        //TODO: Will need to create a method to select and get the current date. Maybe return list of that month's hash
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        String thisMonthKey = Integer.toString(cal.get(Calendar.MONTH)) + Integer.toString(cal.get(Calendar.YEAR));
        ArrayList<Fields> thisMonth = months.get(thisMonthKey);

        if(thisMonth == null){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "You have no history  yet!", Toast.LENGTH_SHORT);
            return;
        }
        boolean missing1st = true;
        boolean missing31st = true;
        for (Fields data : thisMonth) {
            //Algorithm for computing "drunkness"
            float alcoholY = calculateDrunkness(data);
            //Checking for 1st or 31st date to make graph look decent
            if(Integer.parseInt(data.getDay()) == 0) {
                missing1st = false;
            } else if(Integer.parseInt(data.getDay()) == 31) {
                missing31st = false;
            }
            // turn your data into Entry objects
            alcohol.add(new Entry(Float.parseFloat(data.getDay()), alcoholY));
            money.add(new Entry(Float.parseFloat(data.getDay()), (float)data.getMoney()));
        }

        //Adding the 1st and 31st of the month to make graph look decent
        if(missing1st) {
            alcohol.add(new Entry(0f, 0f));
            money.add(new Entry(0f, 0f));
        }
        if(missing31st) {
            alcohol.add(new Entry(31f, 0f));
            money.add(new Entry(31f, 0f));
        }

        //Needs to be sorted to work :)
        Collections.sort(alcohol, new EntryXComparator());
        Collections.sort(money, new EntryXComparator());

        LineDataSet alcoholSet = new LineDataSet(alcohol, "alcohol");
        LineDataSet moneySet = new LineDataSet(money, "money");

        //STYLING Part 1!
        alcoholSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        alcoholSet.setLineWidth(2f);
        alcoholSet.setDrawFilled(true);
        alcoholSet.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
        alcoholSet.setFillColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        alcoholSet.setCircleColor(Color.BLUE);
        alcoholSet.setCircleColorHole(Color.BLUE);
        alcoholSet.setDrawValues(false);
        moneySet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        moneySet.setLineWidth(2f);
        moneySet.setDrawFilled(true);
        moneySet.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
        moneySet.setFillColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        moneySet.setCircleColor(Color.BLUE);
        moneySet.setCircleColorHole(Color.BLUE);
        moneySet.setDrawValues(false);

        LineData lineDataAlcohol = new LineData(alcoholSet);
        LineData lineDataMoney = new LineData(moneySet);

        mChart.setData(lineDataMoney);
        aChart.setData(lineDataAlcohol);

        //STYLING Part 2!
        alcoholSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        moneySet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        Description mDes = mChart.getDescription();
        mDes.setEnabled(false);
        mChart.getXAxis().setAxisMinimum(0f);
        mChart.getXAxis().setAxisMaximum(31f);
        mChart.getXAxis().setLabelCount(5, true);

        aChart.getXAxis().setDrawGridLines(false);
        aChart.getAxisLeft().setDrawGridLines(false);
        aChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        aChart.getAxisRight().setEnabled(false);
        aChart.getLegend().setEnabled(false);
        Description aDesc = aChart.getDescription();
        aDesc.setEnabled(false);
        aChart.getXAxis().setAxisMinimum(0f);
        aChart.getXAxis().setAxisMaximum(31f);
        aChart.getXAxis().setLabelCount(5, true);

        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.getAxisLeft().setValueFormatter(new DollarFormatter());

        aChart.getAxisLeft().setAxisMinimum(0f);
        aChart.getAxisLeft().setAxisMaximum(100f);
        aChart.getAxisLeft().setLabelCount(5, true);
        aChart.getAxisLeft().setValueFormatter(new PercentFormatter());

//        mChart.setBackgroundColor("#211E36");


        // WHAT IF THERE IS NO DATA?!?!?!
        mChart.setNoDataText("You have no data for this month" + '\n' + "Please record any activity in Add Drinks");
        aChart.setNoDataText("You have no data for this month" + '\n' + "Please record any activity in Add Drinks");

        mChart.invalidate(); // refresh
        aChart.invalidate(); // refresh
    }


    public class DollarFormatter implements IAxisValueFormatter {
        private DecimalFormat mFormat;
        public DollarFormatter() {
            // format values to 1 decimal digit
            mFormat = new DecimalFormat("###,###,##0");
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return "$" + mFormat.format(value);
        }
        public int getDecimalDigits() {
            return 1;
        }
    }

    public class PercentFormatter implements IValueFormatter, IAxisValueFormatter
    {
        protected DecimalFormat mFormat;
        public PercentFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0");
        }
        public PercentFormatter(DecimalFormat format) {
            this.mFormat = format;
        }
        // IValueFormatter
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + " %";
        }
        // IAxisValueFormatter
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value) + " %";
        }
        public int getDecimalDigits() {
            return 1;
        }
    }

    public void parseDataByMonths() {
        for (Fields data : nights) {
            // Parse Date
            String day = data.getDay();
            int thisMonth = 0;
            int thisYear = 0;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(day);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                thisMonth = cal.get(Calendar.MONTH);
                thisYear = cal.get(Calendar.YEAR);
                //Changing the full day which we just got to just the date (1st-31st)
                data.setDay(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String key = Integer.toString(thisMonth) + Integer.toString(thisYear);
            if (months.containsKey(key)) {
                months.get(key).add(data);
            } else {
                ArrayList<Fields> newMonth = new ArrayList<>();
                newMonth.add(data);
                months.put(key, newMonth);
            }
        }
        return;
    }

    public void callHistoryAPI (View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");

        Call<Example> call = apiService.gethistory(username, password);


        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                Example table  = response.body();
                //Parse response.body() and add to nights
                for(int i = 0; i < table.getAlcoholtable().size(); i++) {
                    Alcoholtable trial = table.getAlcoholtable().get(i);
                    Fields fields = trial.getFields();
                    nights.add(fields);
                }
                //Load Hashmap of Dates by Month+Year with array list of just days
                parseDataByMonths();

                //Plotting the Data for a particular month
                mChart = (LineChart) findViewById(R.id.chartMoney);
                aChart = (LineChart) findViewById(R.id.chartAlcohol);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                mChart.setMinimumHeight((int)(metrics.heightPixels*0.4));
                aChart.setMinimumHeight((int)(metrics.heightPixels*0.4));
                populatechart();
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                // Log error here since request failed
                Log.e("API Call:", t.toString());
                //TODO: Make a cool page saying not connected to internet, there was a problem...
            }
        });
    }

}
