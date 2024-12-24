package com.example.acc;

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
    private ArrayAdapter<String> adapter;
    private ArrayList<String> itemList;
    private FloatingActionButton fab;
    private TextView totalPriceTextView;
    private double totalPrice = 0.0;
    private Button to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.javalysus);

        editTextItem = findViewById(R.id.editTextItem);
        editTextPrice = findViewById(R.id.editTextPrice);
        listViewItems = findViewById(R.id.listViewItems);
        fab = findViewById(R.id.fab);
        totalPriceTextView = findViewById(R.id.totalPrice);
        to = findViewById(R.id.doneButton);

        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listViewItems.setAdapter(adapter);

        // Initially hide the EditText
        editTextItem.setVisibility(View.GONE);
        editTextPrice.setVisibility(View.GONE);

        // Load saved items
        loadSavedItems();

        // Handle Enter key press event for EditText
        editTextItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addItem();
                    return true;
                }
                return false;
            }
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextItem.getVisibility() == View.GONE) {
                    editTextItem.setVisibility(View.VISIBLE);
                    editTextPrice.setVisibility(View.VISIBLE);
                    editTextItem.requestFocus();
                    showKeyboard(editTextItem);
                } else {
                    editTextItem.setVisibility(View.GONE);
                    editTextPrice.setVisibility(View.GONE);
                    hideKeyboard();
                }
            }
        });

        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            removeItem(position);
        });

        to = findViewById(R.id.doneButton);
        to.setOnClickListener(v -> {
            Intent intent = new Intent(Javalysus.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void addItem() {
        String itemName = editTextItem.getText().toString();
        String priceStr = editTextPrice.getText().toString();
        
        if (!itemName.isEmpty() && !priceStr.isEmpty()) {
            try {
                double price = Double.parseDouble(priceStr);
                Item item = new Item(itemName, 1, price);
                
                List<Item> items = Item.loadItems(this);
                items.add(item);
                Item.saveItems(this, items);
                
                updateUI(itemName, price);
                
                Log.d("Javalysus", "Item saved: " + item);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(String itemName, double price) {
        itemList.add(itemName + " - $" + price);
        adapter.notifyDataSetChanged();
        editTextItem.setText("");
        editTextPrice.setText("");
        editTextItem.setVisibility(View.GONE);
        editTextPrice.setVisibility(View.GONE);
        hideKeyboard();
        totalPrice += price;
        updateTotalPrice();
    }

    private void removeItem(int position) {
        String item = itemList.get(position);
        String itemName = item.substring(0, item.lastIndexOf(" - $"));
        String price = item.substring(item.lastIndexOf("$") + 1);
        
        // Remove from storage
        List<Item> items = Item.loadItems(this);
        items.removeIf(i -> i.getName().equals(itemName));
        Item.saveItems(this, items);
        
        // Update UI
        totalPrice -= Double.parseDouble(price);
        itemList.remove(position);
        adapter.notifyDataSetChanged();
        updateTotalPrice();
        
        Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalPrice() {
        totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editTextItem.getWindowToken(), 0);
        }
    }

    private void loadSavedItems() {
        List<Item> savedItems = Item.loadItems(this);
        Log.d("Javalysus", "Loading items: " + savedItems.size());
        
        for (Item item : savedItems) {
            itemList.add(item.getName() + " - $" + item.getPrice());
            totalPrice += item.getPrice();
        }
        
        adapter.notifyDataSetChanged();
        updateTotalPrice();
    }
}