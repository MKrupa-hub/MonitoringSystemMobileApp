package com.example.monitoringsystem.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.monitoringsystem.Patient.Pressure;
import com.example.monitoringsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorMainActivity extends AppCompatActivity {

    private String login;
    private EditText editText;
    private ListView listView;
    private ImageView helpImage;
    private List<Patient> patiensPesel = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        editText = findViewById(R.id.peselText);
        listView = findViewById(R.id.list);
        helpImage = findViewById(R.id.helpImage);
        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, patiensPesel);
        listView.setAdapter(arrayAdapter);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");

        loadPatients();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                checkIfPatienExists(index);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                askIfRemovePatient(index);
                return true;
            }
        });
        helpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpInfo();
            }
        });

    }

    private void helpInfo(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Podpowiedź");
        alertDialog.setMessage("Witaj!\n\nU góry można dodwać pacjentów podając ich pesel.\n\n" +
                "Pacjenci będą wyświetleni w formie listy i po kliknięciu można przejść do przeglądania jego badań.\n\n" +
                "Aby usunąc pacjenta z listy należy przytrzymać pesel który chcemy usunąć.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private void askIfRemovePatient(int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Ostrzeżenie");
        builder.setMessage("Czy na pewno chcesz usunąć pacjenta z peselem: "+patiensPesel.get(index).getPesel()+"?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                removePatient(index);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    private void removePatient(int index) {
        patiensPesel.remove(index);
        reference = FirebaseDatabase.getInstance().getReference("doctorsPatients").child(login);
        reference.removeValue();
        refactorPatients();
        listView.setAdapter(arrayAdapter);

    }

    private void refactorPatients(){
        reference = FirebaseDatabase.getInstance().getReference("doctorsPatients").child(login);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i=1;
                    for (Patient patient : patiensPesel ) {
                        reference.child(String.valueOf(i)).setValue(patient);
                        i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPatients() {
        reference = FirebaseDatabase.getInstance().getReference("doctorsPatients").child(login);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        patiensPesel.add(dataSnapshot.getValue(Patient.class));
                        listView.setAdapter(arrayAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addPatient(View view) {

        if (!editText.getText().toString().matches(".*[0-9].*")) {
            Toast.makeText(DoctorMainActivity.this, "Pesel nie może być pusty", Toast.LENGTH_LONG).show();
        } else if (editText.getText().toString().length() != 11) {
            Toast.makeText(DoctorMainActivity.this, "Pesel musi składać się z 11 znaków", Toast.LENGTH_LONG).show();
        } else {
            addItem(editText.getText().toString());
            editText.setText("");
        }
    }

    private void addItem(String item) {
        if (patiensPesel.contains(new Patient(item))) {
            Toast.makeText(DoctorMainActivity.this, "Konto z peselem: " + item + " pesel jest już dodane!", Toast.LENGTH_LONG).show();
            return;
        }
        Patient patient = new Patient(item);
        patiensPesel.add(patient);
        assignPatientToDoctor(patient);
        listView.setAdapter(arrayAdapter);
    }

    private void assignPatientToDoctor(Patient patient) {
        reference = FirebaseDatabase.getInstance().getReference("doctorsPatients").child(login);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference.child(String.valueOf(snapshot.getChildrenCount() + 1)).setValue(patient);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openPatientInfo(String login) {
        Intent intent = new Intent(this, DoctorActivity.class);
        intent.putExtra("login", login);
        startActivity(intent);
    }

    private void checkIfPatienExists(int index) {
        reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("pesel").equalTo(Double.parseDouble(patiensPesel.get(index).getPesel()));
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        openPatientInfo(dataSnapshot.child("login").getValue(String.class));
                    }
                } else {
                    Toast.makeText(DoctorMainActivity.this, "Konto z peselem: " + patiensPesel.get(index).getPesel() + " jeszcze nie powstało", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}