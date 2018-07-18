package com.blako.mensajero.VO;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 1/11/16.
 */
public class BkoTrips extends BkoRequestResponse {

    private ArrayList<BkoTripVO> trips;

    public ArrayList<BkoTripVO> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<BkoTripVO> trips) {
        this.trips = trips;
    }


}
