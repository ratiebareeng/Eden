package com.example.eden.viewmodel;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eden.ItemClickListener;
import com.example.eden.R;

public class ComboViewModel extends RecyclerView.ViewHolder implements View.OnClickListener{
    private ItemClickListener itemClickListener;
    public ImageView comboImage;
    public TextView name, itemList, price;

    public ComboViewModel(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        comboImage = itemView.findViewById(R.id.combo_image);
        name = itemView.findViewById(R.id.combo_name);
        itemList = itemView.findViewById(R.id.combo_item_list);
        price = itemView.findViewById(R.id.combo_price);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
