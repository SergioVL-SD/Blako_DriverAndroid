package com.blako.mensajero.DB;


/**
 * Created by franciscotrinidad on 10/7/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blako.mensajero.VO.BkoUser;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class BkoDBManager extends OrmLiteSqliteOpenHelper {
    private static final String TAG = BkoDBManager.class.getSimpleName();
    private static final String DATABASE_NAME = "bko.db";
    private static final int DATABASE_VERSION = 3;
    private Context ctx;
    private static BkoDBManager helper;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);
    private Dao<BkoUser, Long> userDao = null;
    private BkoDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;

    }

    public static synchronized BkoDBManager getHelper(Context context) {
        if (helper == null)
            helper = new BkoDBManager(context);
        usageCounter.incrementAndGet();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate");

            TableUtils.createTableIfNotExists(connectionSource, BkoUser.class);


        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            getUserDao().executeRaw("ALTER TABLE `user` ADD COLUMN announcementId STRING;");
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop database", e);
        }

        onCreate(database, connectionSource);
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            userDao = null;
            helper = null;

        }
    }

    public Dao<BkoUser, Long> getUserDao() throws SQLException {
        if (userDao == null) {
            try {
                userDao = getDao(BkoUser.class);
            } catch (SQLException e) {
                Log.e(TAG, "Can't get DAO.", e);
                throw new RuntimeException(e);
            }
        }
        return userDao;
    }




}
