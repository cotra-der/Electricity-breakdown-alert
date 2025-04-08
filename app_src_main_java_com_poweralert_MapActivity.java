package com.poweralert;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poweralert.models.Sector;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    private GoogleMap mMap;
    private Map<String, Sector> sectors = new HashMap<>();
    private Map<String, String> sectorStatus = new HashMap<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        // Initialize sector data
        initializeSectors();
        
        // Start monitoring sector status
        monitorSectorStatus();
    }
    
    private void initializeSectors() {
        // This would typically come from a database or API
        // For this example, we'll hardcode a few sectors with dummy coordinates
        
        // Sector 1
        sectors.put("sector_1", new Sector("sector_1", "Sector 1", 
                new LatLng(28.5456, 77.1907),
                new LatLng[] {
                        new LatLng(28.5440, 77.1880),
                        new LatLng(28.5470, 77.1880),
                        new LatLng(28.5470, 77.1930),
                        new LatLng(28.5440, 77.1930)
                }));
        
        // Sector 12
        sectors.put("sector_12", new Sector("sector_12", "Sector 12", 
                new LatLng(28.5556, 77.2007),
                new LatLng[] {
                        new LatLng(28.5540, 77.1980),
                        new LatLng(28.5570, 77.1980),
                        new LatLng(28.5570, 77.2030),
                        new LatLng(28.5540, 77.2030)
                }));
        
        // Add more sectors as needed
    }
    
    private void monitorSectorStatus() {
        FirebaseDatabase.getInstance().getReference("power_outages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot sectorSnapshot : dataSnapshot.getChildren()) {
                            String sectorId = sectorSnapshot.getKey();
                            String status = sectorSnapshot.child("status").getValue(String.class);
                            sectorStatus.put(sectorId, status);
                        }
                        
                        // Update map if it's ready
                        if (mMap != null) {
                            updateMap();
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Set initial camera position
        LatLng initialPosition = new LatLng(28.5500, 77.1900); // Centered on the sectors
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 14));
        
        // Draw initial map
        updateMap();
    }
    
    private void updateMap() {
        mMap.clear();
        
        for (Map.Entry<String, Sector> entry : sectors.entrySet()) {
            String sectorId = entry.getKey();
            Sector sector = entry.getValue();
            
            // Determine color based on power status
            int fillColor;
            String status = sectorStatus.get(sectorId);
            
            if (status != null && status.equals("power_cut")) {
                fillColor = Color.argb(128, 255, 0, 0); // Red for power outage
            } else {
                fillColor = Color.argb(128, 0, 255, 0); // Green for normal
            }
            
            // Add sector polygon
            PolygonOptions polygonOptions = new PolygonOptions()
                    .fillColor(fillColor)
                    .strokeColor(Color.BLACK)
                    .strokeWidth(2);
            
            for (LatLng point : sector.getBoundaryCoordinates()) {
                polygonOptions.add(point);
            }
            
            mMap.addPolygon(polygonOptions);
            
            // Add marker with sector name
            mMap.addMarker(new MarkerOptions()
                    .position(sector.getCenterCoordinate())
                    .title(sector.getName())
                    .snippet(status != null && status.equals("power_cut") ? 
                            "Power Outage Reported" : "Normal Power Supply"));
        }
    }
}