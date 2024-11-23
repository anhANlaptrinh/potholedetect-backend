package com.example.potholedetector_backend.controller;

import com.example.potholedetector_backend.model.Pothole;
import com.example.potholedetector_backend.service.PotholeService;
import com.example.potholedetector_backend.service.PotholeWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pothole")
public class PotholeController {

    @Autowired
    private PotholeService potholeService;

    @Autowired
    private PotholeWebSocketHandler webSocketHandler;

    // API lấy danh sách ổ gà
    @GetMapping("/list")
    public ResponseEntity<List<Pothole>> getAllPotholes() {
        List<Pothole> potholes = potholeService.getAllPotholes();
        return ResponseEntity.ok(potholes);
    }

    // API thêm ổ gà
    @PostMapping("/add")
    public ResponseEntity<?> addPothole(@RequestBody Pothole pothole) {
        try {
            if (pothole.getSeverity() < 1 || pothole.getSeverity() > 3) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid severity value. Allowed values: 1, 2, 3.");
            }

            Pothole savedPothole = potholeService.addPothole(pothole);

            // Chuyển thông báo thành JSON
            String message = String.format("{\"latitude\":%f,\"longitude\":%f,\"severity\":%d}",
                    savedPothole.getLatitude(), savedPothole.getLongitude(), savedPothole.getSeverity());
            webSocketHandler.broadcast(message);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedPothole);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while adding pothole.");
        }
    }
}


