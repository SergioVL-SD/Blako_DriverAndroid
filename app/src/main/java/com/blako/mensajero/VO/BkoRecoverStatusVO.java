package com.blako.mensajero.VO;

import java.util.ArrayList;

/**
 * Created by ascenzo on 2/7/16.
 */
public class BkoRecoverStatusVO extends BkoRequestResponse {

    private ArrayList<BkoOrderVO> order;

    public ArrayList<BkoOrderVO> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<BkoOrderVO> order) {
        this.order = order;
    }


    private ArrayList<BkoTripVO> trips;


    public ArrayList<BkoTripVO> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<BkoTripVO> trips) {
        this.trips = trips;
    }
}
