// Food.java
package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food")
public class Food {
    @PrimaryKey
    public int food_id;
    public String name;
    public String tag;
    public String material;
    public String cook_method;
}