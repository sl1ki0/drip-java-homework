package drip.hw4.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Core {
    private String core;
    private Integer flight;
    @SerializedName("gridfins")
    private Boolean gridFins;
    private Boolean legs;
    private Boolean reused;
    @SerializedName("landing_attempt")
    private Boolean landingAttempt;
    @SerializedName("landing_success")
    private Boolean landingSuccess;
    @SerializedName("landing_type")
    private String landingType;
    @SerializedName("landpad")
    private String landingPad;

    public String getCore() {
        return core;
    }

    public Integer getFlight() {
        return flight;
    }

    public Boolean getGridFins() {
        return gridFins;
    }

    public Boolean getLegs() {
        return legs;
    }

    public Boolean getReused() {
        return reused;
    }

    public Boolean getLandingAttempt() {
        return landingAttempt;
    }

    public Boolean getLandingSuccess() {
        return landingSuccess;
    }

    public String getLandingType() {
        return landingType;
    }

    public String getLandingPad() {
        return landingPad;
    }
}
