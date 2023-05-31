package com.alexey.homeec;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class YearActivity extends AppCompatActivity {

    private int selectedYear;
    private CsvHelper csvHelper;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        recyclerView = findViewById(R.id.yearRecyclerView);
        csvHelper = new CsvHelper(this);

        // Show the DatePickerDialog to select the year
        showYearPickerDialog();
    }

    private void showYearPickerDialog() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(YearActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedYear = year;
                        loadYearData();
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        // Hide day and month pickers
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("month", "id", "android")).setVisibility(View.GONE);

        datePickerDialog.show();
    }

    private void loadYearData() {
        List<Transaction> allTransactions = csvHelper.readTransactionRecords();
        List<String> yearTransactions = new ArrayList<>();
        for (Transaction transaction : allTransactions) {
            Calendar transactionDate = Calendar.getInstance();
            transactionDate.setTime(transaction.getDate()); // assuming transaction.getDate() returns a Date object
            int transactionYear = transactionDate.get(Calendar.YEAR);
            if (transactionYear == selectedYear) {
                yearTransactions.add(transaction.toString());
            }
        }

        YearAdapter adapter = new YearAdapter(this, yearTransactions, selectedYear);
        recyclerView.setAdapter(adapter);
    }


}
