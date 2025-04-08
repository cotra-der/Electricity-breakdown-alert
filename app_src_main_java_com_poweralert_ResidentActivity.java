package com.poweralert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.poweralert.utils.FirebaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ResidentActivity extends AppCompatActivity {
    
    private TextView sectorText;
    private TextView powerStatusText;
    private TextView lastUpdatedText;
    private TextView updatesLog;
    private Button btnViewMap;
    
    private String userSector = "sector_12"; // This would be retrieved from user preferences
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident);
        
        // Initialize views
        sectorText = findViewById(R.id.sectorText);
        powerStatusText = findViewById(R.id.powerStatusText);
        lastUpdatedText = findViewById(R.id.lastUpdatedText);
        updatesLog = findViewById(R.id.updatesLog);
        btnViewMap = findViewById(R.id.btnViewMap);
        
        // Set sector text
        sectorText.setText(userSector.replace("_", " ").toUpperCase());
        
        // Subscribe to notifications for this sector
        FirebaseHelper.subscribeToSector(userSector);
        
        // Monitor power status for this sector
        monitorPowerStatus();
        
        // View map button click
        btnViewMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(ResidentActivity.this, MapActivity.class);
            startActivity(mapIntent);
        });
    }
    
    private void monitorPowerStatus() {
        FirebaseHelper.getSectorReference(userSector).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.child("status").getValue(String.class);
                    long timestamp = dataSnapshot.child("lastUpdated").getValue(Long.class);
                    String reportedBy = dataSnapshot.child("reportedBy").getValue(String.class);
                    String estimatedRestoration = dataSnapshot.child("estimatedRestoration").getValue(String.class);
                    
                    updateStatusUI(status, timestamp);
                    addStatusUpdate(status, timestamp, reportedBy, estimatedRestoration);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                powerStatusText.setText("ERROR");
                powerStatusText.setTextColor(getResources().getColor(android.R.color.black));
                lastUpdatedText.setText("Failed to load status");
            }
        });
    }
    
    private void updateStatusUI(String status, long timestamp) {
        if (status.equals("power_cut")) {
            powerStatusText.setText("POWER OUTAGE");
            powerStatusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            powerStatusText.setText("NORMAL");
            powerStatusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
        
        // Calculate time since update
        long now = System.currentTimeMillis();
        long diffInMillis = now - timestamp;
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        
        if (diffInMinutes < 1) {
            lastUpdatedText.setText("Last updated: Just now");
        } else if (diffInMinutes == 1) {
            lastUpdatedText.setText("Last updated: 1 minute ago");
        } else {
            lastUpdatedText.setText("Last updated: " + diffInMinutes + " minutes ago");
        }
    }
    
    private void addStatusUpdate(String status, long timestamp, String reportedBy, String estimatedRestoration) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
        String timeStr = sdf.format(new Date(timestamp));
        
        StringBuilder update = new StringBuilder();
        update.append("[").append(timeStr).append("]\n");
        
        if (status.equals("power_cut")) {
            update.append("Power outage reported\n");
            if (estimatedRestoration != null && !estimatedRestoration.isEmpty()) {
                update.append("Estimated restoration: ").append(estimatedRestoration).append("\n");
            }
        } else {
            update.append("Power restored\n");
        }
        
        if (reportedBy != null && !reportedBy.isEmpty()) {
            update.append("Reported by: ").append(reportedBy).append("\n");
        }
        
        update.append("\n").append(updatesLog.getText().toString());
        updatesLog.setText(update.toString());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unsubscribe from notifications when the activity is destroyed
        FirebaseHelper.unsubscribeFromSector(userSector);
    }
}