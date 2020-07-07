package com.example.eden.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eden.model.OrderEntity;
import com.example.eden.model.OrderRepository;

import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private OrderRepository orderRepository;
    private LiveData<List<OrderEntity>> allOrderItems;
    private LiveData<List<Double>> itemTotals;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
        allOrderItems = orderRepository.getAllOrders();
        itemTotals = orderRepository.getOrderItemTotals();
    }

    public void insertOrder(OrderEntity orderEntity){
        orderRepository.insertOrder(orderEntity);
    }

    public void updateOrder(OrderEntity orderEntity){
        orderRepository.updateOrder(orderEntity);
    }

    public void deleteOrder(OrderEntity orderEntity){
        orderRepository.deleteOrder(orderEntity);
    }

    public void deleteAllOrders(){
        orderRepository.deleteAllOrders();
    }

    public LiveData<List<Double>> getItemTotals() {
        return itemTotals;
    }

    public LiveData<List<OrderEntity>> getAllOrderItems() {
        return allOrderItems;
    }
}
