package com.alexey.homeec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private List<DayWithTotal> data;

    public DayAdapter(List<DayWithTotal> data) {
        this.data = data;
    }

    public void updateData(List<DayWithTotal> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DayWithTotal item = data.get(position);
        holder.dayTextView.setText(String.format(Locale.getDefault(), "%d", item.getDay()));
        holder.totalTextView.setText(String.format(Locale.getDefault(), "%.2f", item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayTextView;
        TextView totalTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            dayTextView = itemView.findViewById(R.id.dayTextView);
            totalTextView = itemView.findViewById(R.id.totalTextView);
        }
    }
}


