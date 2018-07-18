package com.blako.mensajero.firebase;

import com.google.firebase.storage.FirebaseStorage;

public class BkoFirebaseStorage {

    private static FirebaseStorage mfirebaseStorage;

    public static FirebaseStorage getStorage(){
        if (mfirebaseStorage==null){
            mfirebaseStorage= FirebaseStorage.getInstance();
        }
        return mfirebaseStorage;
    }
}
