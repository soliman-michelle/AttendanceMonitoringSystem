package com.example.practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class profAdapter extends RecyclerView.Adapter<profAdapter.MyViewHolder> {

    private final List<profList> items;
    private final Context context;

    public profAdapter(List<profList> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_prof_adapter, null));
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        profList prof = items.get(position);

       holder.name.setText(prof.getProf());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userName);

        }
    }
}
