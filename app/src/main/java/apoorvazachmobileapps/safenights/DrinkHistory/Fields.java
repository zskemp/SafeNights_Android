package apoorvazachmobileapps.safenights.DrinkHistory;

/**
 * Created by nanditakannapadi on 4/1/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Fields {

    @SerializedName("hardliquor")
    @Expose
    private String hardliquor;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("beer")
    @Expose
    private String beer;
    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("shots")
    @Expose
    private String shots;
    @SerializedName("wine")
    @Expose
    private String wine;
    @SerializedName("money")
    @Expose
    private Integer money;

    public String getHardliquor() {
        return hardliquor;
    }

    public void setHardliquor(String hardliquor) {
        this.hardliquor = hardliquor;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getBeer() {
        return beer;
    }

    public void setBeer(String beer) {
        this.beer = beer;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getShots() {
        return shots;
    }

    public void setShots(String shots) {
        this.shots = shots;
    }

    public String getWine() {
        return wine;
    }

    public void setWine(String wine) {
        this.wine = wine;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

}