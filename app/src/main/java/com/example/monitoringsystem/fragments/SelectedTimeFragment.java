package com.example.monitoringsystem.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.monitoringsystem.R;

import java.time.LocalDateTime;
import java.util.Calendar;

public class SelectedTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),this,h,m,true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int h, int m) {
        Fragment fragment = getParentFragment();
        EditText textView = fragment.getView().findViewById(R.id.textTime);
        String date = h+":"+m;
        textView.setText(date);
    }
}
