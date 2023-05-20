package com.example.practice;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;
import android.content.Context;

public class yearLevelAdapter extends RecyclerView.Adapter<yearLevelAdapter.MyViewHolder> {
    private List<String> yearLevels;
    private Context context;
    private OnItemClickListener listener;

    public yearLevelAdapter(List<String> yearLevels, Context context, OnItemClickListener listener) {
        this.yearLevels = yearLevels;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_year_level_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String yearLevel = yearLevels.get(position);
        holder.bind(yearLevel, listener);
    }

    @Override
    public int getItemCount() {
        return yearLevels.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String yearLevel);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView yearLevelTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            yearLevelTextView = itemView.findViewById(R.id.year);
        }

        public void bind(String yearLevel, OnItemClickListener listener) {
            yearLevelTextView.setText(yearLevel);
            itemView.setOnClickListener(v -> listener.onItemClick(yearLevel));
        }
    }
}

