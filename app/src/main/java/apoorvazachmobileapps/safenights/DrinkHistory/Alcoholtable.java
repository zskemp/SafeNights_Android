package apoorvazachmobileapps.safenights.DrinkHistory;

/**
 * Created by nanditakannapadi on 4/1/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import apoorvazachmobileapps.safenights.*;

public class Alcoholtable {

    @SerializedName("fields")
    @Expose
    private apoorvazachmobileapps.safenights.DrinkHistory.Fields fields;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("pk")
    @Expose
    private Integer pk;

    public apoorvazachmobileapps.safenights.DrinkHistory.Fields getFields() {
        return fields;
    }

    public void setFields(apoorvazachmobileapps.safenights.DrinkHistory.Fields fields) {
        this.fields = fields;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

}

