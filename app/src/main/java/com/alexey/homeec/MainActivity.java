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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

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

        ((TextView) tableRow.findViewById(R.id.dateTextView)).setText(sdf.format(transaction.getDate()));
        ((TextView) tableRow.findViewById(R.id.incomeTextView)).setText(String.valueOf(transaction.getIncome()));
        ((TextView) tableRow.findViewById(R.id.categoryTextView)).setText(transaction.getExpenseName());
        ((TextView) tableRow.findViewById(R.id.expenseTextView)).setText(String.valueOf(transaction.getExpense()));

        tableLayout.addView(tableRow);
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
        Map<String, Double> dailyRecords = new HashMap<>();

        if (dayFile.exists()) {
            try (CSVReader csvReader = new CSVReader(new FileReader(dayFile))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    String date = record[0];
                    double expense = Double.parseDouble(record[1]);
                    dailyRecords.put(date, expense);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        // Add new transaction to daily records
        String date = sdf.format(transaction.getDate());
        double expense = transaction.getExpense();
        double previousExpense = 0.0;
        if (dailyRecords.containsKey(date)) {
            previousExpense = dailyRecords.get(date);
        }
        dailyRecords.put(date, previousExpense + expense);

        // Save dailyRecords map back to day.csv file
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(dayFile))) {
            for (Map.Entry<String, Double> entry : dailyRecords.entrySet()) {
                csvWriter.writeNext(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateMonthCsv() {
        Map<String, Double> monthlyRecords = new HashMap<>();

        if (monthFile.exists()) {
            try (CSVReader csvReader = new CSVReader(new FileReader(monthFile))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    String date = record[0];
                    double expense = Double.parseDouble(record[1]);
                    monthlyRecords.put(date, expense);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        // Update monthly records based on daily records in day.csv file
        if (dayFile.exists()) {
            try (CSVReader csvReader = new CSVReader(new FileReader(dayFile))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    String date = record[0].substring(0, 7); // Extract the month from the date
                    double expense = Double.parseDouble(record[1]);
                    double previousExpense = 0.0;
                    if (monthlyRecords.containsKey(date)) {
                        previousExpense = monthlyRecords.get(date);
                    }
                    monthlyRecords.put(date, previousExpense + expense);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateYearCsv() {
        Map<String, Double> yearlyRecords = new HashMap<>();

        if (yearFile.exists()) {
            try (CSVReader csvReader = new CSVReader(new FileReader(yearFile))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    String date = record[0];
                    double expense = Double.parseDouble(record[1]);
                    yearlyRecords.put(date, expense);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        // Update yearly records based on monthly records in month.csv file
        if (monthFile.exists()) {
            try (CSVReader csvReader = new CSVReader(new FileReader(monthFile))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    String date = record[0].substring(0, 4); // Extract the year from the date
                    double expense = Double.parseDouble(record[1]);

                    // Check if date already exists in the map, otherwise default to 0.0
                    double previousExpense = yearlyRecords.containsKey(date) ? yearlyRecords.get(date) : 0.0;

                    yearlyRecords.put(date, previousExpense + expense);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
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
        if (dataFile.exists()) {
            try (CSVReader reader = new CSVReader(new FileReader(dataFile))) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    String date = line[0];
                    String incomeStr = line[1];
                    double income = 0.0;
                    if (incomeStr.matches("-?\\d+(\\.\\d+)?")) {
                        income = Double.parseDouble(incomeStr);
                    } else {
                        continue; // Пропускать некорректные значения
                    }
                    double expense = Double.parseDouble(line[3]);
                    String expenseName = line[2]; // Добавить объявление переменной expenseName
                    Transaction transaction = new Transaction(date, income, expenseName, expense);
                    transactionList.add(transaction);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        swipeDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}