package com.blako.mensajero.VO;

/**
 * Created by franciscotrinidad on 10/5/15.
 */
public class BkoCheckoutResponse extends BkoRequestResponse {

    private String payment;


    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
