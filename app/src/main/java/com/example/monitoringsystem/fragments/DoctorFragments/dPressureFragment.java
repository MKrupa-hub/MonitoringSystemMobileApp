package com.example.monitoringsystem.fragments.DoctorFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.monitoringsystem.Doctor.DoctorActivity;
import com.example.monitoringsystem.Patient.Pressure;
import com.example.monitoringsystem.R;
import com.example.monitoringsystem.fragments.SelectDateFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class dPressureFragment extends Fragment {

    private EditText oneDayText;
    private EditText startDaytext;
    private EditText endDaytext;
    private String login;
    private List<Pressure> pressures = new ArrayList<>();
    private DatabaseReference mDatabase;
    private LineChart oneDayLineChart;
    private LineChart customRangeLineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_d_pressure, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        DoctorActivity doctorActivity = (DoctorActivity) getActivity();
        login = doctorActivity.sendLoginToFragment();
        startDaytext = getView().findViewById(R.id.textStartDate);
        endDaytext = getView().findViewById(R.id.textEndDate);
        oneDayText = getView().findViewById(R.id.textOneDay);
        oneDayLineChart = getView().findViewById(R.id.oneDayChart);
        customRangeLineChart = getView().findViewById(R.id.customRangeDateChart);
        oneDayLineChart.setNoDataText("Brak danych!\nWybierz date żeby zobaczyć pomiary");
        makeTextNotEditable(oneDayText);
        makeTextNotEditable(startDaytext);
        makeTextNotEditable(endDaytext);

        oneDayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneDayText.setText("");
                DialogFragment dateFragment = new SelectDateFragment(R.id.textOneDay);
                dateFragment.show(getFragmentManager(), "DatePicker");
                checkIfOneDayIsPicked();
            }
        });

        startDaytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfDataIsPicked();
                DialogFragment dateFragment = new SelectDateFragment(R.id.textStartDate);
                dateFragment.show(getFragmentManager(), "DatePicker");
                checkIfRangeIsPicked();
            }
        });
        endDaytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfDataIsPicked();
                DialogFragment dateFragment = new SelectDateFragment(R.id.textEndDate);
                dateFragment.show(getFragmentManager(), "DatePicker");
                checkIfRangeIsPicked();
            }
        });

    }

    private void checkIfDataIsPicked(){
        if(!startDaytext.getText().toString().equals("") && !endDaytext.getText().toString().equals("")){
            startDaytext.setText("");
            endDaytext.setText("");
        }
    }

    private void checkIfRangeIsPicked() {

        if(!startDaytext.getText().toString().equals("") || !endDaytext.getText().toString().equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (startDaytext.getText().toString().equals("") || endDaytext.getText().toString().equals("")) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    customRangeLineChart.clear();
                    loadRangeMeasurement(startDaytext.getText().toString(), endDaytext.getText().toString());
                }
            }).start();
        }
    }

    private void checkIfOneDayIsPicked() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (oneDayText.getText().toString().equals("")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                oneDayLineChart.clear();
                loadOneDayMeasurement(oneDayText.getText().toString());
            }
        }).start();
    }

    private void loadRangeMeasurement(String startDate, String endDate) {
        Calendar startCalendarDate = formatDate(startDate);
        Calendar endCalendarDate = formatDate(endDate);

        mDatabase = FirebaseDatabase.getInstance().getReference("pressures").child(login);
        mDatabase.orderByChild("timestamp").startAt(evalutateStartDay(startCalendarDate)).endAt(evalutateEndDay(endCalendarDate)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Entry> entryList = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        pressures.add(s.getValue(Pressure.class));
                        entryList.add(new Entry(s.getValue(Pressure.class).getTimestamp(), s.getValue(Pressure.class).getPressureS()));
                    }
                    displayCustomRangeMeasurement(entryList);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private Calendar formatDate(String date){
        String[] limiter = date.split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.valueOf(limiter[2]), Integer.valueOf(limiter[1]) - 1, Integer.valueOf(limiter[0]));
        return calendar;
    }

    private void loadOneDayMeasurement(String oneDay) {
        Calendar calendar = formatDate(oneDay);
        mDatabase = FirebaseDatabase.getInstance().getReference("pressures").child(login);
        mDatabase.orderByChild("timestamp").startAt(evalutateStartDay(calendar)).endAt(evalutateEndDay(calendar)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Entry> entryList = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        pressures.add(s.getValue(Pressure.class));
                        entryList.add(new Entry(s.getValue(Pressure.class).getTimestamp(), s.getValue(Pressure.class).getPressureS()));
                    }
                    displayOneDayMeasurement(entryList);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void displayCustomRangeMeasurement(List<Entry> entryList){
        LineDataSet lineDataSet = new LineDataSet(entryList, "");
        lineDataSet = prepareLineDataSet(lineDataSet);
        LineData lineData = new LineData(lineDataSet);
        customRangeLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        customRangeLineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new SimpleDateFormat("dd-MM").format(value);
            }
        });
        customRangeLineChart.getDescription().setText("Średnio:" + calcuteAvarage(entryList));
        customRangeLineChart.getDescription().setTextSize(13);
        customRangeLineChart.getLegend().setEnabled(false);
        customRangeLineChart.setData(lineData);
        customRangeLineChart.invalidate();
    }

    private LineDataSet prepareLineDataSet(LineDataSet lineDataSet){
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.parseColor("#40C19D"));
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setHighLightColor(Color.parseColor("#6ADDBD"));
        lineDataSet.setValueTextSize(12);
        lineDataSet.setValueTextColor(Color.DKGRAY);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return lineDataSet;
    }

    private void displayOneDayMeasurement(List<Entry> entryList) {
        LineDataSet lineDataSet = new LineDataSet(entryList, "");
        lineDataSet = prepareLineDataSet(lineDataSet);
        LineData lineData = new LineData(lineDataSet);
        oneDayLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        oneDayLineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new SimpleDateFormat("H:mm").format(value);
            }
        });
        oneDayLineChart.getDescription().setText("Średnio:" + calcuteAvarage(entryList));
        oneDayLineChart.getDescription().setTextSize(13);
        oneDayLineChart.getLegend().setEnabled(false);
        oneDayLineChart.setData(lineData);
        oneDayLineChart.invalidate();
    }

    private float calcuteAvarage(List<Entry> entryList) {
        float sum = 0;
        for (Entry entry : entryList) {
            sum += entry.getY();
        }
        return sum / entryList.size();
    }


    private long evalutateStartDay(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    private long evalutateEndDay(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 599);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23, 59, 59);
        return calendar.getTimeInMillis();
    }

    private void makeTextNotEditable(EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);
    }
}