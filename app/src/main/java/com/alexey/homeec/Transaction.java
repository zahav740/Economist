package com.alexey.homeec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Transaction {
    private Date date;
    private double income;
    private String expenseName;
    private double expense;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public Transaction(String date, double income, String expenseName, double expense) {
        try {
            this.date = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.income = income;
        this.expenseName = expenseName;
        this.expense = expense;
    }

    public Date getDate() {
        return date;
    }

    public double getIncome() {
        return income;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public double getExpense() {
        return expense;
    }
}
