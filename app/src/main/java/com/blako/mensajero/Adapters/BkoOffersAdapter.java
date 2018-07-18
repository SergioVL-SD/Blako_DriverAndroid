package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blako.mensajero.BkoCore;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoOffer;
import com.blako.mensajero.Views.BkoOfferDetailActivity;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoOffersAdapter extends RecyclerView.Adapter<BkoOffersAdapter.ViewHolder> implements BkoOffersOwnListAdapter.OffersListListener {

    private Context context;
    private List<BkoOffer> response;
    private BkoCore.OffersListener listener;

    public BkoOffersAdapter(Context context, List<BkoOffer> response, BkoCore.OffersListener listener) {
        this.response = response;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BkoOffersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_offer_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoOffersAdapter.ViewHolder viewHolder, int position) {
        final BkoOffer selectedItem = response.get(position);
        try {
            viewHolder.countTv.setText("" + selectedItem.getAnnouncement().size() + " OFERTAS");
            viewHolder.dayTv.setText(BkoUtilities.formatToYesterdayOrToday(selectedItem.getDate()).toUpperCase() + " (" + selectedItem.getAnnouncement().size() + ")");

            if (!BkoUtilities.formatToYesterdayOrToday(selectedItem.getDate()).toUpperCase().equals("HOY")) {
                viewHolder.dayTv.setBackgroundColor(ContextCompat.getColor(context, R.color.blako_background));
                viewHolder.bind();
            } else {
                viewHolder.OffersDateRv.setVisibility(View.VISIBLE);
            }

            Integer newOfferPosition = null;
            if (BkoOfferDetailActivity.lastIdOfferTaked != null) {
                int i = 0;
                for (BkoOffer.BkoAnnoucement annoucement : selectedItem.getAnnouncement()) {
                    if (BkoOfferDetailActivity.lastIdOfferTaked.equals(annoucement.getBko_announcement_id())) {

                        newOfferPosition = i;
                        break;
                    }
                    i++;
                }
            }
            BkoTripsRecyclerAdapter adapter = new BkoTripsRecyclerAdapter(context, selectedItem.getAnnouncement(), this, position, newOfferPosition);
            viewHolder.OffersDateRv.setAdapter(adapter);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            viewHolder.OffersDateRv.setLayoutManager(mLayoutManager);
            viewHolder.OffersDateRv.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return response.size();
    }

    @Override
    public void onCheckInListener(BkoOffer.BkoAnnoucement item, int position) {
        listener.onCheckInListener(item);
    }

    @Override
    public void onOfferListener(BkoOffer.BkoAnnoucement item, int position) {
        listener.onOfferListener(item);
    }

    @Override
    public void onOfferCancelListener(BkoOffer.BkoAnnoucement item, int position) {
        listener.onCancelListener(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View parent;
        private TextView dayTv, countTv;
        private RecyclerView OffersDateRv;

        public ViewHolder(View view) {
            super(view);
            parent = view.findViewById(R.id.parent);
            dayTv = (TextView) view.findViewById(R.id.dayTv);
            countTv = (TextView) view.findViewById(R.id.countTv);
            OffersDateRv = (RecyclerView) view.findViewById(R.id.OffersDateRv);

        }

        void bind() {

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (OffersDateRv.getVisibility() == View.VISIBLE) {
                        OffersDateRv.setVisibility(View.GONE);
                    } else {
                        OffersDateRv.setVisibility(View.VISIBLE);
                    }


                }
            });
        }

    }


}
