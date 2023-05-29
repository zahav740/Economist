package com.alexey.homeec;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;

public class DetailActivity extends AppCompatActivity {

    private TableLayout detailTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailTable = findViewById(R.id.detailTable);

        String selectedDate = getIntent().getStringExtra("selectedDate");

        // Загружаем историю транзакций для выбранной даты
        loadTransactionHistory(selectedDate);
    }

    private void loadTransactionHistory(String date) {
        // Реализуйте этот метод в соответствии с вашей логикой
    }
}
