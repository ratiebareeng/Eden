package com.example.eden.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eden.ItemClickListener;
import com.example.eden.R;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public ImageView itemImage;
    public TextView itemName;
    public TextView itemPrice;

    private ItemClickListener itemClickListener;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemImage = itemView.findViewById(R.id.product_image);
        itemName = itemView.findViewById(R.id.product_name);
        itemPrice = itemView.findViewById(R.id.product_price);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
