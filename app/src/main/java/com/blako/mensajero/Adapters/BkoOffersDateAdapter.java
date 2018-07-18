package com.blako.mensajero.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoOfferDate;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoOffersDateAdapter extends RecyclerView.Adapter<BkoOffersDateAdapter.ViewHolder> {
    private List<BkoOfferDate> response;
    private OffersDateListener listener;

    public BkoOffersDateAdapter(List<BkoOfferDate> response, OffersDateListener listener) {
        this.response = response;
        this.listener = listener;
    }

    @Override
    public BkoOffersDateAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_offer_date_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoOffersDateAdapter.ViewHolder viewHolder, int position) {
        final BkoOfferDate selectedItem = response.get(position);
        try {
            viewHolder.countTv.setText("" + selectedItem.getNumberAnnouncement());
            viewHolder.dayTv.setText("" + BkoUtilities.formatToYesterdayOrToday(selectedItem.getDate()).toUpperCase());
            viewHolder.dateTv.setText("");
            String[] dateSplit = BkoUtilities.formatToYesterdayOrToday(selectedItem.getDate()).toUpperCase().split(" ");
            if (dateSplit.length != 0) {
                viewHolder.dayTv.setText("" + dateSplit[0]);
                viewHolder.dateTv.setText(("" + BkoUtilities.formatToYesterdayOrToday(selectedItem.getDate()).toUpperCase()).replace(dateSplit[0],"").toLowerCase());
            }
            viewHolder.bind(selectedItem, BkoUtilities.formatToYesterdayOrToday(selectedItem.getDate()).toUpperCase() + " " + " (" + selectedItem.getNumberAnnouncement() + " OFERTAS)");
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
        private TextView dayTv, countTv, dateTv;


        public ViewHolder(View view) {
            super(view);
            parent = view.findViewById(R.id.parent);
            dayTv = (TextView) view.findViewById(R.id.dayTv);
            countTv = (TextView) view.findViewById(R.id.countTv);
            dateTv = (TextView) view.findViewById(R.id.dateTv);
        }

        void bind(final BkoOfferDate item, final String title) {
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onOfferListener(item, title);
                }
            });
        }

    }

    public interface OffersDateListener {
        void onOfferListener(BkoOfferDate offer, String title);
    }

}
