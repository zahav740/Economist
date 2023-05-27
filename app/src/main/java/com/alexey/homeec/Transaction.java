package com.alexey.homeec;

public class Transaction {
    private String date;
    private double income;
    private String expenseName;
    private double expense;

    public Transaction(String date, double income, String expenseName, double expense) {
        this.date = date;
        this.income = income;
        this.expenseName = expenseName;
        this.expense = expense;
    }

    public String getDate() {
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
