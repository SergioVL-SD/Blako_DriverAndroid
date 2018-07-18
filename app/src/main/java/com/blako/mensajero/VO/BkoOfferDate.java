package com.blako.mensajero.VO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscotrinidad on 02/03/17.
 */

public class BkoOfferDate {
   private String date;
   private String numberAnnouncement;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumberAnnouncement() {
        return numberAnnouncement;
    }

    public void setNumberAnnouncement(String numberAnnouncement) {
        this.numberAnnouncement = numberAnnouncement;
    }
}
