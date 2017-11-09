package com.girish.banksnearme;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by girish on 08-10-2017.
 */

public class ListDataViewHolder extends RecyclerView.ViewHolder {

    public TextView name, address, isOpen, rating;
    public ImageView imageView;
    public LinearLayout mainLayout;

    public ListDataViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        address = (TextView) itemView.findViewById(R.id.address);
        isOpen = (TextView) itemView.findViewById(R.id.is_open);
        rating = (TextView) itemView.findViewById(R.id.rating);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        mainLayout = (LinearLayout) itemView.findViewById(R.id.main_layout);
    }

}