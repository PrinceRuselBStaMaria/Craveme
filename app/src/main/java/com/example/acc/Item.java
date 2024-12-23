package com.example.acc;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Item {
    private String name;
    private int quantity;
    private double price;
    private String description;
    private static final String PREF_NAME = "ItemStorage";
    private static final String KEY_ITEMS = "items_list";
    private static final String FILENAME = "items.txt";

    public Item(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }

    // Save list of items
    public static void saveItems(Context context, List<Item> items) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(items);
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
            
            // Verify file was created
            checkFile(context);
            Log.d("Item", "Items saved: " + json);
        } catch (IOException e) {
            Log.e("Item", "Error saving items", e);
        }
    }

    // Load list of items
    public static List<Item> loadItems(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILENAME);
            if (!file.exists()) {
                Log.d("Item", "No saved items file found");
                return new ArrayList<>();
            }

            FileInputStream fis = context.openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            
            String json = sb.toString();
            Log.d("Item", "Loaded JSON: " + json);
            
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Item>>(){}.getType();
            List<Item> items = gson.fromJson(json, type);
            
            return items != null ? items : new ArrayList<>();
        } catch (IOException e) {
            Log.e("Item", "Error loading items", e);
            return new ArrayList<>();
        }
    }

    // Add single item to storage
    public void addToStorage(Context context) {
        List<Item> items = loadItems(context);
        items.add(this);
        saveItems(context, items);
        checkFile(context);
    }

    // Remove single item from storage
    public static void removeFromStorage(Context context, Item item) {
        List<Item> items = loadItems(context);
        items.removeIf(i -> i.getName().equals(item.getName()));
        saveItems(context, items);
    }

    // Clear all items
    public static void clearStorage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    // Add debug method
    public static void debugStorage(Context context) {
        List<Item> items = loadItems(context);
        Log.d("Item", "Number of stored items: " + items.size());
        for (Item item : items) {
            Log.d("Item", "Stored item: " + item.getName() + " Price: " + item.getPrice());
        }
    }

    // Add toString for better logging
    @Override
    public String toString() {
        return "Item{name='" + name + "', price=" + price + ", quantity=" + quantity + "}";
    }

    // Add debug method to check file
    private static void checkFile(Context context) {
        File file = new File(context.getFilesDir(), FILENAME);
        Log.d("Item", "File path: " + file.getAbsolutePath());
        Log.d("Item", "File exists: " + file.exists());
        Log.d("Item", "File size: " + file.length());
    }
}
