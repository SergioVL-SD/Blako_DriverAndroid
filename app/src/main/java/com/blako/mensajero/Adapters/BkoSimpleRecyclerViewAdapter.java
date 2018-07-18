package com.blako.mensajero.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blako.mensajero.R;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 16/10/17.
 */

public class BkoSimpleRecyclerViewAdapter extends RecyclerView.Adapter<BkoSimpleRecyclerViewAdapter.SimpleViewHolder> {
    private ArrayList<String> dataSource;
    public BkoSimpleRecyclerViewAdapter(ArrayList<String> dataArgs){
        dataSource = dataArgs;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bko_product_item, parent, false);
        return new BkoSimpleRecyclerViewAdapter.SimpleViewHolder(view);

    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        SimpleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.productTv);
        }
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.textView.setText("" +dataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}
