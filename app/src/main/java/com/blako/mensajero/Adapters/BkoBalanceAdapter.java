package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoBalanceResponse;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoBalanceAdapter extends RecyclerView.Adapter<BkoBalanceAdapter.ViewHolder> {
    private Context context;
    private List<BkoBalanceResponse.orderBalance> response;

    public BkoBalanceAdapter(Context context, List<BkoBalanceResponse.orderBalance> response) {
        this.response = response;
        this.context = context;
    }

    @Override
    public BkoBalanceAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_balance_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoBalanceAdapter.ViewHolder viewHolder, int position) {
        final BkoBalanceResponse.orderBalance selectedItem = response.get(position);
        viewHolder.bind(position, selectedItem);

        try {
            viewHolder.dateStartTv.setText(BkoUtilities.JsonDate(selectedItem.getDate_assignment()));
            viewHolder.dateEndTv.setText(BkoUtilities.JsonDate(selectedItem.getDate_finish()));
            viewHolder.costTv.setText("$" + selectedItem.getCost());
            viewHolder.orderTv.setText("#" + selectedItem.getIdorder());
            viewHolder.flagTv.setColorFilter(R.color.blako_background, android.graphics.PorterDuff.Mode.MULTIPLY);
            BkoBalanceTripAdapter adapter = new BkoBalanceTripAdapter(selectedItem.getTrips());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            viewHolder.balanceTripsRv.setLayoutManager(mLayoutManager);
            viewHolder.balanceTripsRv.setAdapter(adapter);

            if (!selectedItem.isOpen()) {
                viewHolder.flagTv.setImageResource(R.drawable.flag_down);
                viewHolder.balanceTripsRv.setVisibility(View.GONE);
            } else {
                viewHolder.flagTv.setImageResource(R.drawable.flag_up);
                viewHolder.balanceTripsRv.setVisibility(View.VISIBLE);
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

        private View parent;
        private ImageView flagTv;
        private TextView dateStartTv, costTv, dateEndTv, orderTv;
        private RecyclerView balanceTripsRv;

        public ViewHolder(View view) {
            super(view);
            parent = view.findViewById(R.id.parent);
            dateStartTv = (TextView) view.findViewById(R.id.dateStartTv);
            dateEndTv = (TextView) view.findViewById(R.id.dateEndTv);
            costTv = (TextView) view.findViewById(R.id.costTv);
            orderTv = (TextView) view.findViewById(R.id.orderTv);
            flagTv = (ImageView) view.findViewById(R.id.flagTv);
            balanceTripsRv = (RecyclerView) view.findViewById(R.id.balanceTripsRv);

        }

        public void bind(final int position, final BkoBalanceResponse.orderBalance item) {

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (item.isOpen()) {
                        response.get(position).setOpen(false);
                        flagTv.setImageResource(R.drawable.flag_down);
                        balanceTripsRv.setVisibility(View.GONE);
                    } else {
                        response.get(position).setOpen(true);
                        flagTv.setImageResource(R.drawable.flag_up);
                        balanceTripsRv.setVisibility(View.VISIBLE);
                    }
                }
            });


        }
    }
}
