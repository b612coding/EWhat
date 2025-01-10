// FoodDao.java
package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface FoodDao {
    @Query("SELECT * FROM food ORDER BY RANDOM() LIMIT 1")
    Food getRandomFood();
}