package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.VO.BkoOfferDate;
import com.blako.mensajero.Views.BkoOfferDetailActivity;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoTripsRecyclerAdapter extends RecyclerView.Adapter<BkoTripsRecyclerAdapter.ViewHolder> {

    private Integer lastIdOfferTaked;
    private Context context;
    private BkoOffersOwnListAdapter.OffersListListener listener;
    private List<BkoOffer.BkoAnnoucement> response;


    public BkoTripsRecyclerAdapter(Context mContext, List<BkoOffer.BkoAnnoucement> response, BkoOffersOwnListAdapter.OffersListListener listener, int dayPosition,Integer lastIdOfferTaked) {
        this.response = response;
        this.context = mContext;
        this.listener = listener;


    }
    private void showPopupMenu(View view, BkoOffer.BkoAnnoucement selectedItem, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.offer_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new BkoTripsRecyclerAdapter.MenuItemClickListener(selectedItem, position));
        popup.show();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        BkoOffer.BkoAnnoucement _selectedItem;
        int _position;

        public MenuItemClickListener(BkoOffer.BkoAnnoucement selectedItem, int position) {
            this._selectedItem = selectedItem;
            this._position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.detailtem:
                    listener.onOfferListener(_selectedItem, _position);

                    return true;
                case R.id.cancelltem:
                    listener.onOfferCancelListener(_selectedItem, _position);
                    return true;


                default:
            }
            return false;
        }
    }


    @Override
    public BkoTripsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoTripsRecyclerAdapter.ViewHolder viewHolder, final int position) {
        final BkoOffer.BkoAnnoucement selectedItem = response.get(position);







        try {

           viewHolder.aliasTv.setText(selectedItem.getBko_announcementaddress_alias() + " (" + selectedItem.getBko_announcement_numberworkers() + " cupos)");


            if (selectedItem.getServicetaken().equals("1")) {
                viewHolder. aliasTv.setText(selectedItem.getBko_announcementaddress_alias());
            }


            viewHolder._parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //swipeLayout.close(true);
                    // swipeLayout.close();
                    listener.onCheckInListener(selectedItem, position);


                }
            });

            viewHolder.shareIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(viewHolder.shareIv, selectedItem, position);
                }
            });

            viewHolder.detailtTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //swipeLayout.close(true);

                    // swipeLayout.close();
                    listener.onOfferListener(selectedItem, position);


                }
            });

            viewHolder.cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //   swipeLayout.close(true);
                    listener.onOfferCancelListener(selectedItem, position);
                    //swipeLayout.close();

                }
            });
            String rangeTime;
            if (selectedItem.getBko_announcement_finishstatus().equals("1")) {

                rangeTime =
                        BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimestart()).replace(":00 ", " ").toUpperCase().replace("PM", "").replace("P.M.", "").replace("AM", "").replace("A.M.", "") + " - " +
                                BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimefinish()).replace(":00 ", " ");

            } else {

                rangeTime =
                        BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimestart()).replace(":00 ", " ");
            }
            viewHolder.dateTv.setText(rangeTime);
            Picasso.with(context).load(selectedItem.getBko_customer_logo()).resize(150, 150).noFade().placeholder(ContextCompat.getDrawable(context, R.drawable.avatar)).networkPolicy(NetworkPolicy.NO_CACHE).into(viewHolder.checkIv);
            viewHolder.earnTv.setText("$" + selectedItem.getBko_announcement_bid());
            viewHolder.earnHourTv.setText("$" + selectedItem.getBko_announcement_costhourguaranteed());





            //if(BkoOfferDetailActivity.lastIdOfferTaked!=null && BkoOfferDetailActivity.lastIdOfferTaked.equals(selectedItem.getBko_announcement_id() ))
            if(lastIdOfferTaked!=null && lastIdOfferTaked== position)
            {
                BkoOfferDetailActivity.lastIdOfferTaked = null;



                int colorFrom = context.getResources().getColor(R.color.blako_green);
                int colorTo = context.getResources().getColor(R.color.blako_white);



                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                BkoOfferDetailActivity.lastIdOfferTaked = null;
                colorAnimation.setDuration(1350); // milliseconds
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                      viewHolder.forparent.setBackgroundColor((int) animator.getAnimatedValue());
                    }

                });

                colorAnimation.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        try {
                            if (position == 0 ) {
                                //greenV.setVisibility(View.VISIBLE);

                                if(BkoUtilities.formatToYesterdayOrToday(selectedItem.getBko_announcement_datetimestart()).toUpperCase().equals("HOY")){
                                    viewHolder. _parent.setBackground(ContextCompat.getDrawable(context, R.drawable.shadow));
                                }
                                else
                                    viewHolder._parent.setBackground(ContextCompat.getDrawable(context, R.drawable.shadowb));


                            } else {
                                if(BkoUtilities.formatToYesterdayOrToday(selectedItem.getBko_announcement_datetimestart()).toUpperCase().equals("HOY")){
                                    viewHolder. _parent.setBackground(ContextCompat.getDrawable(context, R.drawable.shadowb));
                                }
                                else
                                    viewHolder. _parent.setBackground(ContextCompat.getDrawable(context, R.drawable.shadowb));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });


                colorAnimation.start();
            }

            else
            {

                //greenV.setVisibility(View.VISIBLE);
                try {
                    if(BkoUtilities.formatToYesterdayOrToday(selectedItem.getBko_announcement_datetimestart()).toUpperCase().equals("HOY")){
                        if (position == 0 ) {

                            viewHolder._parent.setBackground(ContextCompat.getDrawable(context, R.drawable.shadow)) ;

                        } else {
                            // greenV.setVisibility(View.INVISIBLE);

                            viewHolder._parent.setBackground(ContextCompat.getDrawable(context, R.drawable.shadoww));
                        }
                    }

                    else
                        viewHolder._parent.setBackground(ContextCompat.getDrawable(context, R.drawable.shadowb));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }catch (Exception e){

        }



    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View _parent,forparent;
        private ImageView checkIv,shareIv;

        private TextView dateTv, aliasTv, detailtTv,cancelTv,earnTv,earnHourTv;


        public ViewHolder(View view) {
            super(view);
            forparent= (View) view.findViewById(R.id.forparent);
            _parent = (View) view.findViewById(R.id.parent);
            checkIv = (ImageView) view.findViewById(R.id.checkIv);
            shareIv = (ImageView) view.findViewById(R.id.shareIv);
            dateTv = (TextView) view.findViewById(R.id.dateTv);
            aliasTv = (TextView) view.findViewById(R.id.aliasTv);
            detailtTv = (TextView) view.findViewById(R.id.detailtTv);
            cancelTv = (TextView) view.findViewById(R.id.cancelTv);
            earnTv = (TextView) view.findViewById(R.id.earnTv);
            earnHourTv = (TextView) view.findViewById(R.id.earnHourTv);
        }

        public void bind(final int position, final BkoOffer.BkoAnnoucement selectedItem, final String title) {

            _parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onCheckInListener(selectedItem, position);


                }
            });
        }

    }

    public interface OffersDateListener {


        void onOfferListener(BkoOfferDate offer, String title);

    }

}
