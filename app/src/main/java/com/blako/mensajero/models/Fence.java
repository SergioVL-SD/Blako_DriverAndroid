package com.blako.mensajero.models;

import java.util.List;

public class Fence {
    private Integer id;
    private String label;
    private Double value;
    private List<Double> lats;
    private List<Double> lons;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<Double> getLats() {
        return lats;
    }

    public void setLats(List<Double> lats) {
        this.lats = lats;
    }

    public List<Double> getLons() {
        return lons;
    }

    public void setLons(List<Double> lons) {
        this.lons = lons;
    }
}
