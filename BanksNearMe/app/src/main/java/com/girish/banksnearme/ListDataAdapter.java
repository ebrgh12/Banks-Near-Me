package com.girish.banksnearme;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by girish on 08-10-2017.
 */

public class ListDataAdapter extends RecyclerView.Adapter<ListDataViewHolder> {

    Activity activity;
    BankAtmActivity bankAtmActivity;
    List<DataModel> dataModels;
    ListDataViewHolder listDataViewHolder;

    public ListDataAdapter(Activity activity,
                           BankAtmActivity bankAtmActivity,
                           List<DataModel> dataModels) {
        this.activity = activity;
        this.bankAtmActivity = bankAtmActivity;
        this.dataModels = dataModels;
    }

    @Override
    public ListDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bank_atm_list_item, parent, false);
        listDataViewHolder = new ListDataViewHolder(view);
        return listDataViewHolder;
    }

    @Override
    public void onBindViewHolder(ListDataViewHolder holder, final int position) {
        Picasso.with(activity).load(dataModels.get(position).getImage()).into(holder.imageView);
        holder.name.setText(dataModels.get(position).getName());
        holder.address.setText(dataModels.get(position).getAddress());
        if(dataModels.get(position).getOpen() != null){
            if(dataModels.get(position).getOpen()){
                holder.isOpen.setText("Open");
            }else if (!dataModels.get(position).getOpen()){
                holder.isOpen.setText("Closed");
            }
        } else {
            holder.isOpen.setText("No Data Found");
        }
        holder.rating.setText("Rating : "+String.valueOf(dataModels.get(position).getRating()));

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankAtmActivity.navigateTo(dataModels.get(position).getLatitude(),dataModels.get(position).getLongitude());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

}
