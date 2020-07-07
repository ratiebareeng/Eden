package com.example.eden.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eden.R;
import com.example.eden.model.OrderEntity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BasketItemsAdapter extends RecyclerView.Adapter<BasketItemsAdapter.OrderItemViewHolder> {
    private static final String TAG = "BasketItemsAdapter";
    private List<OrderEntity> orderEntities = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private Locale botswana = new Locale("en", "BW");
    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance(botswana);

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_basket_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderEntity currentOrderEntity = orderEntities.get(position);
        Log.d(TAG, "url: " + currentOrderEntity.getItemImageUrl());
        Glide.with(holder.itemView.getContext()).load(currentOrderEntity.getItemImageUrl()).into(holder.itemImage);
        holder.itemName.setText(currentOrderEntity.getItemName());
        holder.itemPrice.setText(numberFormat.format(currentOrderEntity.getItemPrice()));
        holder.itemQuantity.setText(String.valueOf(currentOrderEntity.getItemQuantity()));
        holder.orderTotal.setText(numberFormat.format(currentOrderEntity.getItemsTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return orderEntities.size();
    }

    public void setOrderEntities(List<OrderEntity> orderEntities) {
        this.orderEntities = orderEntities;
        notifyDataSetChanged();
    }

    class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemPrice;
        TextView itemQuantity;
        TextView orderTotal;
        TextView increaseItemQuantity;
        TextView decreaseItemQuantity;

        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.basket_item_image);
            itemName = itemView.findViewById(R.id.basket_item_name);
            itemPrice = itemView.findViewById(R.id.basket_item_price);
            itemQuantity = itemView.findViewById(R.id.basket_item_quantity);
            orderTotal = itemView.findViewById(R.id.basket_total_price);
            increaseItemQuantity = itemView.findViewById(R.id.increase_item_quantity);

            increaseItemQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    increaseItemQuantity(position);
                }
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                // make sure you don't click and item with an invalid position ex item being delete from basket
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(orderEntities.get(position));
                }
            });
        }
    }

        public interface OnItemClickListener {
            void onItemClick(OrderEntity orderEntity);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

    public OrderEntity getOrderItemAt(int position) {
        return orderEntities.get(position);
    }

    public void increaseItemQuantity(int position) {
        int itemQuantity = orderEntities.get(position).getItemQuantity();
        Log.d(TAG, "init Item Quantity: " + itemQuantity);
        itemQuantity = itemQuantity + 1;
        orderEntities.get(position).setItemQuantity(itemQuantity);
        Log.d(TAG, "increased Item Quantity: " + itemQuantity);
    }

    public void decreaseItemQuantity(int position) {
        int itemQuantity = orderEntities.get(position).getItemQuantity();
        Log.d(TAG, "init Item Quantity: " + itemQuantity);
        itemQuantity = itemQuantity - 1;
        orderEntities.get(position).setItemQuantity(itemQuantity);
        Log.d(TAG, "increased Item Quantity: " + itemQuantity);
    }

}
