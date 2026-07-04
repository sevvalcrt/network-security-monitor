package com.example.network_security_monitor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
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
}