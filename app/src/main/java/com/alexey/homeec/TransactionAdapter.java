package com.alexey.homeec;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private Context context;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        super(context, R.layout.table_row, transactions);
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.table_row, parent, false);

        TextView dateView = rowView.findViewById(R.id.dateTextView);
        TextView incomeView = rowView.findViewById(R.id.incomeTextView);
        TextView categoryView = rowView.findViewById(R.id.categoryTextView);
        TextView expenseView = rowView.findViewById(R.id.expenseTextView);

        Transaction transaction = transactions.get(position);
        dateView.setText(transaction.getDate());
        incomeView.setText(String.valueOf(transaction.getIncome()));
        categoryView.setText(transaction.getExpenseName());
        expenseView.setText(String.valueOf(transaction.getExpense()));

        return rowView;
    }
}
