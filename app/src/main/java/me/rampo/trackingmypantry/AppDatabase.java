package me.rampo.trackingmypantry;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities = {Product.class},version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}
