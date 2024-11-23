package com.example.potholedetector_backend.controller;

import com.example.potholedetector_backend.model.User;
import com.example.potholedetector_backend.repository.UserRepository;
import com.example.potholedetector_backend.service.AuthService;
import com.example.potholedetector_backend.service.GoogleSignInService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService userService;

    @Autowired
    private UserRepository userRepository;

    private static final String CLIENT_ID = "363516771749-u4e3per891qhmfol44bbp8edtqqgfj95.apps.googleusercontent.com";

    @Autowired
    private GoogleSignInService googleSignInService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return userService.registerWithEmail(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        User user = userService.login(email, password);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Đăng nhập thất bại: Tài khoản hoặc mật khẩu không chính xác");
        }
    }

    @PostMapping("/loginWithGoogle")
    public ResponseEntity<User> loginWithGoogle(@RequestParam String idToken) {
        User user = googleSignInService.verifyTokenAndCreateUser(idToken);
        if (user != null) {
            System.out.println("User to be returned: " + user);  // Log to verify accountTypes
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<?> googleCallback(OAuth2AuthenticationToken authentication) {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String avatar = oidcUser.getPicture();

        User user = userService.loginWithGoogle(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/getUserByEmail")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại");
        }
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestParam String email,
            @RequestParam String newPassword) {

        boolean updated = userService.changePassword(email, newPassword);
        if (updated) {
            return ResponseEntity.ok("Password updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email");
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        boolean result = userService.sendVerificationCode(email);
        if (result) {
            return ResponseEntity.ok("Verification code sent to your email");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean result = userService.verifyCode(email, code);
        if (result) {
            return ResponseEntity.ok("Mã xác thực đúng, bạn có thể đặt lại mật khẩu");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã xác thực không đúng");
        }
    }


    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        boolean result = userService.resetPassword(email, newPassword);
        if (result) {
            return ResponseEntity.ok("Mật khẩu đã được cập nhật thành công");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể cập nhật mật khẩu");
        }
    }


    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String avatar = request.get("avatar");
        String username = request.get("username");

        if (email == null || avatar == null || username == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email, username, or avatar is missing");
        }

        boolean updated = userService.updateProfile(email, avatar, username);
        if (updated) {
            return ResponseEntity.ok("Profile updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
