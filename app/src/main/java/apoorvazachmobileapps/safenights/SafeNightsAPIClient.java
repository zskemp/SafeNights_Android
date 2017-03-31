package apoorvazachmobileapps.safenights;

/**
 * Created by nanditakannapadi on 3/31/17.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by sherriff on 10/25/16.
 */

public class SafeNightsAPIClient {
//    public static final String BASE_URL = "http://stardock.cs.virginia.edu/louslist/Courses/view/";
    public static final String BASE_URL = "https://gentle-badlands-54918.herokuapp.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}