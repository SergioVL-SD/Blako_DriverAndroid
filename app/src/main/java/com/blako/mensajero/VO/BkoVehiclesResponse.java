package com.blako.mensajero.VO;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 12/17/15.
 */
public class BkoVehiclesResponse  extends BkoRequestResponse {


    public ArrayList<BkoVehicleVO> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<BkoVehicleVO> vehicles) {
        this.vehicles = vehicles;
    }

    private ArrayList<BkoVehicleVO>  vehicles;


}
