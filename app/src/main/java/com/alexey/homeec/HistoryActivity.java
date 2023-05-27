package com.alexey.homeec;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends Activity {
    private int year, month, day;
    static final int DATE_PICKER_ID = 1111;
    Button buttonYear, buttonMonth, buttonDay;
    CsvHelper csvHelper;
    TableLayout historyTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        csvHelper = new CsvHelper(this);
        historyTable = findViewById(R.id.historyTable);

        // Слушатели для кнопок
        buttonYear = findViewById(R.id.buttonYear);
        buttonMonth = findViewById(R.id.buttonMonth);
        buttonDay = findViewById(R.id.buttonDay);

        buttonYear.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(HistoryActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        displayDataByYear(year);
                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            picker.show();
        });

        buttonMonth.setOnClickListener(v -> {
            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(HistoryActivity.this,
                    (selectedMonth, selectedYear) -> {
                        displayDataByMonth(selectedYear, selectedMonth);
                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
            builder.build().show();
        });

        buttonDay.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(HistoryActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        displayDataByDay(year, monthOfYear, dayOfMonth);
                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            picker.show();
        });

        downloadButton.setOnClickListener(v -> {
            if (isExternalStorageWritable()) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "transaction_history.csv");

                try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                    List<Transaction> transactionList = csvHelper.readTransactionRecords();
                    for (Transaction transaction : transactionList) {
                        String[] data = {transaction.getDate(), String.valueOf(transaction.getIncome()), transaction.getExpenseName(), String.valueOf(transaction.getExpense())};
                        writer.writeNext(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "External storage is not available", Toast.LENGTH_SHORT).show();
            }
        });

        displayAllData();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void loadTransactionHistory(Calendar date) {
        historyTable.removeAllViews();

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);

        List<Transaction> records;
        if (year != 0 && month != 0 && dayOfMonth != 0) {
            records = CsvHelper.getTransactionHistory(year, month, dayOfMonth);
        } else if (year != 0 && month != 0) {
            records = CsvHelper.getTransactionHistoryByMonth(year, month);
        } else if (year != 0) {
            records = CsvHelper.getTransactionHistoryByYear(year);
        } else {
            records = CsvHelper.getAllData();
        }

        if (records.isEmpty()) {

            return;
        }

        for (Transaction transaction : records) {
            String transactionDate = transaction.getDate();
            double income = transaction.getIncome();
            String expenseName = transaction.getExpenseName();
            double expense = transaction.getExpense();

            addRowToTable(transactionDate, String.valueOf(income), expenseName, String.valueOf(expense));
        }
    }


    private void addRowToTable(String date, String income, String expenseName, String expense) {
        TableRow row = new TableRow(this);
        row.addView(createStyledTextView(date));
        row.addView(createStyledTextView(income));
        row.addView(createStyledTextView(expenseName));
        row.addView(createStyledTextView(expense));

        historyTable.addView(row);
    }

    private TextView createStyledTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 0, 20, 0);

        return textView;
    }

    private void exportTransactionHistory() {
        List<Transaction> records = myCsvHelper.getAllData();

        if (records.isEmpty()) {
            showMessage("Нет данных для экспорта");
            return;
        }

        data = new StringBuilder();
        data.append("Дата,Доход,Статья,Расход\n");
        for (Transaction record : records) {
            String transactionDate = record.getDate();
            double income = record.getIncome();
            String expenseName = record.getExpenseName();
            double expense = record.getExpense();

            data.append(transactionDate).append(",").append(income).append(",").append(expenseName).append(",").append(expense).append("\n");
        }

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "transaction_history.csv");

        saveFileLauncher.launch(intent);
    }


    private void saveDataToFile(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(data.toString().getBytes());
                outputStream.close();
            } else {
                showMessage("Не удалось открыть поток вывода");
            }
        } catch (IOException e) {
            showMessage("Ошибка записи в файл: " + e.getMessage());
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void displayAllData() {
        List<Transaction> allData = csvHelper.readTransactionRecords();
        displayData(allData);
    }
    private void displayData(List<Transaction> data) {
        for (Transaction transaction : data) {
            TableRow row = new TableRow(this);
            row.addView(getTextView(transaction.getDate()));
            row.addView(getTextView(Double.toString(transaction.getIncome())));
            row.addView(getTextView(transaction.getExpenseName()));
            row.addView(getTextView(Double.toString(transaction.getExpense())));
            historyTable.addView(row);
        }
    }

    private TextView getTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        return textView;
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                // Вызов DatePickerDialog с текущей датой
                return new DatePickerDialog(this, datePickerListener, year, month, day);
        }
        return null;
    }
}
