package com.alexey.homeec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DayAdapter adapter;
    private int selectedYear;
    private int selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        recyclerView = findViewById(R.id.dayRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DayAdapter(new ArrayList<DayWithTotal>());
        recyclerView.setAdapter(adapter);
        // Show the DatePickerDialog to select the year and month
        showMonthPickerDialog();
    }

    private void showMonthPickerDialog() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(MonthActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedYear = year;
                        selectedMonth = month;
                        loadMonthData();
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        // Hide day picker
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

        datePickerDialog.show();
    }

    private void loadMonthData() {
        // Load the list of days with total amounts for the selected year and month
        List<Transaction> transactions = TransactionData.getTransactionsForMonth(selectedYear, selectedMonth);

        // Group transactions by day
        Map<Integer, List<Transaction>> transactionsByDay = new HashMap<>();
        for (Transaction transaction : transactions) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(transaction.getDate());
            int day = cal.get(Calendar.DAY_OF_MONTH);

            if (!transactionsByDay.containsKey(day)) {
                transactionsByDay.put(day, new ArrayList<>());
            }

            transactionsByDay.get(day).add(transaction);
        }

        // Create list of DayWithTotal objects to pass to the adapter
        List<DayWithTotal> daysWithTotals = new ArrayList<>();
        for (Map.Entry<Integer, List<Transaction>> entry : transactionsByDay.entrySet()) {
            int day = entry.getKey();
            List<Transaction> transactionsForDay = entry.getValue();

            double total = 0;
            for (Transaction transaction : transactionsForDay) {
                total += transaction.getAmount();
            }

            daysWithTotals.add(new DayWithTotal(day, total));
        }

        // Update the RecyclerView adapter
        dayAdapter.updateData(daysWithTotals);

        // Add click listener to each item to start DayActivity with selected year, month and day
        dayAdapter.setOnItemClickListener(new DayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MonthActivity.this, DayActivity.class);
                intent.putExtra("year", selectedYear);
                intent.putExtra("month", selectedMonth);
                intent.putExtra("day", daysWithTotals.get(position).getDay());
                startActivity(intent);
            }
        });
    }

}
