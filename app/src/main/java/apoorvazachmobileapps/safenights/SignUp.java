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

    private boolean goodUsername = false;
    private boolean goodFname = false;
    private boolean goodLname = false;
    private boolean goodEmail = false;
    private boolean goodPassword = false;

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
        //Add Proper Logic For Each Field To Validate for Submission
        mUsername   = (EditText)rootview.findViewById(R.id.username);
        mUsername.addTextChangedListener(new TextValidator(mUsername) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if( mUsername.getText().toString().length() == 0 ) {
                    mUsername.setError("Username is required!");
                } else if( mUsername.getText().toString().length() > 20 ) {
                    mUsername.setError("Username cannot be longer than 20 characters!");
                } else if( mUsername.getText().toString().contains(" ")) {
                    mUsername.setError("Username cannot include spaces!");
                } else if( mUsername.getText().toString().matches("^[A-Za-z0-9]+$")) {
                    //Matches what we want correctly
                    goodUsername = true;
                } else {
                    //Has characters not allowed
                    mUsername.setError("Username can only contain uppercase/lowercase letters and numbers 0-9!");
                }
            }
        });
        mFname   = (EditText)rootview.findViewById(R.id.fname);
        mFname.addTextChangedListener(new TextValidator(mFname) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if( mFname.getText().toString().length() == 0 ) {
                    mFname.setError("First name is required!");
                } else if( mFname.getText().toString().length() > 20 ) {
                    mFname.setError("First name cannot be longer than 20 characters");
                } else if( mFname.getText().toString().contains(" ")) {
                    mFname.setError("First name cannot include spaces");
                } else {
                    //Matches what we want correctly
                    goodFname = true;
                }
            }
        });
        mLname   = (EditText)rootview.findViewById(R.id.lname);
        mLname.addTextChangedListener(new TextValidator(mLname) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if( mLname.getText().toString().length() == 0 ) {
                    mLname.setError("Last name is required!");
                } else if( mLname.getText().toString().length() > 20 ) {
                    mLname.setError("Last name cannot be longer than 20 characters!");
                } else if( mLname.getText().toString().contains(" ")) {
                    mLname.setError("Last name cannot include spaces!");
                } else {
                    //Matches what we want correctly
                    goodLname = true;
                }
            }
        });
        mEmail   = (EditText)rootview.findViewById(R.id.email);
        mEmail.addTextChangedListener(new TextValidator(mEmail) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if(isValidEmail(text)) {
                    goodEmail = true;
                } else {
                    mEmail.setError("Invalid Email!");
                }
            }
        });
        mPassword   = (EditText)rootview.findViewById(R.id.password);
        mPassword.addTextChangedListener(new TextValidator(mPassword) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if( mPassword.getText().toString().length() == 0 ) {
                    mPassword.setError("Password is required!");
                } else if( mPassword.getText().toString().length() < 6 ) {
                    mPassword.setError("Password cannot be shorter than 6 characters!");
                } else if( mPassword.getText().toString().length() > 20 ) {
                    mPassword.setError("Password cannot be longer than 20 characters!");
                } else if( mPassword.getText().toString().contains(" ")) {
                    mPassword.setError("Password cannot include spaces!");
                } else {
                    //Matches what we want correctly
                    goodPassword = true;
                }
            }
        });
        mSignUp = (Button)rootview.findViewById(R.id.signup_button);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodUsername && goodFname && goodLname & goodEmail && goodPassword) {
                    callSignUpAPI(v);
                    mSignUp.setEnabled(false);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You must fulfill all requirements above!\nPlease complete and try again :)", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootview;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void callSignUpAPI(View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        //Get the strings you need for the api
        String username = mUsername.getText().toString();
        final String fname = mFname.getText().toString();
        final String lname = mLname.getText().toString();
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
                    Toast.makeText(getActivity().getApplicationContext(), "There already exists a user with that username.\nPlease choose a different one :)", Toast.LENGTH_LONG).show();
                }
                mSignUp.setEnabled(true);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Log error here since request failed
                Log.e("API Call:", t.toString());
                mSignUp.setEnabled(true);
            }
        });
    }

}
