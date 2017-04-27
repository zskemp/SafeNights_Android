package apoorvazachmobileapps.safenights;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUp extends Fragment {

    //private OnFragmentInteractionListener mListener;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private EditText mUsername;
    private EditText mFname;
    private EditText mLname;
    private EditText mEmail;
    private EditText mPassword;
    private TextView appname;
    Button mSignUp;

    public static SignUp newInstance() {
        SignUp fragment = new SignUp();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Typeface tf = Typeface.createFromAsset(this.getContext().getAssets(),"fonts/Arciform.otf");
        mUsername   = (EditText)rootview.findViewById(R.id.username);
        appname = (TextView)rootview.findViewById(R.id.appname);
        appname.setTypeface(tf);
        appname.getPaint().setShader(new LinearGradient(0,0,0,appname.getLineHeight(), Color.parseColor("#6FDA9C"), Color.parseColor("#56C5EF"), Shader.TileMode.REPEAT));
        mFname   = (EditText)rootview.findViewById(R.id.fname);
        mLname   = (EditText)rootview.findViewById(R.id.lname);
        mEmail   = (EditText)rootview.findViewById(R.id.email);
        mPassword   = (EditText)rootview.findViewById(R.id.password);
        mSignUp = (Button)rootview.findViewById(R.id.signup_button);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSignUpAPI(v);
            }
        });

        return rootview;
    }

    public void callSignUpAPI(View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        //Get the strings you need for the api
        String username = mUsername.getText().toString();
        final String fname = mFname.getText().toString();
        String lname = mLname.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        Call<User> call = apiService.signup(username, fname, lname, email, password);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u  = response.body();
                Log.i("Body", u.toString());
                Log.i("y/n", u.getPassed());
                if(u.getPassed().equals("y")){
                    SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("firstname", fname);
                    editor.commit();
                    //bring them to login page
                    Fragment fragment = new SignIn();
                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment, fragment).addToBackStack("")
                            .commit();
                    Toast.makeText(getActivity().getApplicationContext(), "You have been registered!\nPlease login :)", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "You have filled out your credentials incorrectly.\n Please use correct formatting", Toast.LENGTH_LONG).show();
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
