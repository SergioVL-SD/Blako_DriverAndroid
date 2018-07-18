package com.blako.mensajero.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blako.mensajero.R;
import com.blako.mensajero.Utils.BkoUtilities;
import com.blako.mensajero.VO.BkoVehicleVO;

import java.util.ArrayList;

/**
 * Created by franciscotrinidad on 11/5/15.
 */
public class BkoServicesAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<BkoVehicleVO> vehicles;
    public BkoServicesAdapter(Context context, ArrayList<BkoVehicleVO>  vehicles){
        this.vehicles = vehicles;
        this.context = context;
    }

    @Override
    public int getCount() {
        return vehicles.size();
    }

    @Override
    public BkoVehicleVO getItem(int position) {
      return  vehicles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.bko_vehicles_item, parent, false);
            holder = new ViewHolder();
            holder.idTv = (TextView) convertView.findViewById(R.id.idTv);
            holder.brandTV = (TextView) convertView.findViewById(R.id.brandTV);
            holder.modelTv = (TextView) convertView.findViewById(R.id.modelTv);
            holder.plateTV =  (TextView) convertView.findViewById(R.id.plateTV);
            holder.typeVechicleIv =  (ImageView) convertView.findViewById(R.id.typeVechicleIv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BkoVehicleVO vehicle=getItem(position);
        holder.brandTV.setText(BkoUtilities.ensureNotNullString(vehicle.getBrand()));
        holder.modelTv.setText(BkoUtilities.ensureNotNullString(vehicle.getModel() ));
        holder.plateTV.setText(BkoUtilities.ensureNotNullString(vehicle.getPlate()));

        if(vehicle.getTypeid()!=null){
            if(vehicle.getTypeid().equals("1")){
                holder.typeVechicleIv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.motoc));
            }else if(vehicle.getTypeid().equals("2")){
                holder.typeVechicleIv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.bike));
            }
            else if(vehicle.getTypeid().equals("3")){
                holder.typeVechicleIv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.car));
            }
            else if(vehicle.getTypeid().equals("4")){
                holder.typeVechicleIv.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.van));
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView idTv;
        TextView brandTV;
        TextView modelTv;
        TextView plateTV;
        ImageView typeVechicleIv;

    }

}
