package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoOffer;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoOffersOwnListAdapter extends RecyclerView.Adapter<BkoOffersOwnListAdapter.ViewHolder> {

    private Context context;
    private List<BkoOffer.BkoAnnoucement> response;
    private OffersListListener listener;

    public BkoOffersOwnListAdapter(Context context, List<BkoOffer.BkoAnnoucement> response, OffersListListener listener) {
        this.response = response;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BkoOffersOwnListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_offer_own_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoOffersOwnListAdapter.ViewHolder viewHolder, int position) {
        final BkoOffer.BkoAnnoucement selectedItem = response.get(position);
        try {
            viewHolder.aliasTv.setText(selectedItem.getBko_announcementaddress_alias() + " (" + selectedItem.getBko_announcement_numberworkers() + " cupos)");

            if (selectedItem.getServicetaken().equals("1")) {
                viewHolder.aliasTv.setText(selectedItem.getBko_announcementaddress_alias());
            }

            viewHolder.bind(position, selectedItem);
            String rangeTime;
            if (selectedItem.getBko_announcement_finishstatus().equals("1")) {
                rangeTime =
                        BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimestart()).toUpperCase().replace("AM", "").replace("am", "").replace("PM", "").replace("pm", "") + " - " +
                                BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimefinish()).toUpperCase().replace("AM", "").replace("am", "").replace("PM", "").replace("pm", "") + " Hrs.";

            } else {
                rangeTime =
                        BkoUtilities.formateDateFromstring(selectedItem.getBko_announcement_datetimestart()).toUpperCase().toUpperCase().replace("AM", "").replace("am", "").replace("PM", "").replace("pm", "") + " Hrs.";
            }
            viewHolder.dateTv.setText(rangeTime);
            Picasso.with(context).load(selectedItem.getBko_customer_logo()).resize(150, 150).noFade().placeholder(ContextCompat.getDrawable(context, R.drawable.avatar)).networkPolicy(NetworkPolicy.NO_CACHE).into(viewHolder.checkIv);

        } catch (Exception e) {
            e.fillInStackTrace();

        }


    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View parent;
        private ImageView checkIv;
        private Button cancelBt;
        private TextView dateTv, aliasTv;


        public ViewHolder(View view) {
            super(view);
            parent = view.findViewById(R.id.parent);
            checkIv = (ImageView) view.findViewById(R.id.checkIv);
            dateTv = (TextView) view.findViewById(R.id.dateTv);
            aliasTv = (TextView) view.findViewById(R.id.aliasTv);
            cancelBt = (Button) view.findViewById(R.id.cancelBt);
        }

        public void bind(final int position, final BkoOffer.BkoAnnoucement item) {

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onOfferListener(item, position);
                }
            });

            cancelBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onOfferCancelListener(item, position);
                }
            });
        }

    }

    public interface OffersListListener {

        void onCheckInListener(BkoOffer.BkoAnnoucement item, int position);

        void onOfferListener(BkoOffer.BkoAnnoucement item, int position);

        void onOfferCancelListener(BkoOffer.BkoAnnoucement item, int positon);
    }

}
