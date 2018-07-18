package com.blako.mensajero.VO;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by franciscotrinidad on 10/7/15.
 */
@DatabaseTable(tableName = BkoTablesNames.BKO_USER)
public class BkoUser extends BkoBaseVO {
    public final static String COL_NAME= "name";
    public final static String COL_WORKER_ID= "workerId";
    public final static String COL_WORKER_PARENT_ID = "workerParentId";
    public final static String COL_LAST_NAME = "lastname";
    public final static String COL_ENVIROMENT = "environment";
    public final static String COL_OTHER_DEVICE = "otherdevice";
    public final static String COL_EMAIL= "email";
    public final static String COL_AVAILABLE= "isAvailable";
    public final static String COL_CONECT_TOKEN= "conectToken";
    public final static String COL_VISIBLE_TYPE= "visibilitytype";
    public final static String COL_CAN_BE_PUBLIC= "canbepublic";
    public final static String COL_CAN_BE_PRIVATE= "canbeprivate";
    public final static String COL_PIC_URL= "picUrl";
    public final static String COL_TAKE_WAREHOUSE= "takeWareHouse";
    public final static String COL_WARE_HOUSE_SELECTED= "wareHouseSelected";
    public final static String COL_WARE_HOUSE_ID_SELECTED= "wareHouseIdSelected";
    public final static String COL_PRIVATE_CHECK_IN= "privateCheckIn";
    public final static String COL_ANNOUCEMENT_ID= "announcementId";

    @DatabaseField(columnName = COL_ANNOUCEMENT_ID)
    private String announcementId;
    @DatabaseField(columnName = COL_CAN_BE_PUBLIC)
    private String canbepublic;
    @DatabaseField(columnName = COL_CAN_BE_PRIVATE)
    private String canbeprivate;
    @DatabaseField(columnName = COL_NAME)
    private String name;
    @DatabaseField(columnName = COL_WORKER_ID)
    private String workerId;
    @DatabaseField(columnName = COL_WORKER_PARENT_ID)
    private String workerParentId;
    @DatabaseField(columnName = COL_LAST_NAME)
    private String lastname;
    @DatabaseField(columnName = COL_ENVIROMENT)
    private String environment;
    @DatabaseField(columnName = COL_OTHER_DEVICE)
    private boolean otherdevice;
    @DatabaseField(columnName = COL_EMAIL)
    private String email;
    @DatabaseField(columnName = COL_AVAILABLE)
    private boolean isAvailable;
    @DatabaseField(columnName = COL_CONECT_TOKEN)
    private String conectToken;
    @DatabaseField(columnName = COL_VISIBLE_TYPE)
    private String visibilitytype;
    @DatabaseField(columnName = COL_PIC_URL)
    private String picurl;
    @DatabaseField(columnName = COL_TAKE_WAREHOUSE)
    private String takeWareHouse;
    @DatabaseField(columnName = COL_WARE_HOUSE_SELECTED)
    private String wareHouseSelected;
    @DatabaseField(columnName = COL_WARE_HOUSE_ID_SELECTED)
    private String wareHouseIdSelected;

    @DatabaseField(columnName = COL_PRIVATE_CHECK_IN)
    private String privateCheckIn;


    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getPrivateCheckIn() {
        return privateCheckIn;
    }

    public void setPrivateCheckIn(String privateCheckIn) {
        this.privateCheckIn = privateCheckIn;
    }

    public String getWareHouseIdSelected() {
        return wareHouseIdSelected;
    }

    public void setWareHouseIdSelected(String wareHouseIdSelected) {
        this.wareHouseIdSelected = wareHouseIdSelected;
    }

    public String getWareHouseSelected() {
        return wareHouseSelected;
    }

    public void setWareHouseSelected(String wareHouseSelected) {
        this.wareHouseSelected = wareHouseSelected;
    }

    public String getTakeWareHouse() {
        return takeWareHouse;
    }

    public void setTakeWareHouse(String takeWareHouse) {
        this.takeWareHouse = takeWareHouse;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
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





    public String getConectToken() {
        return conectToken;
    }

    public void setConectToken(String conectToken) {
        this.conectToken = conectToken;
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean isOtherdevice() {
        return otherdevice;
    }

    public void setOtherdevice(boolean otherdevice) {
        this.otherdevice = otherdevice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
