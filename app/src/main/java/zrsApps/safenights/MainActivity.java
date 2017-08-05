package zrsApps.safenights;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);


        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.get_started:
                                selectedFragment = GetStarted.newInstance();
                                break;
                            case R.id.add_drinks:
                                selectedFragment = AddDrinks.newInstance();
                                break;
                            case R.id.history:
                                selectedFragment = History.newInstance();
                                break;
                            case R.id.last_night:
                                selectedFragment = LastNight.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment, "getStarted");
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        if(savedInstanceState == null){
            GetStarted f = GetStarted.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, f, "getStarted").commit();
        }
        else {
            int key = savedInstanceState.getInt("key");
            if(key == 1) {
                AddDrinks f = AddDrinks.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, f, "addDrinks").commit();
            }
            else if(key == 2){
                History f = History.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, f, "history").commit();
            }
            else if(key == 3){
                LastNight f = LastNight.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, f, "lastNight").commit();
            }
        }
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_layout, GetStarted.newInstance());
//        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }
}
