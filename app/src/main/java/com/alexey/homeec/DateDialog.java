package com.alexey.homeec;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

public class DateDialog {
    private Context context;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public DateDialog(Context context, DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.context = context;
        this.onDateSetListener = onDateSetListener;
    }

    public void showDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, onDateSetListener, year, month, day);
        datePickerDialog.show();
    }
}

