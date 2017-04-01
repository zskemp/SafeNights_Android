package apoorvazachmobileapps.safenights;

/**
 * Created by nanditakannapadi on 3/31/17.
 */

import java.util.Date;
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

//    @GET("{user}")
//    Call<List<Alcohol>> alcoholList(@Path("user") String user);

    //Add user parameter to this
//    @FormUrlEncoded
//    @POST("adddrinks/")
//    Call<Alcohol> adddrink(@Field String id, @Field String day, @Field Double beer, @Field Double wine, @Field Double shots, @Field Double liquor, @Field Double money);

    @FormUrlEncoded
    @POST("api/v1/signin/")
    Call<User> signin(@Field("username") String username, @Field("pwd") String password);

    @FormUrlEncoded
    @POST("api/v1/signup/")
    Call<User> signup(@Field("username") String username, @Field("fname") String fname, @Field("lname") String lname, @Field("email") String email, @Field("pwd") String password);

    @FormUrlEncoded
    @POST("api/v1/addrinks/")
    Call<User> adddrinks(@Field("id") String username, @Field("day") Date day, @Field("beer") Double beer, @Field("wine") Double wine, @Field("shots") Double shots, @Field("liquor") Double liquor, @Field("money") Double money);

}