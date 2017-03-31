package apoorvazachmobileapps.safenights;

/**
 * Created by nanditakannapadi on 3/31/17.
 */

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SafeNightsAPIInterface {

    @GET("{dept}?json")
    Call<List<Section>> sectionList(@Path("dept") String dept);

//    @GET("{user}")
//    Call<List<Alcohol>> alcoholList(@Path("user") String user);

    //Add user parameter to this
//    @FormUrlEncoded
//    @POST("adddrinks/")
//    Call<Alcohol> adddrink(@Field String id, @Field String day, @Field Double beer, @Field Double wine, @Field Double shots, @Field Double liquor, @Field Double money);

//    @FormUrlEncoded
//    @POST("signin_mobile/")
//    Call<User> signin(@Field("username") String username, @Field("pwd") String password);
}