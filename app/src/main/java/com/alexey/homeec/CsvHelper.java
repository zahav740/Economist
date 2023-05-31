package com.alexey.homeec;

import android.content.Context;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CsvHelper {
    private Context context;

    public CsvHelper(Context context) {
        this.context = context;
    }

    public List<Transaction> readTransactionRecords() {
        List<Transaction> transactions = new ArrayList<>();

        try {
            InputStream inputStream = this.context.getAssets().open("data.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] transactionData = line.split(",");

                String date = transactionData[0];
                double income = Double.parseDouble(transactionData[1]);
                String expenseName = transactionData[2];
                double expense = Double.parseDouble(transactionData[3]);

                Transaction transaction = new Transaction(date, income, expenseName, expense);

                Calendar cal = Calendar.getInstance();
                cal.setTime(transaction.getDate());
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);

                transactions.add(transaction);
            }

            reader.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
