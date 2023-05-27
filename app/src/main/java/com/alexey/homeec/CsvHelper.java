package com.alexey.homeec;

import android.content.Context;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvHelper {
    private Context context;

    public CsvHelper(Context context) {
        this.context = context;
    }

    public List<Transaction> readTransactionRecords() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader("transaction_history.csv"));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String date = nextLine[0];
                double income = Double.parseDouble(nextLine[1]);
                String expenseName = nextLine[2];
                double expense = Double.parseDouble(nextLine[3]);
                Transaction transaction = new Transaction(date, income, expenseName, expense);
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        return transactions;
    }

}
