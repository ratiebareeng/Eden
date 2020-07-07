package com.example.eden.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface OrderDao {
    @Insert
    void insertOrder(OrderEntity orderEntity);

    @Update
    void updateOrder(OrderEntity orderEntity);

    @Delete
    void deleteOrder(OrderEntity orderEntity);

    @Query("DELETE FROM order_table")
    void deleteAllOrders();

    @Query("SELECT * FROM order_table ORDER BY itemPrice ASC")
    LiveData<List<OrderEntity>> getAllUserOrders();

    /*@Query("SELECT * FROM order_table WHERE dbDocumentName = :dbDocumentName")
    LiveData<List<OrderEntity>> getBusinessCartItems(String dbDocumentName);*/

    @Query("SELECT itemsTotalPrice FROM order_table")
    LiveData<List<Double>> getItemTotals();

    /*@Query("SELECT itemsTotalPrice FROM order_table WHERE dbDocumentName = :dbDocumentName")
    LiveData<List<Double>> getBusinessBasketItemsTotals(String dbDocumentName);*/

}
