package com.example.eden.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {OrderEntity.class}, version = 1, exportSchema = false)
public abstract class FreshProduceDatabase extends RoomDatabase {
    private static final String TAG = "FreshProduceDatabase";
    private static final int NUMBER_OF_THREADS = 4;

    private static volatile FreshProduceDatabase FRESHPRODUCEDATABASE;

    public abstract OrderDao orderDao();

    public static final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static FreshProduceDatabase getFreshProduceDatabaseInstance(final Context context) {
        if (FRESHPRODUCEDATABASE == null) {
            synchronized (FreshProduceDatabase.class) {
                if (FRESHPRODUCEDATABASE == null) {
                    FRESHPRODUCEDATABASE = Room.databaseBuilder(context.getApplicationContext(),
                            FreshProduceDatabase.class, "fresh_produce_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return FRESHPRODUCEDATABASE;
    }

}
