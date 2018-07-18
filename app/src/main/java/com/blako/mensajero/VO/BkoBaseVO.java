package com.blako.mensajero.VO;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by franciscotrinidad on 10/7/15.
 */

public abstract class BkoBaseVO {
    public final static String COL_ID = "_id";

    @DatabaseField(generatedId = true, columnName = COL_ID)
    protected Long _id;

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

}