package com.poweralert.models;

import com.google.android.gms.maps.model.LatLng;

public class Sector {
    private String id;
    private String name;
    private LatLng centerCoordinate;
    private LatLng[] boundaryCoordinates;
    
    public Sector(String id, String name, LatLng centerCoordinate, LatLng[] boundaryCoordinates) {
        this.id = id;
        this.name = name;
        this.centerCoordinate = centerCoordinate;
        this.boundaryCoordinates = boundaryCoordinates;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public LatLng getCenterCoordinate() {
        return centerCoordinate;
    }
    
    public LatLng[] getBoundaryCoordinates() {
        return boundaryCoordinates;
    }
}