package com.alexey.homeec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MonthActivity extends AppCompatActivity {

    private int selectedYear;
    private int selectedMonth;
    private SwipeDetector swipeDetector;
    private DayAdapter dayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        RecyclerView recyclerView = findViewById(R.id.dayRecyclerView);
        dayAdapter = new DayAdapter(new ArrayList<>());
        recyclerView.setAdapter(dayAdapter);

        swipeDetector = new SwipeDetector(5) {
            @Override
            public void onSwipeDetected(Direction direction) {
                if (direction == Direction.RIGHT) {
                    Intent intent = new Intent(MonthActivity.this, YearActivity.class);
                    startActivity(intent);
                    overridePendingTransition(direction.getEnterAnim(), direction.getExitAnim());
                }
            }
        };

        showMonthPickerDialog();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        swipeDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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

        datePickerDialog.show();
    }

    private void loadMonthData() {
        List<Transaction> transactions = getTransactionsForMonth(selectedYear, selectedMonth);
        HashMap<Integer, Double> totalPerDay = new HashMap<>();

        for (Transaction transaction : transactions) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(transaction.getDate());
            int day = cal.get(Calendar.DAY_OF_MONTH);
            double amount = transaction.getExpense();

            Double currentTotal = totalPerDay.get(day);
            if (currentTotal == null) {
                currentTotal = 0.0;
            }
            totalPerDay.put(day, currentTotal + amount);
        }

        // Затем вы можете создать список DayWithTotal на основе данных в totalPerDay
        List<DayWithTotal> dayWithTotals = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : totalPerDay.entrySet()) {
            dayWithTotals.add(new DayWithTotal(entry.getKey(), entry.getValue()));
        }

        // Затем обновите адаптер с новыми данными
        dayAdapter.updateData(dayWithTotals);
    }

}
