package com.blako.mensajero.Dao;

import android.content.Context;

import com.blako.mensajero.DB.BkoDBManager;
import com.blako.mensajero.VO.BkoUser;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

/**
 * Created by franciscotrinidad on 10/7/15.
 */
public class BkoUserDao {

    public static BkoUser Consultar(Context prContext) throws Exception {
        BkoUser user = null;
        BkoDBManager databaseHelper = BkoDBManager.getHelper(prContext);

        try {
            QueryBuilder<BkoUser, Long> qbUser = databaseHelper.getUserDao().queryBuilder();
            user = qbUser.queryForFirst();
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            databaseHelper.close();
        }

        return user;
    }

    public static int Insertar(Context prContext, BkoUser user) {

        int CodigoResultado = 0;
        BkoDBManager databaseHelper = BkoDBManager.getHelper(prContext);
        try {


            CodigoResultado = databaseHelper.getUserDao().create(user);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.close();
        }
        return CodigoResultado;
    }

    public static int Actualizar(Context prContext, BkoUser user) {
        BkoDBManager databaseHelper = BkoDBManager.getHelper(prContext);
        int CodigoResultado = 0;
        try {
            CodigoResultado = databaseHelper.getUserDao().update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.close();
        }
        return CodigoResultado;
    }

    public static int Eliminar(Context prContext) {

        BkoDBManager databaseHelper = BkoDBManager.getHelper(prContext);
        int resultado = 0;
        try {
            DeleteBuilder<BkoUser, Long> dbEmisor = databaseHelper.getUserDao().deleteBuilder();
            resultado = dbEmisor.delete();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseHelper.close();
        }

        return resultado;
    }
}
