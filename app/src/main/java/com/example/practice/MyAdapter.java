package com.example.practice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final List<StoreInfo1> items;
    private final Context context;

    public MyAdapter(List<StoreInfo1> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        StoreInfo1 storeInfo1 = items.get(position);

        holder.name.setText(storeInfo1.getName());
        holder.studNo.setText(storeInfo1.getStudentnumber());
        holder.data.setText(storeInfo1.getQrdata());
        holder.arrival.setText(storeInfo1.getTime());
        holder.status.setText("Present");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView name, studNo, arrival, status, data;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userName);
            studNo = itemView.findViewById(R.id.userStudNo);
            arrival = itemView.findViewById(R.id.arrival);
            status = itemView.findViewById(R.id.status);
            data = itemView.findViewById(R.id.subject);

        }
    }
}
