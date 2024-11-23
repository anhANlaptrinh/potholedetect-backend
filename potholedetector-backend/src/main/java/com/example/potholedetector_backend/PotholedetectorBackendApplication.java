package com.example.potholedetector_backend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class PotholedetectorBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(PotholedetectorBackendApplication.class, args);
		initializeFirebase();
	}

	private static void initializeFirebase() {
		try {
			// Đọc file từ resources
			ClassLoader classLoader = PotholedetectorBackendApplication.class.getClassLoader();
			InputStream serviceAccount = classLoader.getResourceAsStream("google-services.json");

			if (serviceAccount == null) {
				throw new IOException("File google-services.json không tìm thấy trong resources.");
			}

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();
			FirebaseApp.initializeApp(options);
			System.out.println("Firebase Initialized");
		} catch (IOException e) {
			System.err.println("Failed to initialize Firebase: " + e.getMessage());
		}
	}

}
