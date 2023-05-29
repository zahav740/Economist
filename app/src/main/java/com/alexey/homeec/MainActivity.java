package com.alexey.homeec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private double currentBalance = 0.0;
    private List<Transaction> transactionList;
    private TableLayout tableLayout;
    private Gson gson = new Gson();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private File dataFile;
    private SwipeDetector swipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.tableLayout);
        transactionList = new ArrayList<>();
        dataFile = new File(getFilesDir(), "data.csv");

        // Load transactions from file
        loadTransactions();
        for (Transaction transaction : transactionList) {
            addRowToTable(transaction);
            currentBalance += transaction.getIncome() - transaction.getExpense();
        }

        Button balanceButton = findViewById(R.id.balanceButton);
        balanceButton.setText("Баланс: " + currentBalance);

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> finishAffinity());

        balanceButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.balance_dialog, null);
            builder.setView(dialogView);

            EditText incomeInput = dialogView.findViewById(R.id.incomeInput);
            EditText expenseNameInput = dialogView.findViewById(R.id.expenseNameInput);
            EditText expenseInput = dialogView.findViewById(R.id.expenseInput);

            builder.setTitle("Баланс")
                    .setPositiveButton("Click!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String incomeStr = incomeInput.getText().toString();
                            String expenseName = expenseNameInput.getText().toString();
                            String expenseStr = expenseInput.getText().toString();

                            double income = incomeStr.isEmpty() ? 0.0 : Double.parseDouble(incomeStr);
                            double expense = expenseStr.isEmpty() ? 0.0 : Double.parseDouble(expenseStr);

                            if (expenseName.isEmpty() && expense > 0) {
                                // Show some error message or ask user to enter expenseName.
                            } else {
                                Transaction transaction = new Transaction(sdf.format(new Date()), income, expenseName, expense);
                                transactionList.add(transaction);
                                currentBalance += income - expense;
                                balanceButton.setText("Баланс: " + currentBalance);

                                // Save data to JSON and CSV
                                saveData();

                                // Add row to table
                                addRowToTable(transaction);
                            }

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.create().show();
        });

        swipeDetector = new SwipeDetector(5) {
            @Override
            public void onSwipeDetected(Direction direction) {
                if (direction == Direction.LEFT) {
                    // Open HistoryActivity
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(direction.getEnterAnim(), direction.getExitAnim());
                }
            }
        };
        Button historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

    }


    private void addRowToTable(Transaction transaction) {
        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);

        ((TextView) tableRow.findViewById(R.id.dateTextView)).setText(transaction.getDate());
        ((TextView) tableRow.findViewById(R.id.incomeTextView)).setText(String.valueOf(transaction.getIncome()));
        ((TextView) tableRow.findViewById(R.id.categoryTextView)).setText(transaction.getExpenseName());
        ((TextView) tableRow.findViewById(R.id.expenseTextView)).setText(String.valueOf(transaction.getExpense()));

        tableLayout.addView(tableRow);
    }

    private void saveData() {
        String jsonString = gson.toJson(transactionList);
        try {
            FileWriter writer = new FileWriter("test.json");
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(dataFile))) {
            writer.writeNext(new String[]{"Date", "Income", "ExpenseName", "Expense"});
            for (Transaction transaction : transactionList) {
                writer.writeNext(new String[]{transaction.getDate(),
                        String.valueOf(transaction.getIncome()),
                        transaction.getExpenseName(),
                        String.valueOf(transaction.getExpense())});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        if (!dataFile.exists()) {
            // The data file does not exist, no data to load.
            return;
        }

        try (CSVReader csvReader = new CSVReader(new FileReader(dataFile))) {
            // Skip the first line (header).
            csvReader.readNext();

            String[] record;
            while ((record = csvReader.readNext()) != null) {
                String date = record[0];
                double income = Double.parseDouble(record[1]);
                String expenseName = record[2];
                double expense = Double.parseDouble(record[3]);

                Transaction transaction = new Transaction(date, income, expenseName, expense);
                transactionList.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        swipeDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
