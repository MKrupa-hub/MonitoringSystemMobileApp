package com.example.monitoringsystem.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.example.monitoringsystem.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Calendar;
import android.app.DatePickerDialog;

public class pTemperatureFragment extends Fragment {

    EditText textDate;
    EditText textTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_p_temperature, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        textDate = getView().findViewById(R.id.textDate);
        textTime = getView().findViewById(R.id.textTime);
        makeTextNotEditable(textDate);
        makeTextNotEditable(textTime);

        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dateFragment = new SelectDateFragment();
                dateFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        textTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timeFragment = new SelectedTimeFragment();
                timeFragment.show(getFragmentManager(), "TimePicker");

            }
        });
    }

    private void makeTextNotEditable(EditText editText){
        editText.setFocusable(false);
        editText.setClickable(true);
    }

}