package com.alexey.homeec;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

public class YearActivity extends AppCompatActivity {

    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

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
        // Load the list of months with total amounts for the selected year
        // For each month create a new list item
        // Add click listener to each item to start MonthActivity with selected year and month
    }
}
