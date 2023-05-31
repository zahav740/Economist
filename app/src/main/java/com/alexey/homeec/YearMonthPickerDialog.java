package com.alexey.homeec;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.Calendar;

public class YearMonthPickerDialog implements DialogInterface.OnClickListener {

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private final AlertDialog.Builder builder;
    private final NumberPicker monthPicker, yearPicker;
    private OnYearMonthSetListener listener;
    private boolean showMonth = true;

    public YearMonthPickerDialog(Context context, boolean showMonth) {
        this.showMonth = showMonth;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.year_month_picker, null);
        builder = new AlertDialog.Builder(context).setView(dialogView).setTitle("Выберите год и месяц")
                .setPositiveButton("ОК", this)
                .setNegativeButton("Отмена", this);

        monthPicker = dialogView.findViewById(R.id.picker_month);
        yearPicker = dialogView.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(Calendar.getInstance().get(Calendar.MONTH) + 1);

        if (!showMonth) {
            monthPicker.setVisibility(View.GONE);
        }

        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(Calendar.getInstance().get(Calendar.YEAR));
    }

    public void setListener(OnYearMonthSetListener listener) {
        this.listener = listener;
    }

    public void show() {
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (listener != null) {
                    listener.onYearMonthSet(yearPicker.getValue(), monthPicker.getValue());
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                // Ничего не делаем, диалог просто закрывается
                break;
        }
    }

    public int getSelectedYear() {
        return yearPicker.getValue();
    }

    public int getSelectedMonth() {
        return monthPicker.getValue();
    }

    public interface OnYearMonthSetListener {
        void onYearMonthSet(int year, int month);
    }
}


