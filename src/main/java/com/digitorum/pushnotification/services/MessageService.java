package com.digitorum.pushnotification.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class MessageService {

    @Value("${application.firebase.projectId}")
    private String firebaseProjectId;

    @Value("${application.firebase.databaseUrl}")
    private String firebaseDatabaseUrl;

    @Value("${application.firebase.serviceKeyFilePath}")
    private String firebaseServiceKeyFilePath;

    private FirebaseApp initializeApp() throws IOException {

        FileInputStream serviceAccount =
                new FileInputStream(new ClassPathResource(firebaseServiceKeyFilePath).getFile());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(firebaseDatabaseUrl)
                .build();

        return FirebaseApp.initializeApp(options, firebaseProjectId);
    }

    public FirebaseApp getApp() throws IOException {
        try {
            return FirebaseApp.getInstance(firebaseProjectId);
        } catch (IllegalStateException e){
            return initializeApp();
        }
    }

    public void sendMessage(String token) {

        if (token == null) return;

        String title = "Test Title";
        String message = "This is a test notification";

        Message.Builder messageBuilder = Message.builder();

        messageBuilder
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(message).build()
                );

        try {
            FirebaseApp app = getApp();
            FirebaseMessaging.getInstance(app).send(messageBuilder.build());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }


}
