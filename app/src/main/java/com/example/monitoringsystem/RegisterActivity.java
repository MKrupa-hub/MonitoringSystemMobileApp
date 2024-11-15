package com.example.monitoringsystem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void showPass(View view) {
        CheckBox show_pass = findViewById(R.id.show_password);
        EditText password = findViewById(R.id.r_password);
        if (show_pass.isChecked()) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void register(View view) {
        if(!isNetworkAvailable()){
            Toast.makeText(RegisterActivity.this, "Brak dostępu do internetu!", Toast.LENGTH_SHORT).show();
            return;
        }
        Button button = findViewById(R.id.button4);
        button.setEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        CheckBox checkBox = findViewById(R.id.checkBox);
        EditText name = findViewById(R.id.r_name);
        EditText surname = findViewById(R.id.r_surname);
        EditText phone = findViewById(R.id.r_phone);
        EditText password = findViewById(R.id.r_password);
        EditText pesel = findViewById(R.id.r_pesel);
        if (name.getText().toString().equals("") || surname.getText().toString().equals("") || phone.getText().toString().equals("") || password.getText().toString().equals("") || pesel.getText().toString().equals("")) {
            Toast.makeText(RegisterActivity.this, "Wszystkie pola musza byc wypelnione!", Toast.LENGTH_SHORT).show();
            freezeButton(button);
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("pesel").equalTo(Double.parseDouble(pesel.getText().toString()));

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, "Konto podanym numerze pesel jest już utworzone!", Toast.LENGTH_LONG).show();
                } else {
                    Random r = new Random();
                    StringBuilder sb = new StringBuilder();
                    sb.append(name.getText().toString().charAt(0));
                    sb.append(name.getText().toString().charAt(1));
                    sb.append(surname.getText().toString().charAt(0));
                    sb.append(surname.getText().toString().charAt(1));
                    sb.append(r.nextInt(10));
                    sb.append(r.nextInt(10));
                    sb.append(r.nextInt(10));
                    if (checkBox.isChecked()) {
                        sb.insert(0, 'L');
                        User user = new User(name.getText().toString(), surname.getText().toString(), Integer.parseInt(phone.getText().toString()), sb.toString(), password.getText().toString(), Long.parseLong(pesel.getText().toString()));
                        mDatabase.child("users").child(user.getLogin()).setValue(user);
                        displayLogin(user.getLogin());
                    } else {
                        sb.insert(0, 'P');
                        User user = new User(name.getText().toString(), surname.getText().toString(), Integer.parseInt(phone.getText().toString()), sb.toString(), password.getText().toString(), Long.parseLong(pesel.getText().toString()));
                        mDatabase.child("users").child(user.getLogin()).setValue(user);
                        displayLogin(user.getLogin());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        freezeButton(button);
    }

    private void displayLogin(String login){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Uwaga !\n");
        alertDialog.setMessage("Utworzono konto, \ntwój login to: " + login + ".\nZapamiętaj go.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }


    private void freezeButton(Button button){
        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(true);
            }
        },500);
    }


}