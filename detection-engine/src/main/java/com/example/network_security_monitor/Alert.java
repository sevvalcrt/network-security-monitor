package com.example.network_security_monitor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String timestamp;
    @Column(name = "source_ip")
private String sourceIp;
    @Column(name = "alert_type")
    private String alertType;
    private String details;

    public Long getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

public String getAlertType() {
    return alertType;
}

public String getSourceIp() {
    return sourceIp;
}

    public String getDetails() {
        return details;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}