package com.blako.mensajero.VO;

/**
 * Created by franciscotrinidad on 10/5/15.
 */
public class BkoRequestResponse {

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    private boolean response;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
}
