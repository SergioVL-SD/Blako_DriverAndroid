package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class BkoOffersListAdapter extends RecyclerView.Adapter<BkoOffersListAdapter.ViewHolder> {
    private Context context;
    private List<BkoOffer.BkoAnnoucement> response;
    private OffersListListener listener;

    public BkoOffersListAdapter(Context context, List<BkoOffer.BkoAnnoucement> response, OffersListListener listener) {
        this.response = response;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BkoOffersListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_offer_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoOffersListAdapter.ViewHolder viewHolder, int position) {
        final BkoOffer.BkoAnnoucement selectedItem = response.get(position);


        try {
            if (selectedItem.isIsheaderTitle()) {
                viewHolder.headerV.setVisibility(View.VISIBLE);
                viewHolder.offerV.setVisibility(View.GONE);
                viewHolder.titleTv.setText(selectedItem.getAlias());
            } else {
                viewHolder.headerV.setVisibility(View.GONE);
                viewHolder.offerV.setVisibility(View.VISIBLE);
                viewHolder.aliasTv.setText(selectedItem.getBko_announcementaddress_alias());

                if (selectedItem.getServicetaken().equals("1")) {
                    viewHolder.aliasTv.setText(selectedItem.getBko_announcementaddress_alias());
                }

                viewHolder.bind(selectedItem);
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

            }
        } catch (Exception e) {
            e.fillInStackTrace();

        }
    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View parent, headerV, offerV;
        private ImageView checkIv;
        private TextView dateTv, aliasTv, titleTv, earnTv, earnHourTv;


        public ViewHolder(View view) {
            super(view);
            headerV = view.findViewById(R.id.headerV);
            offerV = view.findViewById(R.id.offerV);
            parent = view.findViewById(R.id.parent);
            checkIv = (ImageView) view.findViewById(R.id.checkIv);
            dateTv = (TextView) view.findViewById(R.id.dateTv);
            aliasTv = (TextView) view.findViewById(R.id.aliasTv);
            titleTv = (TextView) view.findViewById(R.id.titleTv);
            earnTv = (TextView) view.findViewById(R.id.earnTv);
            earnHourTv = (TextView) view.findViewById(R.id.earnHourTv);
        }

        public void bind(final BkoOffer.BkoAnnoucement item) {
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!item.isIsheaderTitle())
                        listener.onOfferListener(item);
                }
            });
        }

    }

    public interface OffersListListener {
        void onOfferListener(BkoOffer.BkoAnnoucement item);
    }

}
