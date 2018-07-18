package com.blako.mensajero.VO;

import android.widget.SeekBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscotrinidad on 02/03/17.
 */

public class BkoOffer {
    private String Alias;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private ArrayList<BkoAnnoucement> announcement;

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public ArrayList<BkoAnnoucement> getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(ArrayList<BkoAnnoucement> announcement) {
        this.announcement = announcement;
    }

    public static class BkoAnnoucement implements Serializable {

        private String bko_announcement_id;
        private String bko_announcement_datetimestart;
        private String bko_announcement_finishstatus;
        private String bko_announcement_datetimefinish;
        private String bko_announcement_numberworkers;
        private String bko_announcement_bid;
        private String bko_announcement_numberservices;
        private String bko_campaign_id;
        private String bko_announcementaddress_id;
        private String bko_announcement_status;
        private String bko_announcementaddress_alias;
        private String bko_announcementaddress_street;
        private String bko_announcementaddress_numext;
        private String bko_announcementaddress_numint;
        private String bko_announcementaddress_neighborhood;
        private String bko_announcementaddress_province;
        private String bko_announcementaddress_state;
        private String bko_announcementaddress_city ;
        private String bko_announcementaddress_country;
        private String bko_announcementaddress_lat;
        private String bko_announcementaddress_lng;
        private String bko_announcementaddress_status;
        private String bko_customer_id;
        private String bko_campaign_name;
        private String bko_campaign_need;
        private String bko_vehiclestype_id;
        private String bko_campaign_numberservices;
        private String bko_campaign_bid;
        private String bko_workers_level;
        private String bko_campaign_title;
        private String bko_campaign_description;
        private String bko_campaign_brandvisible;
        private String bko_campaign_timeexpired;
        private String bko_campaign_status;
        private String bko_campaign_checkinexact;
        private String bko_deliverytype_id;
        private String bko_customer_name;
        private String bko_customer_logo;
        private String servicetaken;
        private String bko_announcementworker_onway;
        private String bko_announcementworker_checkin;
        private String bko_announcementworker_checkout;
        private boolean empty;
        private String bko_announcement_costhourguaranteed;
        private String minutes_tolerance;
        private List<BkoPenaltyResponse> penaltys;
        private boolean isheaderTitle;
        private String alias;
        private String time;
        private String date;
        private String bko_announcementworker_id;
        //pri


        public String getBko_announcementworker_id() {
            return bko_announcementworker_id;
        }

        public void setBko_announcementworker_id(String bko_announcementworker_id) {
            this.bko_announcementworker_id = bko_announcementworker_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public boolean isIsheaderTitle() {
            return isheaderTitle;
        }

        public void setIsheaderTitle(boolean isheaderTitle) {
            this.isheaderTitle = isheaderTitle;
        }

        public List<BkoPenaltyResponse> getPenaltys() {
            return penaltys;
        }

        public void setPenaltys(List<BkoPenaltyResponse> penaltys) {
            this.penaltys = penaltys;
        }

        public String getMinutes_tolerance() {
            return minutes_tolerance;
        }

        public void setMinutes_tolerance(String minutes_tolerance) {
            this.minutes_tolerance = minutes_tolerance;
        }

        public String getBko_announcement_costhourguaranteed() {
            return bko_announcement_costhourguaranteed;
        }

        public void setBko_announcement_costhourguaranteed(String bko_announcement_costhourguaranteed) {
            this.bko_announcement_costhourguaranteed = bko_announcement_costhourguaranteed;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }

        public String getBko_campaign_checkinexact() {
            return bko_campaign_checkinexact;
        }

        public void setBko_campaign_checkinexact(String bko_campaign_checkinexact) {
            this.bko_campaign_checkinexact = bko_campaign_checkinexact;
        }

        public String getBko_deliverytype_id() {
            return bko_deliverytype_id;
        }

        public void setBko_deliverytype_id(String bko_deliverytype_id) {
            this.bko_deliverytype_id = bko_deliverytype_id;
        }

        public String getBko_announcementworker_onway() {
            return bko_announcementworker_onway;
        }

        public void setBko_announcementworker_onway(String bko_announcementworker_onway) {
            this.bko_announcementworker_onway = bko_announcementworker_onway;
        }

        public String getBko_announcementworker_checkin() {
            return bko_announcementworker_checkin;
        }

        public void setBko_announcementworker_checkin(String bko_announcementworker_checkin) {
            this.bko_announcementworker_checkin = bko_announcementworker_checkin;
        }

        public String getBko_announcementworker_checkout() {
            return bko_announcementworker_checkout;
        }

        public void setBko_announcementworker_checkout(String bko_announcementworker_checkout) {
            this.bko_announcementworker_checkout = bko_announcementworker_checkout;
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

        public String getServicetaken() {
            return servicetaken;
        }

        public void setServicetaken(String servicetaken) {
            this.servicetaken = servicetaken;
        }

        private boolean title;

        public boolean isTitle() {
            return title;
        }

        public void setTitle(boolean title) {
            this.title = title;
        }

        public String getBko_announcement_id() {
            return bko_announcement_id;
        }

        public void setBko_announcement_id(String bko_announcement_id) {
            this.bko_announcement_id = bko_announcement_id;
        }

        public String getBko_announcement_datetimestart() {
            return bko_announcement_datetimestart;
        }

        public void setBko_announcement_datetimestart(String bko_announcement_datetimestart) {
            this.bko_announcement_datetimestart = bko_announcement_datetimestart;
        }

        public String getBko_announcement_finishstatus() {
            return bko_announcement_finishstatus;
        }

        public void setBko_announcement_finishstatus(String bko_announcement_finishstatus) {
            this.bko_announcement_finishstatus = bko_announcement_finishstatus;
        }

        public String getBko_announcement_datetimefinish() {
            return bko_announcement_datetimefinish;
        }

        public void setBko_announcement_datetimefinish(String bko_announcement_datetimefinish) {
            this.bko_announcement_datetimefinish = bko_announcement_datetimefinish;
        }

        public String getBko_announcement_numberworkers() {
            return bko_announcement_numberworkers;
        }

        public void setBko_announcement_numberworkers(String bko_announcement_numberworkers) {
            this.bko_announcement_numberworkers = bko_announcement_numberworkers;
        }

        public String getBko_announcement_bid() {
            return bko_announcement_bid;
        }

        public void setBko_announcement_bid(String bko_announcement_bid) {
            this.bko_announcement_bid = bko_announcement_bid;
        }

        public String getBko_announcement_numberservices() {
            return bko_announcement_numberservices;
        }

        public void setBko_announcement_numberservices(String bko_announcement_numberservices) {
            this.bko_announcement_numberservices = bko_announcement_numberservices;
        }

        public String getBko_campaign_id() {
            return bko_campaign_id;
        }

        public void setBko_campaign_id(String bko_campaign_id) {
            this.bko_campaign_id = bko_campaign_id;
        }

        public String getBko_announcementaddress_id() {
            return bko_announcementaddress_id;
        }

        public void setBko_announcementaddress_id(String bko_announcementaddress_id) {
            this.bko_announcementaddress_id = bko_announcementaddress_id;
        }

        public String getBko_announcement_status() {
            return bko_announcement_status;
        }

        public void setBko_announcement_status(String bko_announcement_status) {
            this.bko_announcement_status = bko_announcement_status;
        }

        public String getBko_announcementaddress_alias() {
            return bko_announcementaddress_alias;
        }

        public void setBko_announcementaddress_alias(String bko_announcementaddress_alias) {
            this.bko_announcementaddress_alias = bko_announcementaddress_alias;
        }

        public String getBko_announcementaddress_street() {
            return bko_announcementaddress_street;
        }

        public void setBko_announcementaddress_street(String bko_announcementaddress_street) {
            this.bko_announcementaddress_street = bko_announcementaddress_street;
        }

        public String getBko_announcementaddress_numext() {
            return bko_announcementaddress_numext;
        }

        public void setBko_announcementaddress_numext(String bko_announcementaddress_numext) {
            this.bko_announcementaddress_numext = bko_announcementaddress_numext;
        }

        public String getBko_announcementaddress_numint() {
            return bko_announcementaddress_numint;
        }

        public void setBko_announcementaddress_numint(String bko_announcementaddress_numint) {
            this.bko_announcementaddress_numint = bko_announcementaddress_numint;
        }

        public String getBko_announcementaddress_neighborhood() {
            return bko_announcementaddress_neighborhood;
        }

        public void setBko_announcementaddress_neighborhood(String bko_announcementaddress_neighborhood) {
            this.bko_announcementaddress_neighborhood = bko_announcementaddress_neighborhood;
        }

        public String getBko_announcementaddress_province() {
            return bko_announcementaddress_province;
        }

        public void setBko_announcementaddress_province(String bko_announcementaddress_province) {
            this.bko_announcementaddress_province = bko_announcementaddress_province;
        }

        public String getBko_announcementaddress_state() {
            return bko_announcementaddress_state;
        }

        public void setBko_announcementaddress_state(String bko_announcementaddress_state) {
            this.bko_announcementaddress_state = bko_announcementaddress_state;
        }

        public String getBko_announcementaddress_city() {
            return bko_announcementaddress_city;
        }

        public void setBko_announcementaddress_city(String bko_announcementaddress_city) {
            this.bko_announcementaddress_city = bko_announcementaddress_city;
        }

        public String getBko_announcementaddress_country() {
            return bko_announcementaddress_country;
        }

        public void setBko_announcementaddress_country(String bko_announcementaddress_country) {
            this.bko_announcementaddress_country = bko_announcementaddress_country;
        }

        public String getBko_announcementaddress_lat() {
            return bko_announcementaddress_lat;
        }

        public void setBko_announcementaddress_lat(String bko_announcementaddress_lat) {
            this.bko_announcementaddress_lat = bko_announcementaddress_lat;
        }

        public String getBko_announcementaddress_lng() {
            return bko_announcementaddress_lng;
        }

        public void setBko_announcementaddress_lng(String bko_announcementaddress_lng) {
            this.bko_announcementaddress_lng = bko_announcementaddress_lng;
        }

        public String getBko_announcementaddress_status() {
            return bko_announcementaddress_status;
        }

        public void setBko_announcementaddress_status(String bko_announcementaddress_status) {
            this.bko_announcementaddress_status = bko_announcementaddress_status;
        }

        public String getBko_customer_id() {
            return bko_customer_id;
        }

        public void setBko_customer_id(String bko_customer_id) {
            this.bko_customer_id = bko_customer_id;
        }

        public String getBko_campaign_name() {
            return bko_campaign_name;
        }

        public void setBko_campaign_name(String bko_campaign_name) {
            this.bko_campaign_name = bko_campaign_name;
        }

        public String getBko_campaign_need() {
            return bko_campaign_need;
        }

        public void setBko_campaign_need(String bko_campaign_need) {
            this.bko_campaign_need = bko_campaign_need;
        }

        public String getBko_vehiclestype_id() {
            return bko_vehiclestype_id;
        }

        public void setBko_vehiclestype_id(String bko_vehiclestype_id) {
            this.bko_vehiclestype_id = bko_vehiclestype_id;
        }

        public String getBko_campaign_numberservices() {
            return bko_campaign_numberservices;
        }

        public void setBko_campaign_numberservices(String bko_campaign_numberservices) {
            this.bko_campaign_numberservices = bko_campaign_numberservices;
        }

        public String getBko_campaign_bid() {
            return bko_campaign_bid;
        }

        public void setBko_campaign_bid(String bko_campaign_bid) {
            this.bko_campaign_bid = bko_campaign_bid;
        }

        public String getBko_workers_level() {
            return bko_workers_level;
        }

        public void setBko_workers_level(String bko_workers_level) {
            this.bko_workers_level = bko_workers_level;
        }

        public String getBko_campaign_title() {
            return bko_campaign_title;
        }

        public void setBko_campaign_title(String bko_campaign_title) {
            this.bko_campaign_title = bko_campaign_title;
        }

        public String getBko_campaign_description() {
            return bko_campaign_description;
        }

        public void setBko_campaign_description(String bko_campaign_description) {
            this.bko_campaign_description = bko_campaign_description;
        }

        public String getBko_campaign_brandvisible() {
            return bko_campaign_brandvisible;
        }

        public void setBko_campaign_brandvisible(String bko_campaign_brandvisible) {
            this.bko_campaign_brandvisible = bko_campaign_brandvisible;
        }

        public String getBko_campaign_timeexpired() {
            return bko_campaign_timeexpired;
        }

        public void setBko_campaign_timeexpired(String bko_campaign_timeexpired) {
            this.bko_campaign_timeexpired = bko_campaign_timeexpired;
        }

        public String getBko_campaign_status() {
            return bko_campaign_status;
        }

        public void setBko_campaign_status(String bko_campaign_status) {
            this.bko_campaign_status = bko_campaign_status;
        }
    }
}
