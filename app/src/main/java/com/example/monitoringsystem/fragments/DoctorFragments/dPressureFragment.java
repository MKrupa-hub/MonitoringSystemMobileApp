package com.example.monitoringsystem.fragments.DoctorFragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

import java.text.DecimalFormat;
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
    private Spinner spinner;
    private String typeOfMeasurement;
    private static final String[] paths = {"Skurczowe", "Rozkurczowe", "Puls"};

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
        startDaytext = getView().findViewById(R.id.pTextStartDate);
        endDaytext = getView().findViewById(R.id.pTextEndDate);
        oneDayText = getView().findViewById(R.id.pTextOneDay);
        oneDayLineChart = getView().findViewById(R.id.pOneDayChart);
        customRangeLineChart = getView().findViewById(R.id.pCustomRangeDateChart);
        oneDayLineChart.setNoDataText("Brak danych!\nWybierz date żeby zobaczyć pomiary");
        customRangeLineChart.setNoDataText("Brak danych!\nWybierz date żeby zobaczyć pomiary");
        makeTextNotEditable(oneDayText);
        makeTextNotEditable(startDaytext);
        makeTextNotEditable(endDaytext);
        spinner = getView().findViewById(R.id.pSpinner);
        spinner.getBackground().setColorFilter(Color.parseColor("#40C19D"), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        typeOfMeasurement = "pressureS";
                        break;
                    case 1:
                        typeOfMeasurement = "pressureR";
                        break;
                    case 2:
                        typeOfMeasurement = "pulse";
                        break;
                }
                loadRangeMeasurement(startDaytext.getText().toString(), endDaytext.getText().toString());
                loadOneDayMeasurement(oneDayText.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setUpDates();

        oneDayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneDayText.setText("");
                DialogFragment dateFragment = new SelectDateFragment(R.id.pTextOneDay);
                dateFragment.show(getChildFragmentManager(), "DatePicker");
                checkIfOneDayIsPicked();
            }
        });

        startDaytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfDataIsPicked();
                DialogFragment dateFragment = new SelectDateFragment(R.id.pTextStartDate);
                dateFragment.show(getChildFragmentManager(), "DatePicker");
                checkIfRangeIsPicked();
            }
        });
        endDaytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfDataIsPicked();
                DialogFragment dateFragment = new SelectDateFragment(R.id.pTextEndDate);
                dateFragment.show(getChildFragmentManager(), "DatePicker");
                checkIfRangeIsPicked();
            }
        });

    }

    private void setUpDates() {
        Calendar calendar = Calendar.getInstance();
        oneDayText.setText(calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        startDaytext.setText(calendar.get(Calendar.DATE) - 5 + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        endDaytext.setText(calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        loadRangeMeasurement(startDaytext.getText().toString(), endDaytext.getText().toString());
        loadOneDayMeasurement(oneDayText.getText().toString());
    }

    private void checkIfDataIsPicked() {
        if (!startDaytext.getText().toString().equals("") && !endDaytext.getText().toString().equals("")) {
            startDaytext.setText("");
            endDaytext.setText("");
        }
    }

    private void checkIfRangeIsPicked() {

        if (!startDaytext.getText().toString().equals("") || !endDaytext.getText().toString().equals("")) {
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
                        entryList.add(loadDataAccordingToType(s,entryList));
                    }
                    displayCustomRangeMeasurement(entryList);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private Entry loadDataAccordingToType(DataSnapshot s, List<Entry> entryList ){
        if(typeOfMeasurement.equals("pressureS")){
            return new Entry(s.getValue(Pressure.class).getTimestamp(), s.getValue(Pressure.class).getPressureS());
        }else if(typeOfMeasurement.equals("pressureR")){
            return new Entry(s.getValue(Pressure.class).getTimestamp(), s.getValue(Pressure.class).getPressureR());
        }else{
            return new Entry(s.getValue(Pressure.class).getTimestamp(), s.getValue(Pressure.class).getPulse());
        }
    }

    private Calendar formatDate(String date) {
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
                        entryList.add(loadDataAccordingToType(s,entryList));
                    }
                    displayOneDayMeasurement(entryList);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void displayOneDayMeasurement(List<Entry> entryList) {

        oneDayLineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new SimpleDateFormat("HH:mm").format(value);
            }
        });
        oneDayLineChart = setMinAndMaxXValuesOneDay(oneDayLineChart);
        prepareChart(oneDayLineChart, setUpLineData(entryList), entryList).invalidate();
    }

    private LineChart prepareChart(LineChart lineChart, LineData lineData, List<Entry> entryList){
        lineData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new DecimalFormat("0").format(Double.valueOf(value));
            }
        });
        lineChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);
        lineChart.getAxisRight().setAxisLineColor(Color.TRANSPARENT);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getDescription().setText("Średnio: " + calcuteAvarage(entryList));
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getDescription().setTextSize(13);
        lineChart.setScaleYEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setData(lineData);
        return lineChart;
    }

    private LineData setUpLineData(List<Entry> entryList){
        LineDataSet lineDataSet = new LineDataSet(entryList, "");
        lineDataSet = prepareLineDataSet(lineDataSet);
        lineDataSet = estimateColorValue(lineDataSet);
        return new LineData(lineDataSet);
    }

    private void displayCustomRangeMeasurement(List<Entry> entryList) {
        customRangeLineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new SimpleDateFormat("dd-MM").format(value);
            }
        });
        customRangeLineChart = setMinAndMaxXValuesCustomRange(customRangeLineChart);
        prepareChart(customRangeLineChart, setUpLineData(entryList), entryList).invalidate();
    }

    private LineChart setMinAndMaxXValuesOneDay(LineChart lineChart){
        Calendar startCalendarDate = formatDate(oneDayText.getText().toString());
        lineChart.getXAxis().setAxisMinimum(evalutateStartDay(startCalendarDate));
        lineChart.getXAxis().setAxisMaximum(evalutateEndDay(startCalendarDate));

        return lineChart;
    }

    private LineChart setMinAndMaxXValuesCustomRange(LineChart lineChart){
        Calendar startCalendarDate = formatDate(startDaytext.getText().toString());
        Calendar endCalendarDate = formatDate(endDaytext.getText().toString());
        lineChart.getXAxis().setAxisMinimum(evalutateStartDay(startCalendarDate));
        lineChart.getXAxis().setAxisMaximum(evalutateEndDay(endCalendarDate));

        return lineChart;
    }

    private LineDataSet estimateColorValue(LineDataSet lineDataSet) {
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (Entry entry : lineDataSet.getValues()) {
            if(typeOfMeasurement.equals("pressureS")){
                if (entry.getY() <= 119)
                    colors.add(Color.BLUE);
                else if (entry.getY() >= 120 && entry.getY() <= 139)
                    colors.add(Color.GREEN);
                else if (entry.getY() >= 140)
                    colors.add(Color.RED);
            }else if(typeOfMeasurement.equals("pressureR")){
                if (entry.getY() <= 79)
                    colors.add(Color.BLUE);
                else if (entry.getY() >= 80 && entry.getY() <= 89)
                    colors.add(Color.GREEN);
                else if (entry.getY() >= 90)
                    colors.add(Color.RED);
            }else{
                if (entry.getY() <= 59)
                    colors.add(Color.BLUE);
                else if (entry.getY() >= 60 && entry.getY() <= 100)
                    colors.add(Color.GREEN);
                else if (entry.getY() >= 101)
                    colors.add(Color.RED);
            }
        }
        lineDataSet.setCircleColors(colors);
        lineDataSet.setColor(Color.parseColor("#40C19D"));
        return lineDataSet;
    }

    private LineDataSet prepareLineDataSet(LineDataSet lineDataSet) {
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setValueTextSize(12);
        lineDataSet.setValueTextColor(Color.DKGRAY);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return lineDataSet;
    }



    private String calcuteAvarage(List<Entry> entryList) {
        float sum = 0;
        for (Entry entry : entryList) {
            sum += entry.getY();
        }
        return new DecimalFormat("0.00").format(Double.valueOf(sum / entryList.size()));
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