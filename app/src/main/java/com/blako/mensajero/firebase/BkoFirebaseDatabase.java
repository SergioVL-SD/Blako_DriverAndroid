package com.blako.mensajero.firebase;

import com.google.firebase.database.FirebaseDatabase;

public class BkoFirebaseDatabase {

    private static FirebaseDatabase mFirebaseDatabase;

    public static FirebaseDatabase getDatabase(){
        if (mFirebaseDatabase==null){
            mFirebaseDatabase= FirebaseDatabase.getInstance();
            mFirebaseDatabase.setPersistenceEnabled(true);
        }
        return mFirebaseDatabase;
    }
}
