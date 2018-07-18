package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoOffer;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoOffersGroupListAdapter extends RecyclerView.Adapter<BkoOffersGroupListAdapter.ViewHolder> implements BkoOffersListAdapter.OffersListListener {
    private Context context;
    private List<BkoOffer> offers;
    private BkoOffersListAdapter.OffersListListener listener;

    public BkoOffersGroupListAdapter(Context context, List<BkoOffer> offers, BkoOffersListAdapter.OffersListListener listener) {
        this.offers = offers;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BkoOffersGroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_offer_group_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoOffersGroupListAdapter.ViewHolder viewHolder, int position) {
        final BkoOffer selectedItem = offers.get(position);
        try {
            viewHolder.countTv.setText("" + selectedItem.getAnnouncement().size() + "");
            viewHolder.dayTv.setText(selectedItem.getAlias());

            if (!BkoUtilities.formatToYesterdayOrToday(selectedItem.getDate()).toUpperCase().equals("HOY")) {
                viewHolder.bind();
            } else {
                viewHolder.OffersDateRv.setVisibility(View.VISIBLE);
            }

            BkoOffersListAdapter adapter = new BkoOffersListAdapter(context, selectedItem.getAnnouncement(), this);


            //BkoTripsRecyclerAdapter adapter = new BkoTripsRecyclerAdapter(context, selectedItem.getAnnouncement(), this, position, newOfferPosition);
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
        return offers.size();
    }

    @Override
    public void onOfferListener(BkoOffer.BkoAnnoucement item) {
        listener.onOfferListener(item);
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
                        dayTv.setTextColor(ContextCompat.getColor(context,R.color.blako_background));
                    } else {
                        OffersDateRv.setVisibility(View.VISIBLE);
                        dayTv.setTextColor(ContextCompat.getColor(context,R.color.blako_gray));
                    }
                }
            });
        }

    }


}
