package apoorvazachmobileapps.safenights.LocationHistory;

/**
 * Created by nanditakannapadi on 4/6/17.
 */


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("loc_table")
    @Expose
    private List<LocTable> locTable = null;

    public List<LocTable> getLocTable() {
        return locTable;
    }

    public void setLocTable(List<LocTable> locTable) {
        this.locTable = locTable;
    }

}