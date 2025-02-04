package com.gamee.devoot_backend.common.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
public class FirebaseConfig {
	@Value("${firebase.config.path}")
	private String firebaseConfigPath;

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		// Check if FirebaseApp is already initialized
		List<FirebaseApp> apps = FirebaseApp.getApps();
		if (apps.isEmpty()) {
			// If not, initialize FirebaseApp
			FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

			return FirebaseApp.initializeApp(options);
		}
		// Return the existing FirebaseApp instance
		return FirebaseApp.getInstance();
	}

	@Bean
	public FirebaseAuth getFirebaseAuth() {
		try {
			return FirebaseAuth.getInstance(firebaseApp());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
