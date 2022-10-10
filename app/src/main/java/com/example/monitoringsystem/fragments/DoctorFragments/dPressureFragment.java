package com.example.monitoringsystem.fragments.DoctorFragments;

import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class dPressureFragment extends Fragment {

    private EditText oneDay;
    private String login;
    private List<Pressure> pressures = new ArrayList<>();
    private DatabaseReference mDatabase;
    private Calendar calendar;

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
        oneDay = getView().findViewById(R.id.textOneDay);

        makeTextNotEditable(oneDay);

        oneDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneDay.setText("");
                DialogFragment dateFragment = new SelectDateFragment(R.id.textOneDay);
                dateFragment.show(getFragmentManager(), "DatePicker");
                checkIfDataIsPicked();
            }
        });

    }

    private void checkIfDataIsPicked(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(oneDay.getText().toString().equals("")){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                displayOneDayMeasurement(oneDay.getText().toString());
            }
        }).start();
    }

    private void displayOneDayMeasurement(String oneDay) {
        String[] date = oneDay.split("/");
        calendar = Calendar.getInstance();
        calendar.set(Integer.valueOf(date[2]), Integer.valueOf(date[1]) - 1, Integer.valueOf(date[0]));
        mDatabase = FirebaseDatabase.getInstance().getReference("pressures").child(login);
        mDatabase.orderByChild("timestamp").startAt(evalutateStartDay(calendar)).endAt(evalutateEndDay(calendar)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Toast.makeText(getContext(), s.getValue(Pressure.class).getPressureS()+"\n" + s.getValue(Pressure.class).getPressureR() +"\n" + s.getValue(Pressure.class).getPulse(), Toast.LENGTH_SHORT).show();
                        pressures.add(s.getValue(Pressure.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private long evalutateStartDay(Calendar calendar){
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0,0,0);
        return calendar.getTimeInMillis();
    }

    private long evalutateEndDay(Calendar calendar){
        calendar.set(Calendar.MILLISECOND, 599);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23,59,59);
        return calendar.getTimeInMillis();
    }

    private void makeTextNotEditable(EditText editText){
        editText.setFocusable(false);
        editText.setClickable(true);
    }
}