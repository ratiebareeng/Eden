package com.example.eden.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eden.R;

public class OrderRequestViewHolder extends RecyclerView.ViewHolder {
    public TextView orderId;
    public TextView orderDate;
    public TextView orderStatus;
    public TextView orderTotal;

    public OrderRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        orderId = itemView.findViewById(R.id.order_id);
        orderDate = itemView.findViewById(R.id.order_date);
        orderStatus = itemView.findViewById(R.id.order_status);
        orderTotal = itemView.findViewById(R.id.order_total);
    }
}
