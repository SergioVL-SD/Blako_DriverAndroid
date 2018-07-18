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
import com.blako.mensajero.VO.BkoOfferGroupResponse;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoOffersDayAdapter extends RecyclerView.Adapter<BkoOffersDayAdapter.ViewHolder> {
    public static String date;
    private Context context;
    private List<BkoOfferGroupResponse> response;
    private OffersDayListener listener;

    public BkoOffersDayAdapter(Context context, List<BkoOfferGroupResponse> response, OffersDayListener listener) {
        this.response = response;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BkoOffersDayAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_offer_day_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoOffersDayAdapter.ViewHolder viewHolder, int position) {
        final BkoOfferGroupResponse selectedItem = response.get(position);
        try {
            viewHolder.countTv.setText("(" + selectedItem.getNumber_offer() + " OFERTAS)");
            viewHolder.dayTv.setText(selectedItem.getBko_customer_name());
            viewHolder.bind(selectedItem, "(" + selectedItem.getNumber_offer() + " OFERTAS)");
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
        private TextView dayTv, countTv;

        public ViewHolder(View view) {
            super(view);
            parent = view.findViewById(R.id.parent);
            checkIv = (ImageView) view.findViewById(R.id.checkIv);
            dayTv = (TextView) view.findViewById(R.id.dayTv);
            countTv = (TextView) view.findViewById(R.id.countTv);

        }

        void bind(final BkoOfferGroupResponse item, final String title) {

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onOfferListener(item, title);
                }
            });
        }

    }

    public interface OffersDayListener {
        void onOfferListener(BkoOfferGroupResponse items, String title);
    }

}
