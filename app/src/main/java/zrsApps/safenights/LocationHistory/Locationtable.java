package zrsApps.safenights.LocationHistory;

/**
 * Created by nanditakannapadi on 4/6/17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Locationtable {

    @SerializedName("fields")
    @Expose
    private Fields fields;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("pk")
    @Expose
    private Integer pk;

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
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