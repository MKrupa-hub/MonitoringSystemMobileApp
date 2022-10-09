package com.example.monitoringsystem.fragments.DoctorFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.monitoringsystem.Doctor.DoctorActivity;
import com.example.monitoringsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class dPressureFragment extends Fragment {

    private String pesel;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_d_pressure, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        DoctorActivity doctorActivity = (DoctorActivity) getActivity();
        pesel = doctorActivity.sendPeselToFragment();

        displayTodayMeasurement();
    }

    private void displayTodayMeasurement() {
        mDatabase = FirebaseDatabase.getInstance().getReference("pressures").child(pesel);
        mDatabase.orderByChild("timestamp").startAt(evalutateStartDay(Calendar.getInstance())).endAt(evalutateEndDay(Calendar.getInstance())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Toast.makeText(getContext(), String.valueOf(s.child("pressureR").getValue(Long.class)), Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private long evalutateStartDay(Calendar calendar){
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0,0,0);
        return calendar.getTimeInMillis();
    }

    private long evalutateEndDay(Calendar calendar){
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23,59,59);
        return calendar.getTimeInMillis();
    }
}