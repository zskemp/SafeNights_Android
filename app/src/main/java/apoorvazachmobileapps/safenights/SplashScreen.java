package apoorvazachmobileapps.safenights;


//Imports
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;


//Splash Screen!
public class SplashScreen extends Activity {
    TextView appname;
    TextView loading;
    AVLoadingIndicatorView indicator;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Arciform.otf");
        indicator = (AVLoadingIndicatorView)findViewById(R.id.avi);
        appname = (TextView)findViewById(R.id.appname);
        appname.setTypeface(tf);
        appname.getPaint().setShader(new LinearGradient(0,0,0,appname.getLineHeight(), Color.parseColor("#6FDA9C"), Color.parseColor("#56C5EF"), Shader.TileMode.REPEAT));
        loading = (TextView)findViewById(R.id.loading);
        loading.setTypeface(tf);
        indicator.show();
        indicator.setVisibility(View.VISIBLE);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    if (settings.getString("username", "") == null || settings.getString("username","") == ""){
                        Intent intent = new Intent(SplashScreen.this, Login.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    }

                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
