package apoorvazachmobileapps.safenights;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_favorites:
                                selectedFragment = GetStarted.newInstance();
                                break;
                            case R.id.action_schedules:
                                selectedFragment = AddDrinks.newInstance();
                                break;
                            case R.id.action_music:
                                selectedFragment = History.newInstance();
                                break;
                            case R.id.last_night:
                                selectedFragment = LastNight.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, GetStarted.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }
}
//    static final int NUM_ITEMS = 2;
//
//    MyAdapter mAdapter;
//
//    ViewPager mPager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // this information can come from a database or web service
//        List<String> pageTitles = new ArrayList<String>() {{
//            add("Tab One");
//            add("Tab Two");
//        }};
//
//// this information can come from a database or web service
//        List<Class> fragmentTypes = new ArrayList<Class>() {{
//            add(SignIn.class);
//            add(SignUp.class);
//        }};
//
//
//        mAdapter = new MyAdapter(getSupportFragmentManager(), pageTitles, fragmentTypes);
//
//        mPager = (ViewPager)findViewById(R.id.pager);
//        mPager.setAdapter(mAdapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mPager);

//        // Watch for button clicks.
//        Button button = (Button)findViewById(R.id.goto_first);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mPager.setCurrentItem(0);
//            }
//        });
//        button = (Button)findViewById(R.id.goto_last);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mPager.setCurrentItem(NUM_ITEMS-1);
//            }
//        });
//    }

//    public static class MyAdapter extends FragmentPagerAdapter {
//        public MyAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public int getCount() {
//            return NUM_ITEMS;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            if(position == 0)
//            {
//                SignIn tab1 = new SignIn();
//                return tab1;
//            }
//            else
//            {
//                SignUp tab2 = new SignUp();
//                return tab2;
//            }
//        }
//    }

//}
