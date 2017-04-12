package apoorvazachmobileapps.safenights.LocationHistory;

/**
 * Created by nanditakannapadi on 4/6/17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Fields {

    @SerializedName("xcord")
    @Expose
    private String xcord;
    @SerializedName("adventureID")
    @Expose
    private String adventureID;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("ycord")
    @Expose
    private String ycord;

    public String getXcord() {
        return xcord;
    }

    public void setXcord(String xcord) {
        this.xcord = xcord;
    }

    public String getAdventureID() {
        return adventureID;
    }

    public void setAdventureID(String adventureID) {
        this.adventureID = adventureID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getYcord() {
        return ycord;
    }

    public void setYcord(String ycord) {
        this.ycord = ycord;
    }

}