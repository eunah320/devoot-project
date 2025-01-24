package com.gamee.devoot_backend.config;

import java.io.FileInputStream;
import java.io.IOException;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	@PostConstruct
	public FirebaseApp firebaseApp() throws IOException {
		FileInputStream serviceAccount =
			new FileInputStream("src/main/resources/firebase-adminsdk.json");

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build();

		return FirebaseApp.initializeApp(options);
	}
}
