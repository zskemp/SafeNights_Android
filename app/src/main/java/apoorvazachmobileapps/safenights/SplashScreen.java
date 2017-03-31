package apoorvazachmobileapps.safenights;


//Imports
        import android.app.Activity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;

//Splash Screen!
public class SplashScreen extends Activity {
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

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
