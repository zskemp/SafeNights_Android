package apoorvazachmobileapps.safenights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class testwebservice extends AppCompatActivity {

    public static final String BASE_URL = "http://stardock.cs.virginia.edu/louslist/Courses/view/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testwebservice);
    }

    public void callAPI(View view) {

        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        EditText mnemonic = (EditText)findViewById(R.id.editText);
        String mnemonicSearch = mnemonic.getText().toString();

        Call<List<Section>> call = apiService.sectionList(mnemonicSearch);
        call.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(Call<List<Section>> call, Response<List<Section>> response) {
                List<Section> sections = response.body();
                String courseDisplay = "";
                for(Section s : sections) {
                    Log.d("LousList", "Received: " + s);
                    courseDisplay += s + "\n";
                }
                TextView display = (TextView)findViewById(R.id.textview);
                display.setText(courseDisplay);
            }

            @Override
            public void onFailure(Call<List<Section>> call, Throwable t) {
                // Log error here since request failed
                Log.e("LousList", t.toString());
            }
        });

    }
}
