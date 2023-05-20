package com.example.practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class programAdapter extends RecyclerView.Adapter<programAdapter.MyViewHolder>{
    private final List<String> items;
    private final Context context;

    public programAdapter(List<String> items, Context context) {
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_program_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String section = items.get(position);
        holder.program.setText(section);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView program;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            program = itemView.findViewById(R.id.program);
        }
    }
}

