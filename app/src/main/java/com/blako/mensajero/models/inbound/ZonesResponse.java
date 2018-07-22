package com.blako.mensajero.models.inbound;

public class ZonesResponse {
    private String msg;
    private Boolean success;
    private Long timestamp;
    private ZonesInnerZonesResponse zones;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public ZonesInnerZonesResponse getZones() {
        return zones;
    }

    public void setZones(ZonesInnerZonesResponse zones) {
        this.zones = zones;
    }
}
