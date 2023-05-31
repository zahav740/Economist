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
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MainActivity extends AppCompatActivity {

    private double currentBalance = 0.0;
    private List<Transaction> transactionList;
    private TableLayout tableLayout;
    private Gson gson = new Gson();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat monthSdf = new SimpleDateFormat("yyyy-MM");
    private SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
    private File dataFile, dayFile, monthFile, yearFile;
    private SwipeDetector swipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.tableLayout);
        transactionList = new ArrayList<>();
        dataFile = new File(getFilesDir(), "data.csv");
        dayFile = new File(getFilesDir(), "day.csv");
        monthFile = new File(getFilesDir(), "month.csv");
        yearFile = new File(getFilesDir(), "year.csv");

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
                                saveData(transaction);

                                // Add row to table
                                addRowToTable(transaction);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(true)
                    .show();
        });

        swipeDetector = new SwipeDetector();
        tableLayout.setOnTouchListener(swipeDetector);
    }

    private void addRowToTable(Transaction transaction) {
        TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);

        // Форматирование даты в строку
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dateString = formatter.format(transaction.getDate());

        ((TextView) row.findViewById(R.id.dateTextView)).setText(dateString);
        ((TextView) row.findViewById(R.id.incomeTextView)).setText(String.valueOf(transaction.getIncome()));
        ((TextView) row.findViewById(R.id.categoryTextView)).setText(transaction.getExpenseName());
        ((TextView) row.findViewById(R.id.expenseTextView)).setText(String.valueOf(transaction.getExpense()));
        tableLayout.addView(row);
        row.setOnTouchListener(swipeDetector);
    }


    private void saveData(Transaction transaction) {
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
            for (Transaction tr : transactionList) {
                writer.writeNext(new String[]{sdf.format(tr.getDate()),
                        String.valueOf(tr.getIncome()),
                        tr.getExpenseName(),
                        String.valueOf(tr.getExpense())});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save to day.csv
        saveToDayCsv(transaction);

        // Update month.csv
        updateMonthCsv();

        // Update year.csv
        updateYearCsv();
    }

    private void saveToDayCsv(Transaction transaction) {
        // Save transaction to day.csv
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(dayFile, true))) {
            // Форматирование даты в строку
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String dateString = formatter.format(transaction.getDate());
            csvWriter.writeNext(new String[]{dateString, String.valueOf(transaction.getIncome() - transaction.getExpense())});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateMonthCsv() {
        Map<String, Double> monthlyRecords = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(dayFile))) {
            List<String[]> records = csvReader.readAll();
            for (String[] record : records) {
                String date = record[0];
                double amount = Double.parseDouble(record[1]);
                String month = monthSdf.format(sdf.parse(date));
                // Используйте get и проверьте на null
                Double previousAmount = monthlyRecords.get(month);
                if (previousAmount == null) {
                    previousAmount = 0.0;
                }
                monthlyRecords.put(month, previousAmount + amount);
            }
        } catch (IOException | CsvException | ParseException e) {
            e.printStackTrace();
        }

        // Save monthlyRecords map back to month.csv file
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(monthFile))) {
            for (Map.Entry<String, Double> entry : monthlyRecords.entrySet()) {
                csvWriter.writeNext(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateYearCsv() {
        Map<String, Double> yearlyRecords = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(monthFile))) {
            List<String[]> records = csvReader.readAll();
            for (String[] record : records) {
                String date = record[0];
                double amount = Double.parseDouble(record[1]);
                String year = yearSdf.format(monthSdf.parse(date));
                // Используйте get и проверьте на null
                Double previousAmount = yearlyRecords.get(year);
                if (previousAmount == null) {
                    previousAmount = 0.0;
                }
                yearlyRecords.put(year, previousAmount + amount);
            }
        } catch (IOException | CsvException | ParseException e) {
            e.printStackTrace();
        }

        // Save yearlyRecords map back to year.csv file
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(yearFile))) {
            for (Map.Entry<String, Double> entry : yearlyRecords.entrySet()) {
                csvWriter.writeNext(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadTransactions() {
        try (CSVReader reader = new CSVReader(new FileReader(dataFile))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String date = row[0];
                double income = Double.parseDouble(row[1]);
                String expenseName = row[2];
                double expense = Double.parseDouble(row[3]);
                transactionList.add(new Transaction(date, income, expenseName, expense));
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private class SwipeDetector implements View.OnTouchListener {
        private static final int MIN_DISTANCE = 100;
        private float downX, upX;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    return true;
                case MotionEvent.ACTION_UP:
                    upX = event.getX();
                    float deltaX = downX - upX;

                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        if (deltaX < 0) {
                            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                            startActivity(intent);
                            return true;
                        }
                    }
                    return false;
            }
            return false;
        }
    }
}
