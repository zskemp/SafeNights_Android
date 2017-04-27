package apoorvazachmobileapps.safenights;

/**
 * Created by nanditakannapadi on 3/31/17.
 */

import java.util.Date;
import java.util.List;

import apoorvazachmobileapps.safenights.DrinkHistory.Example;
import apoorvazachmobileapps.safenights.LocationHistory.Location;
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
    @POST("api/v1/adddrinks/")
    Call<User> adddrinks(@Field("username") String username, @Field("pwd") String password, @Field("day") String day, @Field("beer") int beer, @Field("wine") int wine, @Field("shots") int shots, @Field("liquor") int liquor, @Field("money") int money);

    @FormUrlEncoded
    @POST("api/v1/gethistory/")
    Call<Example> gethistory(@Field("username") String username, @Field("pwd") String password);

    @FormUrlEncoded
    @POST("api/v1/startnight/")
    Call<User> startnight(@Field("username") String username, @Field("pwd") String password);

    @FormUrlEncoded
    @POST("api/v1/addloc/")
    Call<User> addlocation(@Field("username") String username, @Field("pwd") String password, @Field("id") String id, @Field("xcord") Double xcord, @Field("ycord") Double ycord);

    @FormUrlEncoded
    @POST("api/v1/getnight/")
    Call<Location> getnight(@Field("username") String username, @Field("pwd") String password, @Field("id") String id);

    @FormUrlEncoded
    @POST("api/v1/email/")
    Call<User> email (@Field("name") String name, @Field("reason") int reasonNum, @Field("email") String email, @Field("location") String location, @Field("xcord") Double xcord, @Field("ycord") Double ycord);

}