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
        super(context, R.layout.list_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.itemName);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);
            holder.editButton = convertView.findViewById(R.id.editButton);
            holder.checkbox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = items.get(position);
        holder.textView.setText(item.getName() + " - ₱" + item.getPrice());
        holder.checkbox.setChecked(item.isChecked());
        
        holder.checkbox.setOnClickListener(v -> {
            ((Javalysus)context).onItemChecked(position, holder.checkbox.isChecked());
        });

        holder.deleteButton.setOnClickListener(v -> {
            ((Javalysus)context).removeItemAndRefresh(position);
        });

        holder.editButton.setOnClickListener(v -> {
            ((Javalysus)context).editItem(position);
        });

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        // Force layout refresh
        if (context instanceof Javalysus) {
            ((Javalysus) context).runOnUiThread(() -> super.notifyDataSetChanged());
        }
    }

    static class ViewHolder {
        TextView textView;
        ImageButton deleteButton;
        ImageButton editButton;
        CheckBox checkbox;
    }
}