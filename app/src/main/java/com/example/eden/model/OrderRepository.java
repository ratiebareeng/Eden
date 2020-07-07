package com.example.eden.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eden.model.FreshProduceDatabase;
import com.example.eden.model.OrderDao;
import com.example.eden.model.OrderEntity;

import java.util.List;

public class OrderRepository {
    private OrderDao orderDao;
    private LiveData<List<OrderEntity>> allOrders;
    private LiveData<List<Double>> orderItemTotals;

    public OrderRepository(Application application) {
        FreshProduceDatabase freshProduceDatabase = FreshProduceDatabase.getFreshProduceDatabaseInstance(application);
        orderDao = freshProduceDatabase.orderDao();
        allOrders = orderDao.getAllUserOrders();
        orderItemTotals = orderDao.getItemTotals();
    }
    public void insertOrder(OrderEntity orderEntity){
        FreshProduceDatabase.executorService.execute(() -> orderDao.insertOrder(orderEntity));
    }

    public void updateOrder(OrderEntity orderEntity){
        FreshProduceDatabase.executorService.execute(()-> orderDao.updateOrder(orderEntity));
    }

    public void deleteOrder(OrderEntity orderEntity){
        FreshProduceDatabase.executorService.execute(()-> orderDao.deleteOrder(orderEntity));
    }

    public void deleteAllOrders(){
        FreshProduceDatabase.executorService.execute(()-> orderDao.deleteAllOrders());
    }

    public LiveData<List<Double>> getOrderItemTotals() {
        return orderItemTotals;
    }

    public LiveData<List<OrderEntity>> getAllOrders() {
        return allOrders;
    }
}
