package com.blako.mensajero.VO;

/**
 * Created by franciscotrinidad on 2/3/16.
 */
public class BkoVersionVO extends BkoRequestResponse{

    private String timerequest;
    private String timedisconnect;

    public String getTimerequest() {
        return timerequest;
    }

    public void setTimerequest(String timerequest) {
        this.timerequest = timerequest;
    }
}
