package com.blako.mensajero.VO;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 12/17/15.
 */
public class BkoUserLoginResponse extends BkoRequestResponse {

    private String workerId;


    private String name;
    private String lastname;
    private boolean otherdevice;
    private String workerParentId;
    private String visibilitytype;
    private String canbepublic;
    private String canbeprivate;
    private String enviromentId;
    private String enviromentUrl;

    //
    private String email;

    private String picurl;

    private String checkinexact;

    private String checkin_warehouse;
    private String warehouse_id;

    private String warehouse_name;

    private ArrayList<BkoOffer.BkoAnnoucement> announcementData;

    private String vehicles_brand;
    private String vehicles_carmodel;
    private String vehicles_id;

    public ArrayList<BkoOffer.BkoAnnoucement> getAnnouncementData() {
        return announcementData;
    }

    public void setAnnouncementData(ArrayList<BkoOffer.BkoAnnoucement> announcementData) {
        this.announcementData = announcementData;
    }

    public String getCheckin_warehouse() {
        return checkin_warehouse;
    }

    public void setCheckin_warehouse(String checkin_warehouse) {
        this.checkin_warehouse = checkin_warehouse;
    }

    public String getVehicles_brand() {
        return vehicles_brand;
    }

    public void setVehicles_brand(String vehicles_brand) {
        this.vehicles_brand = vehicles_brand;
    }

    public String getVehicles_carmodel() {
        return vehicles_carmodel;
    }

    public void setVehicles_carmodel(String vehicles_carmodel) {
        this.vehicles_carmodel = vehicles_carmodel;
    }

    public String getVehicles_id() {
        return vehicles_id;
    }

    public void setVehicles_id(String vehicles_id) {
        this.vehicles_id = vehicles_id;
    }


    public String getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(String warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public String getWarehouse_name() {
        return warehouse_name;
    }

    public void setWarehouse_name(String warehouse_name) {
        this.warehouse_name = warehouse_name;
    }

    public String getCheckinexact() {
        return checkinexact;
    }

    public void setCheckinexact(String checkinexact) {
        this.checkinexact = checkinexact;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnviromentId() {
        return enviromentId;
    }

    public void setEnviromentId(String enviromentId) {
        this.enviromentId = enviromentId;
    }

    public String getEnviromentUrl() {
        return enviromentUrl;
    }

    public void setEnviromentUrl(String enviromentUrl) {
        this.enviromentUrl = enviromentUrl;
    }

    public String getCanbeprivate() {
        return canbeprivate;
    }

    public void setCanbeprivate(String canbeprivate) {
        this.canbeprivate = canbeprivate;
    }

    public String getCanbepublic() {
        return canbepublic;
    }

    public void setCanbepublic(String canbepublic) {
        this.canbepublic = canbepublic;
    }


    public String getVisibilitytype() {
        return visibilitytype;
    }

    public void setVisibilitytype(String visibilitytype) {
        this.visibilitytype = visibilitytype;
    }

    public boolean isOtherdevice() {
        return otherdevice;
    }

    public void setOtherdevice(boolean otherdevice) {
        this.otherdevice = otherdevice;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerParentId() {
        return workerParentId;
    }

    public void setWorkerParentId(String workerParentId) {
        this.workerParentId = workerParentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


}
