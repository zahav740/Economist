package com.alexey.homeec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private List<DayWithTotal> data;
    private OnItemClickListener listener;

    public DayAdapter(List<DayWithTotal> data) {
        this.data = data;
    }

    public void updateData(List<DayWithTotal> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Загружаем разметку из XML и создаем ViewHolder
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_item, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Pass the data to the ViewHolder
        DayWithTotal dayWithTotal = data.get(position);
        holder.dayTextView.setText(dayWithTotal.getDay());
        holder.totalTextView.setText(String.valueOf(dayWithTotal.getTotal()));

        // Use holder.getAdapterPosition() inside click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(v, position);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Этот класс будет содержать представления для отдельного элемента
        TextView dayTextView;
        TextView totalTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            dayTextView = itemView.findViewById(R.id.dayTextView);
            totalTextView = itemView.findViewById(R.id.totalTextView);
        }
    }

}

