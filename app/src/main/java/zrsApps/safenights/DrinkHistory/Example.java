package zrsApps.safenights.DrinkHistory;

/**
 * Created by nanditakannapadi on 4/1/17.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

    @SerializedName("alcoholtable")
    @Expose
    private List<Alcoholtable> alcoholtable = null;

    public List<Alcoholtable> getAlcoholtable() {
        return alcoholtable;
    }

    public void setAlcoholtable(List<Alcoholtable> alcoholtable) {
        this.alcoholtable = alcoholtable;
    }

}
