package com.example.practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class studentAdapter extends RecyclerView.Adapter<studentAdapter.MyViewHolder>{
    private final List<studentList> items;
    private final Context context;

    public studentAdapter(List<studentList> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public studentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_student_adapter, null));
    }

    @Override
    public void onBindViewHolder(@NonNull studentAdapter.MyViewHolder holder, int position) {
        studentList student = items.get(position);

        holder.name.setText(student.getFullname());
        holder.studnum.setText(student.getStudNum());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView name, studnum;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            studnum = itemView.findViewById(R.id.studnum);
        }
    }
}
