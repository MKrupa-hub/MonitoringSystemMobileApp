package com.example.monitoringsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_login = (Button) findViewById(R.id.log_in);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_in(v);
            }
        });
    }

    public void log_in(View view) {
        EditText login = (EditText) findViewById(R.id.login);
        EditText password = (EditText) findViewById(R.id.password);
        if(password.getText().toString().equals("") || login.getText().toString().equals("")){
            allert('B');
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("login").equalTo(login.getText().toString());
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String passwordFromDB = dataSnapshot.child(login.getText().toString()).child("password").getValue(String.class);
                    if (passwordFromDB.equals(password.getText().toString())) {

                    char input = login.getText().toString().charAt(0);
                    switch (input) {
                        case 'L':
                            allert('L');
                            doctorActivity();
                            break;
                        case 'P':
                            allert('P');
                            patientActivity();
                            break;
                        }
                    }
                    else {
                        allert('H');
                    }
                }
                else {
                    allert('N');
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                allert('N');

            }
        });
    }


    public void allert(char input){

        switch (input){
            case 'L':
                Toast.makeText(MainActivity.this,"Zalogowano jako Lekarz!",Toast.LENGTH_SHORT).show();
                break;
            case 'P':
                Toast.makeText(MainActivity.this,"Zalogowano jako Pacjent!",Toast.LENGTH_SHORT).show();
                break;
            case 'N':
                Toast.makeText(MainActivity.this,"Niepoprawny login!",Toast.LENGTH_SHORT).show();
                break;
            case 'H':
                Toast.makeText(MainActivity.this,"Niepoprawny hasło!",Toast.LENGTH_SHORT).show();
                break;
            case 'B':
                Toast.makeText(MainActivity.this,"Login i hasło nie mogą być puste!",Toast.LENGTH_SHORT).show();
                break;

        }
    }

    public void doctorActivity(){
        Intent intent = new Intent(this, DoctorActivity.class);
        startActivity(intent);
    }

    public void patientActivity(){
        Intent intent = new Intent(this, PatientActivity.class);
        startActivity(intent);
    }


    public void registerActivity(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}