package com.blako.mensajero.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blako.mensajero.BkoDataMaganer;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoChildTripVO;
import com.blako.mensajero.VO.BkoTripVO;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by franciscotrinidad on 1/12/16.
 */
public class BkoTripsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BkoTripVO> currentTrips;
    private boolean playing;
    public static MediaPlayer mp;

    public BkoTripsAdapter(Context context, ArrayList<BkoTripVO> currentTrips) {
        this.currentTrips = currentTrips;
        this.context = context;

        if (mp != null && mp.isPlaying())
            mp.stop();
    }

    @Override
    public int getCount() {
        return currentTrips.size();
    }

    @Override
    public BkoTripVO getItem(int position) {
        return currentTrips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.bko_trip_item, parent, false);
            holder = new ViewHolder();
            holder.placeNameTv = (TextView) convertView.findViewById(R.id.placeNameTv);
            holder.statusTv = (TextView) convertView.findViewById(R.id.statusTv);
            holder.ordetTv = (TextView) convertView.findViewById(R.id.ordetTv);
            holder.newTv = (TextView) convertView.findViewById(R.id.newTv);
            holder.timeBackTv = (TextView) convertView.findViewById(R.id.timeBackTv);

            holder.timeTripTv = (TextView) convertView.findViewById(R.id.timeTripTv);
            holder.parent = convertView.findViewById(R.id.parent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BkoChildTripVO trip = getItem(position).getOrigen().get(0);
        BkoChildTripVO tripDelivery = getItem(position).getDestino().get(0);
        holder.placeNameTv.setText(BkoUtilities.ensureNotNullString(trip.getBko_customeraddress_alias()));

        if (trip.getBko_orders_trips_completeddatetime() != null && trip.getBko_orders_trips_completeddatetime().length() != 0) {
            trip = getItem(position).getDestino().get(0);
            holder.placeNameTv.setText(BkoUtilities.ensureNotNullString(tripDelivery.getBko_customeraddress_contact()));
            if (trip.getBko_orders_trips_startdatetime() == null || trip.getBko_orders_trips_startdatetime().length() == 0)
                holder.statusTv.setText(context.getString(R.string.blako_trips_status_tres));
            else
                holder.statusTv.setText(context.getString(R.string.blako_trips_status_cuatro));

            if (trip.getBko_orders_trips_item_confirm() != null)
                holder.statusTv.setText(context.getString(R.string.blako_trips_status_cinco));

            if(BkoDataMaganer.getCurrentOffer(context)== null &&!BkoDataMaganer.getOnDemand(context)){
                BkoDataMaganer.setOnDemand(context,true);
            }

        } else {

            if (tripDelivery.getBko_requeststatus_id() != null && tripDelivery.getBko_requeststatus_id().equals("2")) {
                holder.placeNameTv.setTextColor(context.getResources().getColor(R.color.blako_background));
                holder.ordetTv.setTextColor(context.getResources().getColor(R.color.blako_background));
                holder.statusTv.setBackgroundColor(context.getResources().getColor(R.color.blako_green));
                holder.statusTv.setAlpha(1.0f);
                holder.statusTv.setVisibility(View.VISIBLE);
                holder.newTv.setVisibility(View.GONE);
                holder.timeBackTv.setBackgroundColor(context.getResources().getColor(R.color.blako_green));
                holder.timeBackTv.setAlpha(1.0f);
                if(BkoDataMaganer.getCurrentOffer(context)== null &&!BkoDataMaganer.getOnDemand(context)){
                    BkoDataMaganer.setOnDemand(context,true);
                }

            } else {


                    holder.statusTv.setAlpha(0.2f);
                    holder.statusTv.setBackgroundColor(context.getResources().getColor(R.color.blako_white));
                    holder.timeBackTv.setBackgroundColor(context.getResources().getColor(R.color.blako_white));
                    holder.timeBackTv.setAlpha(0.1f);
                    holder.statusTv.setText(context.getString(R.string.blako_trips_status_cero));
                    holder.newTv.setVisibility(View.VISIBLE);
                    holder.placeNameTv.setTextColor(context.getResources().getColor(R.color.blako_white));
                    holder.ordetTv.setTextColor(context.getResources().getColor(R.color.blako_white));


                    if (!playing ) {
                        mp = MediaPlayer.create(context, R.raw.bells_blako);
                        mp.setLooping(true);
                        mp.start();
                        playing = true;
                    }


                    if (! BkoDataMaganer.getOnDemand(context) && BkoDataMaganer.getCurrentOffer(context)==null) {
                        if (mp != null && mp.isPlaying())
                            mp.stop();
                    }





                    int colorFrom = context.getResources().getColor(R.color.blako_white);
                    int colorTo = context.getResources().getColor(R.color.blako_green);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(1400);
                    colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
                    colorAnimation.setRepeatCount(10000);
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            try {
                                holder.parent.setBackgroundColor((int) animator.getAnimatedValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });
                    colorAnimation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }
                    });
                    colorAnimation.start();





            }




        }
        if (trip.getBko_queuedtasks_shipping_schedule() != null && !trip.getBko_queuedtasks_shipping_schedule().equals("0") && trip.getBko_queuedtasks_shipping_schedule().length() != 0) {


            CountDownTimer timer;
            long end;
            final Date now = new Date();
            final Date dateTrip = BkoUtilities.getDate(trip.getBko_queuedtasks_shipping_schedule(), "yyyy-MM-dd HH:mm:ss");
            end = now.getTime() - dateTrip.getTime();

            new CountDownTimer(end, 1000) {

                public void onTick(long a) {
                    try {

                        Date now = new Date();
                        long end = now.getTime() - dateTrip.getTime();

                        long hoursL = ((end / (1000 * 60 * 60)) % 24);
                        long minutesL = ((end / (1000 * 60)) % 60);
                        long secondsL = ((end / 1000) % 60);
                        String seconds = secondsL < 10 ? "0" + secondsL : "" + secondsL;
                        String minutes = minutesL < 10 ? "0" + minutesL : "" + minutesL;
                        String hours = hoursL < 10 ? "0" + hoursL : "" + hoursL;
                        holder.timeTripTv.setText(hours + ":" + minutes + ":" + seconds + " hrs.");

                        if (hoursL > 0) {
                            //holder.timeBackTv.setAlpha(0.1f);
                            holder.timeBackTv.setBackgroundColor(ContextCompat.getColor(context,R.color.blako_red));
                        }
                        else if (minutesL < 45 &&minutesL>=30 ) {
                            holder.timeBackTv.setBackgroundColor(ContextCompat.getColor(context,R.color.blako_orange));
                        }
                        else if (minutesL >= 45) {
                            //holder.timeBackTv.setAlpha(0.1f);
                            holder.timeBackTv.setBackgroundColor(ContextCompat.getColor(context,R.color.blako_red));
                        }


                    }catch (Exception e){

                    }

                  //  if(minutes>30){

                   // }
                }

                public void onFinish() {

                }

            }.start();
        }
        holder.ordetTv.setText(BkoUtilities.ensureNotNullString(tripDelivery.getBko_customeraddress_alias()));
        return convertView;
    }


    static class ViewHolder {
        View parent;
        TextView placeNameTv;
        TextView ordetTv;
        TextView statusTv;
        TextView newTv;
        TextView timeTripTv;
        TextView  timeBackTv;
    }
}
