package com.example.practice;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter {
    private Activity mContext;
    List<studentSched> schedList;

    public ListAdapter(Activity mContext, List<studentSched> schedList){
        super(mContext,R.layout.activity_list_items,schedList);
        this.mContext = mContext;
        this.schedList = schedList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= mContext.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.activity_list_items, null, true);

        TextView sub = listItemView.findViewById(R.id.subject);
        TextView days = listItemView.findViewById(R.id.day);
        TextView times = listItemView.findViewById(R.id.time);
        TextView rooms = listItemView.findViewById(R.id.room);

        studentSched sched = schedList.get(position);

        sub.setText(sched.getSubject());
        days.setText(sched.getDay());
        times.setText(sched.getTime());
        rooms.setText(sched.getRoom());

        return listItemView;
    }
}
