package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoServiceNoOfferResponse;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoServicesNoOfferAdapter extends RecyclerView.Adapter<BkoServicesNoOfferAdapter.ViewHolder> {
    private List<BkoServiceNoOfferResponse.BkoServiceNoOfferVO> response;

    public BkoServicesNoOfferAdapter(List<BkoServiceNoOfferResponse.BkoServiceNoOfferVO> response) {
        this.response = response;
    }

    @Override
    public BkoServicesNoOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_service_no_offer_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoServicesNoOfferAdapter.ViewHolder viewHolder, int position) {
        final BkoServiceNoOfferResponse.BkoServiceNoOfferVO selectedItem = response.get(position);
        try {
            viewHolder.clientTv.setText(BkoUtilities.ensureNotNullString(selectedItem.getBko_customeraddress_alias()));
            viewHolder.orderTv.setText(BkoUtilities.ensureNotNullString(selectedItem.getBko_queuedtasks_receiver_alias()));
            viewHolder.dateTv.setText(BkoUtilities.ensureNotNullString(selectedItem.getBko_orders_daterequest()));
            viewHolder.totalTv.setText("$" + BkoUtilities.ensureNotNullString(selectedItem.getBko_orders_total()));
        } catch (Exception e) {
            e.fillInStackTrace();

        }


    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderTv, clientTv, dateTv, totalTv;

        public ViewHolder(View view) {
            super(view);
            orderTv = (TextView) view.findViewById(R.id.orderTv);
            clientTv = (TextView) view.findViewById(R.id.clientTv);
            dateTv = (TextView) view.findViewById(R.id.dateTv);
            totalTv = (TextView) view.findViewById(R.id.totalTv);

        }

    }


}
