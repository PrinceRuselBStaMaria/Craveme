package com.example.acc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Javalysus extends AppCompatActivity {

    private EditText editTextItem;
    private EditText editTextPrice;
    private ListView listViewItems;
    private CustomAdapter adapter;
    private List<Item> items;
    private FloatingActionButton fab;
    private TextView totalPriceTextView;
    private double totalPrice = 0.0;
    private Button to;
    private ImageButton acc;
    private View overlay;
    // Add field to track editing position
    private int editingPosition = -1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.javalysus);

        editTextItem = findViewById(R.id.editTextItem);
        editTextPrice = findViewById(R.id.editTextPrice);
        listViewItems = findViewById(R.id.listViewItems);
        fab = findViewById(R.id.fab);
        totalPriceTextView = findViewById(R.id.totalPrice);
        to = findViewById(R.id.lipat);
        acc = findViewById(R.id.doneButton);
        overlay = findViewById(R.id.overlay);
        items = Item.loadItems(this);
        adapter = new CustomAdapter(this, items);
        listViewItems.setAdapter(adapter);

        items = Item.loadItems(this);

        // Initially hide the EditText
        editTextItem.setVisibility(View.GONE);
        editTextPrice.setVisibility(View.GONE);

        // Load saved items
        loadSavedItems();

        // Add touch listener for overlay
        overlay.setOnClickListener(v -> {
            hideInputFields();
            hideKeyboard();
        });

        // Handle Enter key press event for EditText
        editTextItem.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                editTextPrice.requestFocus();
                return true;
            }
            return false;
        });

        editTextPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addItem();
                    return true;
                }
                return false;
            }
        });

        // Handle FloatingActionButton click event
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextItem.getVisibility() == View.GONE) {
                    overlay.setVisibility(View.VISIBLE);
                    editTextItem.setVisibility(View.VISIBLE);
                    editTextPrice.setVisibility(View.VISIBLE);
                    editTextItem.requestFocus();
                    showKeyboard(editTextItem);
                } else {
                    overlay.setVisibility(View.GONE);
                    editTextItem.setVisibility(View.GONE);
                    editTextPrice.setVisibility(View.GONE);
                    hideKeyboard();
                }
            }
        });

        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            removeItem(position);
        });

        acc.setOnClickListener(v -> {
            Intent intent = new Intent(Javalysus.this, UserProfileActivity.class);
            startActivity(intent);
        });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(Javalysus.this, CalculatorNiShane.class);
            startActivity(intent);
        });
    }

    // Update addItem method to handle editing
    private void addItem() {
        String itemName = editTextItem.getText().toString();
        String priceStr = editTextPrice.getText().toString();

        if (!itemName.isEmpty() && !priceStr.isEmpty()) {
            try {
                double price = Double.parseDouble(priceStr);
                
                if (editingPosition != -1) {
                    // Update existing item
                    Item item = items.get(editingPosition);
                    totalPrice -= item.getPrice(); // Subtract old price
                    item.setName(itemName);
                    item.setPrice(price);
                    totalPrice += price; // Add new price
                    updateTotalPrice();
                    adapter.notifyDataSetChanged();
                    editingPosition = -1;
                    hideInputFields();
                    hideKeyboard();
                } else {
                    // Add new item
                    Item item = new Item(itemName, 1, price);
                    items.add(item);
                    totalPrice += price;
                    updateTotalPrice();
                    adapter.notifyDataSetChanged();
                    hideInputFields();
                    hideKeyboard();
                }
                
                Item.saveItems(this, items);
                editTextItem.setText("");
                editTextPrice.setText("");
                
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(String itemName, double price) {
        items = Item.loadItems(this);
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
        
        editTextItem.setText("");
        editTextPrice.setText("");
        editTextItem.setVisibility(View.GONE);
        editTextPrice.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        hideKeyboard();
        totalPrice += price;
        updateTotalPrice();
    }

    public void updateItem(int position, Item item) {
        items.set(position, item);
        Item.saveItems(this, items);
        adapter.notifyDataSetChanged();
    }

    // Update editItem method
    public void editItem(int position) {
        editingPosition = position;
        Item item = items.get(position);
        editTextItem.setText(item.getName());
        editTextPrice.setText(String.valueOf(item.getPrice()));
        editTextItem.setVisibility(View.VISIBLE);
        editTextPrice.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        editTextItem.requestFocus();
        showKeyboard(editTextItem);
    }

    public void removeItem(int position) {
        Item item = items.get(position);
        totalPrice -= item.getPrice();
        items.remove(position);
        Item.saveItems(this, items);
        adapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    public void removeItemAndRefresh(int position) {
        Item item = items.get(position);
        totalPrice -= item.getPrice();
        items.remove(position);
        Item.saveItems(this, items);
        
        adapter.remove(item);
        adapter.notifyDataSetChanged();
        updateTotalPrice();
        
        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalPrice() {
        totalPriceTextView.setText(String.format("Total: ₱%.2f", totalPrice));
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void hideInputFields() {
        overlay.setVisibility(View.GONE);
        editTextItem.setVisibility(View.GONE);
        editTextPrice.setVisibility(View.GONE);
    }

    private void loadSavedItems() {
        items = Item.loadItems(this);
        adapter.clear();
        adapter.addAll(items);
        
        totalPrice = 0;
        for (Item item : items) {
            totalPrice += item.getPrice();
        }
        
        updateTotalPrice();
    }

    // Add method to handle checkbox changes
    public void onItemChecked(int position, boolean isChecked) {
        Item item = items.get(position);
        item.setChecked(isChecked);
        adapter.notifyDataSetChanged();
        Item.saveItems(this, items);
        
        // Optional: Show feedback
        String status = isChecked ? "completed" : "pending";
        Toast.makeText(this, "Item marked as " + status, Toast.LENGTH_SHORT).show();
    }
}

