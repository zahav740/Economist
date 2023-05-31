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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private boolean showByDay = false;
    private boolean showByMonth = false;
    private boolean showByYear = false;

    private TableLayout historyTable;

    private SwipeDetector swipeDetector;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat monthSdf = new SimpleDateFormat("yyyy-MM");
    private SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");

    private File dayFile, monthFile, yearFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyTable = findViewById(R.id.historyTable);

        dayFile = new File(getFilesDir(), "day.csv");
        monthFile = new File(getFilesDir(), "month.csv");
        yearFile = new File(getFilesDir(), "year.csv");

        Button buttonDay = findViewById(R.id.buttonDay);
        buttonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showByDay = true;
                showByMonth = false;
                showByYear = false;
                showDatePickerDialog();
            }
        });

        Button buttonMonth = findViewById(R.id.buttonMonth);
        buttonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showByDay = false;
                showByMonth = true;
                showByYear = false;
                showDatePickerDialog();
            }
        });

        Button buttonYear = findViewById(R.id.buttonYear);
        buttonYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showByDay = false;
                showByMonth = false;
                showByYear = true;
                showDatePickerDialog();
            }
        });

        swipeDetector = new SwipeDetector(5) {
            @Override
            public void onSwipeDetected(Direction direction) {
                if (direction == Direction.RIGHT) {
                    // Open MainActivity
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

    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        loadTransactionHistory(c);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        if (showByMonth) {
            datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        } else if (showByYear) {
            datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
            datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("month", "id", "android")).setVisibility(View.GONE);
        }

        datePickerDialog.show();
    }

    private void loadTransactionHistory(Calendar c) {
        String date = "";

        if (showByDay) {
            date = sdf.format(c.getTime());
            List<Transaction> transactions = getTransactionsForDay(date);
            populateTableWithTransactions(transactions);
        } else if (showByMonth) {
            date = monthSdf.format(c.getTime());
            List<Transaction> transactions = getTransactionsForMonth(date);
            populateTableWithTransactions(transactions);
        } else if (showByYear) {
            date = yearSdf.format(c.getTime());
            List<Transaction> transactions = getTransactionsForYear(date);
            populateTableWithTransactions(transactions);
        }
    }

    private List<Transaction> getTransactionsForDay(String date) {
        List<Transaction> transactions = new ArrayList<>();

        if (dayFile.exists()) {
            try (CSVReader csvReader = new CSVReader(new FileReader(dayFile))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    String transactionDate = record[0];
                    if (transactionDate.equals(date)) {
                        double expense = Double.parseDouble(record[1]);
                        Transaction transaction = new Transaction(transactionDate, 0.0, "", expense);
                        transactions.add(transaction);
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        return transactions;
    }


    private List<Transaction> getTransactionsForMonth(String date) {
        List<Transaction> transactions = new ArrayList<>();

        if (monthFile.exists()) {
            try (CSVReader csvReader = new CSVReader(new FileReader(monthFile))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    String transactionDate = record[0];
                    if (transactionDate.startsWith(date)) {
                        double expense = Double.parseDouble(record[1]);
                        Transaction transaction = new Transaction(transactionDate, 0.0, "", expense);
                        transactions.add(transaction);
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        return transactions;
    }


    private List<Transaction> getTransactionsForYear(String date) {
        List<Transaction> transactions = new ArrayList<>();

        if (yearFile.exists()) {
            try (CSVReader csvReader = new CSVReader(new FileReader(yearFile))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    String transactionDate = record[0];
                    if (transactionDate.startsWith(date)) {
                        double expense = Double.parseDouble(record[1]);
                        Transaction transaction = new Transaction(transactionDate, 0.0, "", expense);
                        transactions.add(transaction);
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        return transactions;
    }


    private void populateTableWithTransactions(List<Transaction> transactions) {
        historyTable.removeAllViews();

        for (Transaction transaction : transactions) {
            TableRow row = new TableRow(this);

            // Customize your table row based on the details of the transaction
            // For example:
            TextView dateTextView = new TextView(this);
            dateTextView.setText(sdf.format(transaction.getDate()));
            row.addView(dateTextView);

            TextView incomeTextView = new TextView(this);
            incomeTextView.setText(String.valueOf(transaction.getIncome()));
            row.addView(incomeTextView);

            TextView expenseNameTextView = new TextView(this);
            expenseNameTextView.setText(transaction.getExpenseName());
            row.addView(expenseNameTextView);

            TextView expenseTextView = new TextView(this);
            expenseTextView.setText(String.valueOf(transaction.getExpense()));
            row.addView(expenseTextView);

            historyTable.addView(row);
        }
    }
}
