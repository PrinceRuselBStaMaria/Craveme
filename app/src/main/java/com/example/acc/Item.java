package com.example.acc;

import android.content.Context;
import android.content.SharedPreferences;

public class Item {
    private String name;
    private int quantity;

    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void saveToStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ItemStorage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("itemName", name);
        editor.putInt("itemQuantity", quantity);
        editor.apply();
    }

    public static Item retrieveFromStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ItemStorage", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("itemName", null);
        int quantity = sharedPreferences.getInt("itemQuantity", 0);
        if (name != null) {
            return new Item(name, quantity);
        }
        return null;
    }
}
