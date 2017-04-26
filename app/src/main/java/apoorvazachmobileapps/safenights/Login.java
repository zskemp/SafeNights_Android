package apoorvazachmobileapps.safenights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            SignIn firstFragment = new SignIn();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, firstFragment).commit();
        }
    }

}
