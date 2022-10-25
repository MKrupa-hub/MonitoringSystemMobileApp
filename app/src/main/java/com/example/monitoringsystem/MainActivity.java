package com.example.monitoringsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.monitoringsystem.Doctor.DoctorMainActivity;
import com.example.monitoringsystem.Patient.PatientMainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private EditText login;
    private EditText password;
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_login = (Button) findViewById(R.id.logInButton);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_in(v);
            }
        });
    }

    public void remindPassword(View view) {
        Intent intent = new Intent(this, RemindPassword.class);
        startActivity(intent);
    }

    public void log_in(View view) {
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        logInButton = findViewById(R.id.logInButton);
        logInButton.setEnabled(false);
        if (password.getText().toString().equals("") || login.getText().toString().equals("")) {
            allert('B');
            return;
        }
        if (isNetworkAvailable()) {
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
                                    doctorActivity(login.getText().toString());
                                    break;
                                case 'P':
                                    allert('P');
                                    patientActivity(login.getText().toString());
                                    ;
                                    break;
                            }
                        } else {
                            allert('H');
                        }
                    } else {
                        allert('N');
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    allert('N');
                }
            });
        } else {
            allert('I');
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void allert(char input) {

        switch (input) {
            case 'L':
                Toast.makeText(MainActivity.this, "Zalogowano jako Lekarz!", Toast.LENGTH_SHORT).show();
                break;
            case 'P':
                Toast.makeText(MainActivity.this, "Zalogowano jako Pacjent!", Toast.LENGTH_SHORT).show();
                break;
            case 'N':
                Toast.makeText(MainActivity.this, "Niepoprawny login!", Toast.LENGTH_SHORT).show();
                break;
            case 'H':
                Toast.makeText(MainActivity.this, "Niepoprawne hasło!", Toast.LENGTH_SHORT).show();
                break;
            case 'B':
                Toast.makeText(MainActivity.this, "Login i hasło nie mogą być puste!", Toast.LENGTH_SHORT).show();
                break;
            case 'I':
                Toast.makeText(MainActivity.this, "Brak dostępu do internetu!", Toast.LENGTH_SHORT).show();
                break;

        }
        freezeButton();
    }

    public void doctorActivity(String login) {
        Intent intent = new Intent(this, DoctorMainActivity.class);
        intent.putExtra("login", login);
        startActivity(intent);
    }

    public void patientActivity(String login) {
        Intent intent = new Intent(this, PatientMainActivity.class);
        intent.putExtra("login", login);
        startActivity(intent);
    }


    public void registerActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void freezeButton() {
        logInButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                logInButton.setEnabled(true);
            }
        }, 500);
    }

}