package apoorvazachmobileapps.safenights.LocationHistory;

/**
 * Created by nanditakannapadi on 4/6/17.
 */


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("locationtable")
    @Expose
    private List<Locationtable> locationtable = null;

    public List<Locationtable> getLocationtable() {
        return locationtable;
    }

    public void setLocationtable(List<Locationtable> locationtable) {
        this.locationtable = locationtable;
    }

}