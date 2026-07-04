package com.example.network_security_monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping("/alerts")
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @GetMapping("/alerts/port-scan")
    public List<Alert> getPortScanAlerts() {
        return alertRepository.findByAlertType("PORT_SCAN");
    }
}