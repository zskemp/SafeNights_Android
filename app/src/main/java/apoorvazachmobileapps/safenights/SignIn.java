package apoorvazachmobileapps.safenights;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignIn extends Fragment {

    //private OnFragmentInteractionListener mListener;
    private EditText mUsername;
    private EditText mPassword;
    Button mSignIn;
    TextView appname;
    TextView mRegister;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";

    public SignIn() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen
        View rootview = inflater.inflate(R.layout.fragment_sign_in, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Typeface tf = Typeface.createFromAsset(this.getContext().getAssets(),"fonts/angelina.TTF");
        appname = (TextView)rootview.findViewById(R.id.appname);
        appname.setTypeface(tf);
        mUsername   = (EditText)rootview.findViewById(R.id.username);
        mPassword   = (EditText)rootview.findViewById(R.id.password);
        mSignIn = (Button)rootview.findViewById(R.id.login_button);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSignInAPI(v);
            }
        });
        mRegister = (TextView)rootview.findViewById(R.id.register_button);
        mRegister.setTypeface(tf);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SignUp();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, fragment).addToBackStack("")
                        .commit();
            }
        });

        return rootview;
    }

    public void callSignInAPI (View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        final String uname = mUsername.getText().toString();
        final String pword = mPassword.getText().toString();


        Call<User> call = apiService.signin(uname, pword);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u  = response.body();
                if(u.getPassed().equals("y")){
                    SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username", uname);
                    editor.putString("password", pword);
                    editor.putString("id", "");
                    Set<String> locations = new HashSet<String>();
                    editor.putStringSet("locations", locations);
                    editor.commit();

                    Intent intent = new Intent(SignIn.this.getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Incorrect Credentials!", Toast.LENGTH_LONG).show();
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
