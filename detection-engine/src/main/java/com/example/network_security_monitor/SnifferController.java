package com.example.network_security_monitor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnifferController {

    private Process snifferProcess;

    @PostMapping("/api/sniffer/start")
    public String startSniffer() {
        if (snifferProcess != null && snifferProcess.isAlive()) {
            return "Already running";
        }
        try {
            ProcessBuilder pb = new ProcessBuilder("./sniffer");
            pb.directory(new java.io.File("/home/sevv/network-security-monitor/capture-engine"));
            pb.redirectOutput(new java.io.File("/home/sevv/network-security-monitor/capture-engine/live_output.log"));
            snifferProcess = pb.start();
            return "Started";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/api/sniffer/stop")
    public String stopSniffer() {
        if (snifferProcess != null && snifferProcess.isAlive()) {
            snifferProcess.destroy();
            return "Stopped";
        }
        return "Not running";
    }

    @PostMapping("/api/sniffer/status")
    public String status() {
        if (snifferProcess != null && snifferProcess.isAlive()) {
            return "running";
        }
        return "stopped";
    }
}
