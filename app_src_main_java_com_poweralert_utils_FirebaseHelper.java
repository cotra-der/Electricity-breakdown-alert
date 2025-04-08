package com.poweralert.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.poweralert.models.PowerOutage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private static final String POWER_OUTAGES_REF = "power_outages";
    
    // Get reference to a specific sector's power status
    public static DatabaseReference getSectorReference(String sectorId) {
        return FirebaseDatabase.getInstance().getReference(POWER_OUTAGES_REF).child(sectorId);
    }
    
    // Report power outage for a sector
    public static void reportPowerOutage(String sectorId, String reportedBy, String estimatedRestoration) {
        PowerOutage outage = new PowerOutage(
            "power_cut", 
            System.currentTimeMillis(),
            reportedBy,
            estimatedRestoration
        );
        
        getSectorReference(sectorId).setValue(outage);
    }
    
    // Restore power for a sector
    public static void restorePower(String sectorId, String reportedBy) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "normal");
        updates.put("lastUpdated", System.currentTimeMillis());
        updates.put("reportedBy", reportedBy);
        updates.put("estimatedRestoration", "");
        
        getSectorReference(sectorId).updateChildren(updates);
    }
    
    // Subscribe to a sector's notifications
    public static void subscribeToSector(String sectorId) {
        FirebaseMessaging.getInstance().subscribeToTopic(sectorId);
    }
    
    // Unsubscribe from a sector's notifications
    public static void unsubscribeFromSector(String sectorId) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(sectorId);
    }
}