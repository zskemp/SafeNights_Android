package apoorvazachmobileapps.safenights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Apoorva on 3/31/2017.
 */

public class User {
    @SerializedName("passed")
    @Expose
    private String passed;
    public String getPassed() {
        return passed;
    }
    public void setPassed(String passed) {
        this.passed = passed;
    }
//    private HashMap<String, String> response;

//    public HashMap<String, String> getResponse() {
//        return response;
//    }
//
//    /**
//     *
//     * @param response
//     * The response
//     */
//    public void setResponse(HashMap<String, String> response) {
//        this.response = response;
//    }
}
