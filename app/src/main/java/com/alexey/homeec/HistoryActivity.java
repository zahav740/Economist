package com.alexey.homeec;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alexey.homeec.Transaction;

import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private SwipeDetector swipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Button buttonYear = findViewById(R.id.buttonYear);
        buttonYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, YearActivity.class);
                startActivity(intent);
            }
        });

        Button buttonMonth = findViewById(R.id.buttonMonth);
        buttonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MonthActivity.class);
                startActivity(intent);
            }
        });

        Button buttonDay = findViewById(R.id.buttonDay);
        buttonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, DayActivity.class);
                startActivity(intent);
            }
        });

        swipeDetector = new SwipeDetector(5) {
            @Override
            public void onSwipeDetected(Direction direction) {
                if (direction == Direction.RIGHT) {
                    Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(direction.getEnterAnim(), direction.getExitAnim());
                }
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        swipeDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
