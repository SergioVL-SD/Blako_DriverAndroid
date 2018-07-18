package com.blako.mensajero.VO;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 1/12/16.
 */
public class BkoTripVO {

    private String bko_orders_id;
    private String bko_orders_trips_id;
    private String bko_orders_trips_type;
    private String bko_queuedtasks_id;
    private ArrayList<BkoChildTripVO>  origen;
    private ArrayList<BkoChildTripVO>  destino;


    public String getBko_orders_id() {
        return bko_orders_id;
    }

    public void setBko_orders_id(String bko_orders_id) {
        this.bko_orders_id = bko_orders_id;
    }

    public String getBko_orders_trips_id() {
        return bko_orders_trips_id;
    }

    public void setBko_orders_trips_id(String bko_orders_trips_id) {
        this.bko_orders_trips_id = bko_orders_trips_id;
    }

    public String getBko_orders_trips_type() {
        return bko_orders_trips_type;
    }

    public void setBko_orders_trips_type(String bko_orders_trips_type) {
        this.bko_orders_trips_type = bko_orders_trips_type;
    }

    public String getBko_queuedtasks_id() {
        return bko_queuedtasks_id;
    }

    public void setBko_queuedtasks_id(String bko_queuedtasks_id) {
        this.bko_queuedtasks_id = bko_queuedtasks_id;
    }

    public ArrayList<BkoChildTripVO> getOrigen() {
        return origen;
    }

    public void setOrigen(ArrayList<BkoChildTripVO> origen) {
        this.origen = origen;
    }

    public ArrayList<BkoChildTripVO> getDestino() {
        return destino;
    }

    public void setDestino(ArrayList<BkoChildTripVO> destino) {
        this.destino = destino;
    }
}