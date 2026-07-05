package com.example.network_security_monitor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final DetectionService detectionService;

    public EventController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping("/events")
    public String receiveEvent(@RequestBody NetworkEvent event) {
        detectionService.processEvent(event.getSourceIp(), event.getDestPort());
        return "Event received";
    }
}