package com.blako.mensajero.VO;

/**
 * Created by ascenzo on 1/30/16.
 */
public class BkoCompleteResponse extends BkoRequestResponse{

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    private boolean upload;
}
