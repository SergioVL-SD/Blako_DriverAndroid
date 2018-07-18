package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoBalanceResponse;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoBalanceTripAdapter extends RecyclerView.Adapter<BkoBalanceTripAdapter.ViewHolder> {
    private List<BkoBalanceResponse.tripBalance> response;

    BkoBalanceTripAdapter(List<BkoBalanceResponse.tripBalance> response) {
        this.response = response;
    }

    @Override
    public BkoBalanceTripAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_balance_trip_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoBalanceTripAdapter.ViewHolder viewHolder, int position) {
        final BkoBalanceResponse.tripBalance selectedItem = response.get(position);

        try {
            viewHolder.dateStartTv.setText(BkoUtilities.JsonDate(selectedItem.getBko_orders_trips_startdatetime()));
            viewHolder.dateEndTv.setText(BkoUtilities.JsonDate(selectedItem.getBko_orders_trips_completeddatetime()));
            viewHolder.costTv.setText("$" + selectedItem.getBko_orders_trips_cost());
            viewHolder.orderTv.setText("#" + selectedItem.getBko_orders_trips_id());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dateStartTv, costTv, dateEndTv, orderTv;

        public ViewHolder(View view) {
            super(view);
            dateStartTv = (TextView) view.findViewById(R.id.dateStartTv);
            dateEndTv = (TextView) view.findViewById(R.id.dateEndTv);
            costTv = (TextView) view.findViewById(R.id.costTv);
            orderTv = (TextView) view.findViewById(R.id.orderTv);
        }

    }


}
