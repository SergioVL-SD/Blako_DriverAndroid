package com.blako.mensajero.DB;

import android.provider.BaseColumns;

public class DbContracts {

    public static class HubEntry implements BaseColumns {
        public static final String TABLE_NAME= "tbl_hubs";

        public static final String HUB_ID= "hubId";
        public static final String LABEL= "label";
        public static final String REVISION= "revision";
        public static final String REGION_ID= "regionId";
        public static final String STATUS= "status";
    }

    public static class PointEntry implements BaseColumns {
        public static final String TABLE_NAME= "tbl_points";

        public static final String HUB_ID= "hubId";
        public static final String LAT= "lat";
        public static final String LON= "lon";
    }

    public static class DefaultValueEntry implements BaseColumns {
        public static final String TABLE_NAME= "tbl_default_values";

        public static final String HUB_ID= "hubId";
        public static final String VALUE= "value";
        public static final String RATE= "rate";
        public static final String REGION_ID= "regionId";
    }
}
