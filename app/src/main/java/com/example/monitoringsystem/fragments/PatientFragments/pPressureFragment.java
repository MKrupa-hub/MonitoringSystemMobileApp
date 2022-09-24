package com.example.monitoringsystem.fragments.PatientFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.monitoringsystem.Patient.PatientActivity;
import com.example.monitoringsystem.Patient.Pressure;
import com.example.monitoringsystem.R;
import com.example.monitoringsystem.fragments.SelectDateFragment;
import com.example.monitoringsystem.fragments.SelectedTimeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.

 */
public class pPressureFragment extends Fragment {

    private EditText textDate;
    private EditText textTime;
    private EditText textS;
    private EditText textR;
    private EditText textPulse;
    private Button load;
    private Button save;
    private DatabaseReference mDatabase;
    private String login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_p_pressure, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        PatientActivity patientActivity = (PatientActivity) getActivity();
        login = patientActivity.sendLoginToFragment();

        textDate = getView().findViewById(R.id.textDateView);
        textTime = getView().findViewById(R.id.textTimeView);
        textS = getView().findViewById(R.id.textS);
        textR = getView().findViewById(R.id.textR);
        textPulse = getView().findViewById(R.id.textPulse);
        load = getView().findViewById(R.id.load);
        save = getView().findViewById(R.id.save);

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
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastPressure();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPressure();
            }
        });

    }

    public void addPressure() {
        if (textPulse.getText().toString().equals("") || textTime.getText().toString().equals("")
                || textDate.getText().toString().equals("")|| textR.getText().toString().equals("")
                || textS.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Wszystkie pola musza byc wypelnione!", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("pressures");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pressure pressure = new Pressure(login,textDate.getText().toString(),
                        textTime.getText().toString(),Integer.parseInt(textS.getText().toString()),
                        Integer.parseInt(textR.getText().toString()),Integer.parseInt(textPulse.getText().toString()));
                reference.child(pressure.getLogin()).child(String.valueOf(snapshot.child(pressure.getLogin()).getChildrenCount() + 1)).setValue(pressure);
                Toast.makeText(getContext(), "Dodano!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getLastPressure(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("pressures").child(login);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Pressure pressure = snapshot.child(String.valueOf(snapshot.getChildrenCount())).getValue(Pressure.class);
                    textDate.setText(String.valueOf(pressure.getDate()));
                    textTime.setText(String.valueOf(pressure.getTime()));
                    textS.setText(String.valueOf(pressure.getPressureS()));
                    textR.setText(String.valueOf(pressure.getPressureR()));
                    textPulse.setText(String.valueOf(pressure.getPulse()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeTextNotEditable(EditText editText){
        editText.setFocusable(false);
        editText.setClickable(true);
    }
}