package com.blako.mensajero.models;

import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class HubConfig {

    private Integer hubId;
    private String label;
    private double rate;
    private double rateExtra;
    private PolygonOptions polygonOptions;
    private Integer regionId;

    public HubConfig(Integer hubId, String label, double rate, double rateExtra, PolygonOptions polygonOptions, Integer regionId) {
        this.hubId = hubId;
        this.label = label;
        this.rate = rate;
        this.rateExtra = rateExtra;
        this.polygonOptions = polygonOptions;
        this.regionId = regionId;
    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getRateExtra() {
        return rateExtra;
    }

    public void setRateExtra(double rateExtra) {
        this.rateExtra = rateExtra;
    }

    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

    public void setPolygonOptions(PolygonOptions polygonOptions) {
        this.polygonOptions = polygonOptions;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }
}
