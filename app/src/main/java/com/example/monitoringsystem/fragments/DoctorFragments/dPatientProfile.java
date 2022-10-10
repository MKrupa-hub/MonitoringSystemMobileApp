package com.example.monitoringsystem.fragments.DoctorFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.monitoringsystem.Doctor.DoctorActivity;
import com.example.monitoringsystem.Patient.PatientMainActivity;
import com.example.monitoringsystem.R;
import com.example.monitoringsystem.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class dPatientProfile extends Fragment {

    private String login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_p_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        DoctorActivity doctorActivity = (DoctorActivity) getActivity();
        fillData(doctorActivity.sendLoginToFragment());
    }


    void fillData(String login) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("login").getValue(String.class).equals(login)) {
                        User user = dataSnapshot.getValue(User.class);
                        TextView textViewLogin = getView().findViewById(R.id.login_display);
                        TextView textViewName = getView().findViewById(R.id.name_display);
                        TextView textViewSurname = getView().findViewById(R.id.surname_display);
                        TextView textViewPesel = getView().findViewById(R.id.pesel_display);
                        TextView textViewPhone = getView().findViewById(R.id.phone_display);

                        textViewLogin.setText(user.getLogin());
                        textViewName.setText(user.getName());
                        textViewSurname.setText(user.getSurname());
                        textViewPhone.setText(String.valueOf(user.getPhone()));
                        textViewPesel.setText(String.valueOf(user.getPesel()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}