package com.example.monitoringsystem.Doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.monitoringsystem.R;
import com.google.android.material.navigation.NavigationView;

public class DoctorActivity extends AppCompatActivity {

    private String pesel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        Intent intent = getIntent();
        pesel = intent.getStringExtra("pesel");

        DrawerLayout drawerLayout = findViewById(R.id.doctorMainActivity);

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.doctorNavigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this, R.id.doctorNavHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);

    }

    public String sendPeselToFragment(){
        return pesel;
    }
}