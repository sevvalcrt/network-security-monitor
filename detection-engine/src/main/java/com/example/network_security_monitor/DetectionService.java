package com.example.network_security_monitor;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DetectionService {

    private final ConcurrentHashMap<String, Set<Integer>> connectionTracker = new ConcurrentHashMap<>();
    private final Set<String> alertedIps = ConcurrentHashMap.newKeySet();
    private static final int PORT_SCAN_THRESHOLD = 10;

    private final AlertRepository alertRepository;

    public DetectionService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public void processEvent(String sourceIp, int destPort) {
        connectionTracker.computeIfAbsent(sourceIp, key -> ConcurrentHashMap.newKeySet()).add(destPort);

        int uniquePorts = connectionTracker.get(sourceIp).size();

        if (uniquePorts > PORT_SCAN_THRESHOLD && !alertedIps.contains(sourceIp)) {
            alertedIps.add(sourceIp);

            Alert alert = new Alert();
            alert.setSourceIp(sourceIp);
            alert.setAlertType("PORT_SCAN");
            alert.setDetails("Unique ports: " + uniquePorts);
            alert.setTimestamp(java.time.LocalDateTime.now().toString());

            alertRepository.save(alert);
        }
    }
}