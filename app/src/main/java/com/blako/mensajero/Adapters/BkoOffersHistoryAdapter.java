package com.blako.mensajero.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoOfferHistoryResponse;

import java.util.List;

/**
 * Created by ascenzo on 10/13/16.
 */
public class BkoOffersHistoryAdapter extends RecyclerView.Adapter<BkoOffersHistoryAdapter.ViewHolder> {
    private List<BkoOfferHistoryResponse.BkoOfferHistoryVO> response;


    public BkoOffersHistoryAdapter(List<BkoOfferHistoryResponse.BkoOfferHistoryVO> response) {
        this.response = response;
    }

    @Override
    public BkoOffersHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bko_offer_history_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BkoOffersHistoryAdapter.ViewHolder viewHolder, int position) {
        final BkoOfferHistoryResponse.BkoOfferHistoryVO selectedItem = response.get(position);

        try {
            viewHolder.clientTv.setText(BkoUtilities.ensureNotNullString(selectedItem.getCliente()));
            viewHolder.wareHouseTv.setText(BkoUtilities.ensureNotNullString(selectedItem.getAlmacen()));
            viewHolder.servicesTv.setText(BkoUtilities.ensureNotNullString(selectedItem.getServicios()) + " Servicios");
            viewHolder.totalTv.setText("$" + BkoUtilities.ensureNotNullString(selectedItem.getPago()));
            viewHolder.descriptionTv.setText(BkoUtilities.ensureNotNullString(selectedItem.getDescripcion_clasificacion()));
            viewHolder.offerStartTv.setText(BkoUtilities.JsonDate(BkoUtilities.ensureNotNullString(selectedItem.getInicio_fecha_offert()), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));
            viewHolder.offerEndTv.setText(BkoUtilities.JsonDate(BkoUtilities.ensureNotNullString(selectedItem.getFin_fecha_offert()), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));
            viewHolder.checkinTv.setText(BkoUtilities.JsonDate(BkoUtilities.ensureNotNullString(selectedItem.getFecha_checkin()), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));
            viewHolder.checkoutTv.setText(BkoUtilities.JsonDate(BkoUtilities.ensureNotNullString(selectedItem.getFecha_checkout()), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            e.fillInStackTrace();

        }
    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView clientTv, wareHouseTv, offerStartTv, offerEndTv, checkinTv, checkoutTv, totalTv, descriptionTv, servicesTv;

        public ViewHolder(View view) {
            super(view);
            clientTv = (TextView) view.findViewById(R.id.clientTv);
            wareHouseTv = (TextView) view.findViewById(R.id.wareHouseTv);
            offerStartTv = (TextView) view.findViewById(R.id.offerStartTv);
            offerEndTv = (TextView) view.findViewById(R.id.offerEndTv);
            checkinTv = (TextView) view.findViewById(R.id.checkinTv);
            checkoutTv = (TextView) view.findViewById(R.id.checkoutTv);
            totalTv = (TextView) view.findViewById(R.id.totalTv);
            descriptionTv = (TextView) view.findViewById(R.id.descriptionTv);
            servicesTv = (TextView) view.findViewById(R.id.servicesTv);
        }

    }


}
