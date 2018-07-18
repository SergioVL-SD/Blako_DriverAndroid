package com.blako.mensajero.VO;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 1/26/16.
 */
public class BkoServiceNoOfferResponse extends BkoRequestResponse {

    private ArrayList<BkoServiceNoOfferVO> servicios;

    public ArrayList<BkoServiceNoOfferVO> getServicios() {
        return servicios;
    }

    public void setServicios(ArrayList<BkoServiceNoOfferVO> servicios) {
        this.servicios = servicios;
    }

    public class BkoServiceNoOfferVO {

        private String bko_queuedtasks_receiver_alias;
        private String bko_customeraddress_alias;
        private String bko_orders_daterequest;
        private String bko_orders_total;

        public String getBko_queuedtasks_receiver_alias() {
            return bko_queuedtasks_receiver_alias;
        }

        public void setBko_queuedtasks_receiver_alias(String bko_queuedtasks_receiver_alias) {
            this.bko_queuedtasks_receiver_alias = bko_queuedtasks_receiver_alias;
        }

        public String getBko_customeraddress_alias() {
            return bko_customeraddress_alias;
        }

        public void setBko_customeraddress_alias(String bko_customeraddress_alias) {
            this.bko_customeraddress_alias = bko_customeraddress_alias;
        }

        public String getBko_orders_daterequest() {
            return bko_orders_daterequest;
        }

        public void setBko_orders_daterequest(String bko_orders_daterequest) {
            this.bko_orders_daterequest = bko_orders_daterequest;
        }

        public String getBko_orders_total() {
            return bko_orders_total;
        }

        public void setBko_orders_total(String bko_orders_total) {
            this.bko_orders_total = bko_orders_total;
        }
    }


}
