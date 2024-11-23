package com.example.potholedetector_backend.repository;

import com.example.potholedetector_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    User findByEmail(String email); // Thêm phương thức tìm kiếm theo email
}