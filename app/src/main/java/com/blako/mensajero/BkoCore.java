package com.blako.mensajero;

import com.blako.mensajero.VO.BkoOffer;

/**
 * Created by franciscotrinidad on 10/03/17.
 */

public class BkoCore {

    public static  OffersListener offersInterfaceListener;
    public static  OffersTripsListener offersTripsListener;

    public static void setoffersInterfaceListener(OffersListener listener)
    {

        offersInterfaceListener = listener;
    }

    public static void setoffersTripsListener(OffersTripsListener listener)
    {

        offersTripsListener = listener;
    }


    public interface OffersListener {
        void onLogoutListener(boolean status);
        void onCheckInListener(BkoOffer.BkoAnnoucement selectedItem);
        void onOfferListener(BkoOffer.BkoAnnoucement selectedItem);
        void onCancelListener(BkoOffer.BkoAnnoucement selectedItem);
    }


    public interface OffersTripsListener {
        void onCheckOut();

    }

}
