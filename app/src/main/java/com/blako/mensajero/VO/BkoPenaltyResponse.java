package com.blako.mensajero.VO;

import java.io.Serializable;

/**
 * Created by ascenzo on 7/28/17.
 */

public class BkoPenaltyResponse implements Serializable {

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    private String hour;
    private String rate;

}
