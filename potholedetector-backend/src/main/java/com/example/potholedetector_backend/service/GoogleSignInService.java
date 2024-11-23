package com.example.potholedetector_backend.service;

import com.example.potholedetector_backend.model.User;
import com.example.potholedetector_backend.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;

@Service
public class GoogleSignInService {
    private static final String CLIENT_ID = "363516771749-u4e3per891qhmfol44bbp8edtqqgfj95.apps.googleusercontent.com";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final UserRepository userRepository;

    @Autowired
    public GoogleSignInService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User verifyTokenAndCreateUser(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY)
                    .build(); // Bỏ qua kiểm tra CLIENT_ID

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                System.out.println("Token không hợp lệ.");
                return null;
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            // Kiểm tra chỉ cần email không null
            String email = payload.getEmail();
            if (email == null) {
                System.out.println("Email không tồn tại trong token.");
                return null;
            }
            String givenName = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");
            String name = givenName + " " + familyName;
            String avatar = (String) payload.get("picture");

            System.out.println("Xác thực thành công! Email: " + email);

            // Kiểm tra nếu người dùng đã tồn tại
            User existingUser = userRepository.findByEmail(email);
            if (existingUser != null) {
                if (existingUser.getAvatar() == null || existingUser.getAvatar().isEmpty()) {
                    existingUser.setAvatar(avatar); // Chỉ cập nhật avatar nếu còn trống
                }
                if (existingUser.getUsername() == null || existingUser.getUsername().isEmpty()) {
                    existingUser.setUsername(name); // Chỉ cập nhật username nếu còn trống
                }
                existingUser.getAccountTypes().add("google");
                return userRepository.save(existingUser);
            } else {
                // Nếu chưa tồn tại, tạo người dùng mới
                User newUser = new User(name, email);
                newUser.setAvatar(avatar);
                newUser.getAccountTypes().add("google");
                return userRepository.save(newUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi xác thực token: " + e.getMessage());
            return null;
        }
    }
}