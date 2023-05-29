package com.alexey.homeec;

public class HistoryActivity1 {
    /*
    import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private boolean showByDay = false;
    private boolean showByMonth = false;
    private boolean showByYear = false;

    private TableLayout historyTable;

    private SwipeDetector swipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyTable = findViewById(R.id.historyTable);

        Button buttonDay = findViewById(R.id.buttonDay);
        buttonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showByDay = true;
                showByMonth = false;
                showByYear = false;
                loadTransactionHistory(Calendar.getInstance()); // Примечание: замените на нужную дату
            }
        });

        Button buttonMonth = findViewById(R.id.buttonMonth);
        buttonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showByDay = false;
                showByMonth = true;
                showByYear = false;
                loadTransactionHistory(Calendar.getInstance()); // Примечание: замените на нужную дату
            }
        });

        Button buttonYear = findViewById(R.id.buttonYear);
        buttonYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showByDay = false;
                showByMonth = false;
                showByYear = true;
                loadTransactionHistory(Calendar.getInstance()); // Примечание: замените на нужную дату
            }
        });

        loadTransactionHistory(Calendar.getInstance()); // Загрузка истории транзакций при запуске активности

        swipeDetector = new SwipeDetector(5) {
            @Override
            public void onSwipeDetected(Direction direction) {
                if (direction == Direction.RIGHT) {
                    // Open HistoryActivity
                    Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(direction.getEnterAnim(), direction.getExitAnim());
                }
            }
        };
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        swipeDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void loadTransactionHistory(Calendar date) {
        // Удаляем все существующие представления из таблицы
        historyTable.removeAllViews();

        // Берем данные из CsvHelper, а не из DatabaseHelper
        CsvHelper csvHelper = new CsvHelper(this);

        List<Transaction> transactions = null;
        if (showByDay) {
            transactions = csvHelper.readTransactionRecords(); // передаем this
            // здесь ваш код для фильтрации транзакций по дням
        } else if (showByMonth) {
            transactions = csvHelper.readTransactionRecords(); // передаем this
            // здесь ваш код для фильтрации транзакций по месяцам
        } else if (showByYear) {
            transactions = csvHelper.readTransactionRecords(); // передаем this
            // здесь ваш код для фильтрации транзакций по годам
        }


        if (transactions != null) {
            // Добавьте каждую транзакцию в таблицу
            for (Transaction transaction : transactions) {
                // Создаем новую строку
                TableRow row = new TableRow(this);

                // Создаем TextView для каждого поля
                TextView dateView = new TextView(this);
                dateView.setText(transaction.getDate()); // Подставляем ваш формат даты

                TextView incomeView = new TextView(this);
                incomeView.setText(String.valueOf(transaction.getIncome()));

                TextView expenseNameView = new TextView(this);
                expenseNameView.setText(transaction.getExpenseName());

                TextView expenseView = new TextView(this);
                expenseView.setText(String.valueOf(transaction.getExpense()));

                // Добавляем TextView в строку
                row.addView(dateView);
                row.addView(incomeView);
                row.addView(expenseNameView);
                row.addView(expenseView);

                // Добавляем строку в таблицу
                historyTable.addView(row);

                // Добавляем обработчик нажатий на эту строку
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
                        intent.putExtra("selectedDate", transaction.getDate()); // или другое значение даты, в зависимости от вашего формата
                        startActivity(intent);
                    }
                });
            }
        }
    }
}

     */
}
