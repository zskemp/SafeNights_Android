package apoorvazachmobileapps.safenights;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getStarted(View view) {
        Intent intent = new Intent(this, GetStarted.class);
        startActivity(intent);
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
}
