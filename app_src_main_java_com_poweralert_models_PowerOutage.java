package com.poweralert.models;

public class PowerOutage {
    private String status;
    private long lastUpdated;
    private String reportedBy;
    private String estimatedRestoration;
    
    // Required for Firebase
    public PowerOutage() {
    }
    
    public PowerOutage(String status, long lastUpdated, String reportedBy, String estimatedRestoration) {
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.reportedBy = reportedBy;
        this.estimatedRestoration = estimatedRestoration;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getReportedBy() {
        return reportedBy;
    }
    
    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }
    
    public String getEstimatedRestoration() {
        return estimatedRestoration;
    }
    
    public void setEstimatedRestoration(String estimatedRestoration) {
        this.estimatedRestoration = estimatedRestoration;
    }
}