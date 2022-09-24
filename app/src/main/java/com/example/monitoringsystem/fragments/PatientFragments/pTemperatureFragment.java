package com.example.monitoringsystem.fragments.PatientFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.monitoringsystem.Patient.PatientActivity;
import com.example.monitoringsystem.Patient.Temperature;
import com.example.monitoringsystem.R;
import com.example.monitoringsystem.fragments.SelectDateFragment;
import com.example.monitoringsystem.fragments.SelectedTimeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class pTemperatureFragment extends Fragment {

    private EditText textDate;
    private EditText textTime;
    private EditText textTemperature;
    private TextView displayTemperature;
    private Button addButton;
    private DatabaseReference mDatabase;
    private String login;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_p_temperature, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        PatientActivity patientActivity = (PatientActivity) getActivity();
        login = patientActivity.sendLoginToFragment();

        displayTemperature = getView().findViewById(R.id.display_temperature);
        textDate = getView().findViewById(R.id.textDateView);
        textTime = getView().findViewById(R.id.textTimeView);
        textTemperature = getView().findViewById(R.id.textTemperature);
        addButton = getView().findViewById(R.id.add_temperature);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        makeTextNotEditable(textDate);
        makeTextNotEditable(textTime);

        getLastTemperature();

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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTemperature();
            }
        });

    }


    public void addTemperature() {
        if (textTemperature.getText().toString().equals("") || textTime.getText().toString().equals("") || textDate.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Wszystkie pola musza byc wypelnione!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Float.parseFloat(textTemperature.getText().toString()) > 44 || Float.parseFloat(textTemperature.getText().toString()) < 0) {
            Toast.makeText(getContext(), "Podana złą temperaturę!", Toast.LENGTH_SHORT).show();
            textTemperature.setText("");
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("temperatures");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Temperature temperature = new Temperature(login,textDate.getText().toString(),textTime.getText().toString(),Float.parseFloat(textTemperature.getText().toString()));
                reference.child(temperature.getLogin()).child(String.valueOf(snapshot.getChildrenCount() + 1)).setValue(temperature);
                Toast.makeText(getContext(), "Dodano!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLastTemperature(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("temperatures").child(login);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Temperature temperature = snapshot.child(String.valueOf(snapshot.getChildrenCount())).getValue(Temperature.class);
                    displayTemperature.setText(String.valueOf(temperature.getTemperature()));

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