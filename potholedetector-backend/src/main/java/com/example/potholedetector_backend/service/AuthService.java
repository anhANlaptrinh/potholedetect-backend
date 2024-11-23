package com.example.potholedetector_backend.service;

import com.example.potholedetector_backend.model.User;
import com.example.potholedetector_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.potholedetector_backend.model.User;
import com.example.potholedetector_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private Map<String, String> verificationCodes = new HashMap<>();
    private Map<String, Long> verificationCodeTimestamps = new HashMap<>();

    public User register(User user) {
        return userRepository.save(user);
    }

    public User loginWithGoogle(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.getAccountTypes().add("google");
            return userRepository.save(user);
        } else {
            if (!user.getAccountTypes().contains("google")) {
                user.getAccountTypes().add("google");
                userRepository.save(user);
            }
            return user;
        }
    }

    public ResponseEntity<?> registerWithEmail(User newUser) {
        User user = userRepository.findByEmail(newUser.getEmail());
        if (user == null) {
            newUser.getAccountTypes().add("email");
            return ResponseEntity.ok(register(newUser));
        } else if (user.getAccountTypes().contains("email")) {
            return ResponseEntity.badRequest().body("Email đã được đăng ký.");
        } else if (user.getAccountTypes().contains("google")) {
            return ResponseEntity.badRequest().body("Email này chỉ có thể đăng nhập qua Google.");
        } else {
            return ResponseEntity.badRequest().body("Tài khoản đã được đăng ký.");
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }

        return false; // Trả về false nếu email không tồn tại
    }

    public boolean sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String code = generateVerificationCode();
            verificationCodes.put(email, code);
            verificationCodeTimestamps.put(email, System.currentTimeMillis()); // Lưu thời gian gửi mã
            return sendEmail(email, code);
        }
        return false;
    }

    public boolean verifyCode(String email, String code) {
        Long timestamp = verificationCodeTimestamps.get(email);
        if (timestamp != null) {
            long elapsed = System.currentTimeMillis() - timestamp;
            if (elapsed > 600000) { // 10 phút
                return false; // Mã đã hết hạn
            }
        }
        return code.equals(verificationCodes.get(email));
    }

    public boolean resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && verificationCodes.containsKey(email)) {
            user.setPassword(newPassword);  // Lưu mật khẩu trực tiếp không mã hóa
            userRepository.save(user);
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(999999)); // Tạo mã 6 chữ số
    }

    private boolean sendEmail(String email, String code) {
        final String username = "dohuynhan2408@gmail.com";
        final String password = "fsiy icmu bsrl tmdf";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("dohuynhan2408@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Password Reset Code");
            message.setText("Your verification code is: " + code);
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(String email, String avatarUrl, String username) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setAvatar(avatarUrl);
            user.setUsername(username);
            userRepository.save(user);
            return true;
        }
        return false;
    }

}
