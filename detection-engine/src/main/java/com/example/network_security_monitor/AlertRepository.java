package com.example.network_security_monitor;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByAlertType(String alertType);
}