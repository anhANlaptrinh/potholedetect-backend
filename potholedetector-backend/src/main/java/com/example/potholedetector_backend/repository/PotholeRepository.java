package com.example.potholedetector_backend.repository;

import com.example.potholedetector_backend.model.Pothole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PotholeRepository extends MongoRepository<Pothole, String> {
    // Các phương thức truy vấn tùy chỉnh nếu cần
}
