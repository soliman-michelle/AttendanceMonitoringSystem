package com.example.practice;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ProgramViewHolder extends RecyclerView.ViewHolder {
    public TextView programTextView;

    public ProgramViewHolder(View itemView) {
        super(itemView);
        programTextView = itemView.findViewById(R.id.programTextView);
    }
}
