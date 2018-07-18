package com.blako.mensajero.VO;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 1/26/16.
 */
public class BkoBalanceResponse extends BkoRequestResponse {

    private String total_cost;

    private ArrayList<orderBalance> orders;

    public String getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(String total_cost) {
        this.total_cost = total_cost;
    }


    public ArrayList<orderBalance> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<orderBalance> orders) {
        this.orders = orders;
    }

    public static class orderBalance{

        private String idorder;

        private String cost;
        private String date_assignment;
        private String date_finish;
        private ArrayList<tripBalance> trips;
        private boolean isOpen;

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

        public ArrayList<tripBalance> getTrips() {
            return trips;
        }

        public void setTrips(ArrayList<tripBalance> trips) {
            this.trips = trips;
        }

        public String getIdorder() {
            return idorder;
        }

        public void setIdorder(String idorder) {
            this.idorder = idorder;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public String getDate_assignment() {
            return date_assignment;
        }

        public void setDate_assignment(String date_assignment) {
            this.date_assignment = date_assignment;
        }

        public String getDate_finish() {
            return date_finish;
        }

        public void setDate_finish(String date_finish) {
            this.date_finish = date_finish;
        }

        public String getDate_start() {
            return date_start;
        }

        public void setDate_start(String date_start) {
            this.date_start = date_start;
        }

        private String date_start;



    }

    public static class tripBalance{
        private String bko_orders_id;
        private String bko_orders_trips_completeddatetime;
        private String bko_orders_trips_cost;
        private String bko_orders_trips_date;
        private String bko_orders_trips_id;
        private String bko_orders_trips_startdatetime;


        public String getBko_orders_id() {
            return bko_orders_id;
        }

        public void setBko_orders_id(String bko_orders_id) {
            this.bko_orders_id = bko_orders_id;
        }

        public String getBko_orders_trips_completeddatetime() {
            return bko_orders_trips_completeddatetime;
        }

        public void setBko_orders_trips_completeddatetime(String bko_orders_trips_completeddatetime) {
            this.bko_orders_trips_completeddatetime = bko_orders_trips_completeddatetime;
        }

        public String getBko_orders_trips_cost() {
            return bko_orders_trips_cost;
        }

        public void setBko_orders_trips_cost(String bko_orders_trips_cost) {
            this.bko_orders_trips_cost = bko_orders_trips_cost;
        }

        public String getBko_orders_trips_date() {
            return bko_orders_trips_date;
        }

        public void setBko_orders_trips_date(String bko_orders_trips_date) {
            this.bko_orders_trips_date = bko_orders_trips_date;
        }

        public String getBko_orders_trips_id() {
            return bko_orders_trips_id;
        }

        public void setBko_orders_trips_id(String bko_orders_trips_id) {
            this.bko_orders_trips_id = bko_orders_trips_id;
        }

        public String getBko_orders_trips_startdatetime() {
            return bko_orders_trips_startdatetime;
        }

        public void setBko_orders_trips_startdatetime(String bko_orders_trips_startdatetime) {
            this.bko_orders_trips_startdatetime = bko_orders_trips_startdatetime;
        }
    }

}
