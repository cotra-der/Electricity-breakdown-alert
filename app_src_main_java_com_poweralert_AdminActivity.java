package com.poweralert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.poweralert.utils.FirebaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {
    
    private Spinner sectorSpinner;
    private Button btnReportPowerCut;
    private Button btnRestorePower;
    private Button btnViewMap;
    private TextView logView;
    
    private String[] sectors = {"sector_1", "sector_2", "sector_3", "sector_4", "sector_5", 
                                "sector_6", "sector_7", "sector_8", "sector_9", "sector_10",
                                "sector_11", "sector_12"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        
        // Initialize views
        sectorSpinner = findViewById(R.id.sectorSpinner);
        btnReportPowerCut = findViewById(R.id.btnReportPowerCut);
        btnRestorePower = findViewById(R.id.btnRestorePower);
        btnViewMap = findViewById(R.id.btnViewMap);
        logView = findViewById(R.id.logView);
        
        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, sectors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectorSpinner.setAdapter(adapter);
        
        // Report power cut button click
        btnReportPowerCut.setOnClickListener(v -> {
            String selectedSector = sectorSpinner.getSelectedItem().toString();
            FirebaseHelper.reportPowerOutage(
                    selectedSector,
                    "Admin User",
                    "Estimated 2 hours"
            );
            addLogEntry("Power outage reported in " + selectedSector);
            Toast.makeText(AdminActivity.this, 
                    "Power outage reported for " + selectedSector, 
                    Toast.LENGTH_SHORT).show();
        });
        
        // Restore power button click
        btnRestorePower.setOnClickListener(v -> {
            String selectedSector = sectorSpinner.getSelectedItem().toString();
            FirebaseHelper.restorePower(selectedSector, "Admin User");
            addLogEntry("Power restored in " + selectedSector);
            Toast.makeText(AdminActivity.this, 
                    "Power restored for " + selectedSector, 
                    Toast.LENGTH_SHORT).show();
        });
        
        // View map button click
        btnViewMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(AdminActivity.this, MapActivity.class);
            startActivity(mapIntent);
        });
        
        // Monitor status changes
        setupStatusMonitoring();
    }
    
    private void setupStatusMonitoring() {
        for (String sector : sectors) {
            FirebaseHelper.getSectorReference(sector).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String status = dataSnapshot.child("status").getValue(String.class);
                        long timestamp = dataSnapshot.child("lastUpdated").getValue(Long.class);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String timeStr = sdf.format(new Date(timestamp));
                        
                        if (status.equals("power_cut")) {
                            addLogEntry("[" + timeStr + "] " + sector + ": Power outage reported");
                        } else if (status.equals("normal")) {
                            addLogEntry("[" + timeStr + "] " + sector + ": Power restored");
                        }
                    }
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    addLogEntry("Error monitoring " + sector);
                }
            });
        }
    }
    
    private void addLogEntry(String entry) {
        String currentLog = logView.getText().toString();
        logView.setText(entry + "\n" + currentLog);
    }
}