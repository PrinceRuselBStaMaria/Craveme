package com.example.acc;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Javalysus extends AppCompatActivity {

    private EditText editTextItem;
    private EditText editTextPrice;
    private ListView listViewItems;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> itemList;
    private FloatingActionButton fab;
    private TextView totalPriceTextView;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.javalysus);

        editTextItem = findViewById(R.id.editTextItem);
        editTextPrice = findViewById(R.id.editTextPrice);
        listViewItems = findViewById(R.id.listViewItems);
        fab = findViewById(R.id.fab);
        totalPriceTextView = findViewById(R.id.totalPrice);

        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listViewItems.setAdapter(adapter);

        // Initially hide the EditText
        editTextItem.setVisibility(View.GONE);
        editTextPrice.setVisibility(View.GONE);

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
    }

    private void addItem() {
        String item = editTextItem.getText().toString();
        String price = editTextPrice.getText().toString();
        if (!item.isEmpty() && !price.isEmpty()) {
            itemList.add(item + " - $" + price);
            adapter.notifyDataSetChanged();
            editTextItem.setText("");
            editTextPrice.setText("");
            editTextItem.setVisibility(View.GONE);
            editTextPrice.setVisibility(View.GONE);
            hideKeyboard();

            // Update total price
            totalPrice += Double.parseDouble(price);
            updateTotalPrice();
        } else {
            Toast.makeText(Javalysus.this, "Please enter both item and price", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeItem(int position) {
        String item = itemList.get(position);
        String price = item.substring(item.lastIndexOf("$") + 1);
        totalPrice -= Double.parseDouble(price);
        itemList.remove(position);
        adapter.notifyDataSetChanged();
        updateTotalPrice();
        Toast.makeText(Javalysus.this, "Item removed", Toast.LENGTH_SHORT).show();
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
}