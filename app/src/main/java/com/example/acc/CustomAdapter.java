package com.example.acc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Item> {
    private Context context;
    private List<Item> items;
    
    public CustomAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Item item = items.get(position);
        
        CheckBox checkbox = convertView.findViewById(R.id.checkbox);
        TextView itemName = convertView.findViewById(R.id.itemName);
        ImageButton editButton = convertView.findViewById(R.id.editButton);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        itemName.setText(item.getName() + " - ₱" + item.getPrice());
        checkbox.setChecked(item.isChecked());

        checkbox.setOnClickListener(v -> {
            item.setChecked(checkbox.isChecked());
            ((Javalysus)context).updateItem(position, item);
        });

        editButton.setOnClickListener(v -> {
            ((Javalysus)context).editItem(position);
        });

        deleteButton.setOnClickListener(v -> {
            ((Javalysus)context).removeItem(position);
        });

        return convertView;
    }
}