package com.blako.mensajero.VO;

/**
 * Created by franciscotrinidad on 18/09/17.
 */

public class BkoOfferGroupResponse {

    private String bko_customer_id;
    private String bko_customer_name;
    private String bko_customer_logo;
    private String number_offer;

    public String getBko_customer_id() {
        return bko_customer_id;
    }

    public void setBko_customer_id(String bko_customer_id) {
        this.bko_customer_id = bko_customer_id;
    }

    public String getBko_customer_name() {
        return bko_customer_name;
    }

    public void setBko_customer_name(String bko_customer_name) {
        this.bko_customer_name = bko_customer_name;
    }

    public String getBko_customer_logo() {
        return bko_customer_logo;
    }

    public void setBko_customer_logo(String bko_customer_logo) {
        this.bko_customer_logo = bko_customer_logo;
    }

    public String getNumber_offer() {
        return number_offer;
    }

    public void setNumber_offer(String number_offer) {
        this.number_offer = number_offer;
    }
}
