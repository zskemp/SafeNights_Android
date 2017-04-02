package apoorvazachmobileapps.safenights.DrinkHistory;

/**
 * Created by nanditakannapadi on 4/1/17.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

    @SerializedName("alcoholtable")
    @Expose
    private List<apoorvazachmobileapps.safenights.DrinkHistory.Alcoholtable> alcoholtable = null;

    public List<apoorvazachmobileapps.safenights.DrinkHistory.Alcoholtable> getAlcoholtable() {
        return alcoholtable;
    }

    public void setAlcoholtable(List<apoorvazachmobileapps.safenights.DrinkHistory.Alcoholtable> alcoholtable) {
        this.alcoholtable = alcoholtable;
    }

}
