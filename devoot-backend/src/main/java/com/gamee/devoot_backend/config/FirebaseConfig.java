package com.gamee.devoot_backend.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
public class FirebaseConfig {

	@Bean
	public FirebaseAuth firebaseAuth() throws IOException {
		FileInputStream serviceAccount =
			new FileInputStream("src/main/resources/firebase-adminsdk.json");

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build();

		FirebaseApp.initializeApp(options);
		return FirebaseAuth.getInstance();
	}
}
