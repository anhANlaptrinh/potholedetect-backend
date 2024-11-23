package com.example.potholedetector_backend.service;

import com.example.potholedetector_backend.model.Pothole;
import com.example.potholedetector_backend.repository.PotholeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PotholeService {

    @Autowired
    private PotholeRepository potholeRepository;

    // Lấy tất cả ổ gà
    public List<Pothole> getAllPotholes() {
        return potholeRepository.findAll();
    }

    // Thêm ổ gà mới
    public Pothole addPothole(Pothole pothole) {
        return potholeRepository.save(pothole);
    }
}

