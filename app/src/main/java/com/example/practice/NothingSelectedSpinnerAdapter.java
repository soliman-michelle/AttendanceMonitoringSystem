package com.example.practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NothingSelectedSpinnerAdapter extends ArrayAdapter<CharSequence> {
    private final LayoutInflater inflater;
    private final int layoutResourceId;
    private final String promptText;

    public NothingSelectedSpinnerAdapter(ArrayAdapter<CharSequence> adapter, int layoutResourceId, Context context, String promptText) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.promptText = promptText;
        inflater = LayoutInflater.from(context);

        // Add the prompt as the first item in the adapter
        add(null);
        for (int i = 0; i < adapter.getCount(); i++) {
            add(adapter.getItem(i));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(layoutResourceId, parent, false);

        TextView textView = view.findViewById(android.R.id.text1);
        CharSequence item = getItem(position);
        if (item == null) {
            // Display the prompt with a different style
            textView.setText(promptText);
            textView.setTextColor(getContext().getResources().getColor(R.color.black));
        } else {
            textView.setText(item);
            textView.setTextColor(getContext().getResources().getColor(android.R.color.black));
        }

        return view;
    }
}
