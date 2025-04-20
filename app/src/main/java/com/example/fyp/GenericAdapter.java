package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {

    private final List<T> items;
    private final OnBindDataListener<T> onBindDataListener;

    public GenericAdapter(List<T> items, OnBindDataListener<T> onBindDataListener) {
        this.items = (items != null) ? items : new ArrayList<>(); // 初始化 items
        this.onBindDataListener = onBindDataListener;
    }

    public void updateItems(List<T> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = items.get(position);
        onBindDataListener.onBindData(holder.textView, item);
    }

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0; // 添加 null 檢查
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnBindDataListener<T> {
        void onBindData(TextView textView, T item);
    }
}