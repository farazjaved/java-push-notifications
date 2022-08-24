package com.digitorum.pushnotification.utils;

import com.digitorum.pushnotification.models.Notification;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

@Component
public class FirebaseMessageUtil {

    @Value("${application.firebase.projectId}")
    private String PROJECT_ID;
    @Value("${application.firebase.serviceKeyFilePath}")
    private String firebaseServiceKeyFilePath;
    private final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private final String[] SCOPES = { MESSAGING_SCOPE };

    public final String MESSAGE_KEY = "message";

    private String getFCMSendEndpoint(){
        return "/v1/projects/" + PROJECT_ID + "/messages:send";
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(new ClassPathResource(firebaseServiceKeyFilePath).getFile()))
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private HttpURLConnection getConnection() throws IOException {
        String baseUrl = "https://fcm.googleapis.com";
        URL url = new URL(baseUrl + getFCMSendEndpoint());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
    }

    private void sendMessage(JsonObject fcmMessage) throws IOException {
        HttpURLConnection connection = getConnection();
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
        writer.write(fcmMessage.toString());
        writer.flush();
        writer.close();

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            String response = inputStreamToString(connection.getInputStream());
            System.out.println("Message sent to Firebase for delivery, response:");
            System.out.println(response);
        } else {
            System.out.println("Unable to send message to Firebase:");
            String response = inputStreamToString(connection.getErrorStream());
            System.out.println(response);
        }
    }

    private JsonObject buildNotificationMessage(Notification notification) {
        JsonObject jNotification = new JsonObject();
        jNotification.addProperty("title", notification.getTitle());
        jNotification.addProperty("body", notification.getBody());

        JsonObject jMessage = new JsonObject();
        jMessage.add("notification", jNotification);
        jMessage.addProperty("topic", notification.getTopic());

        JsonObject jFcm = new JsonObject();
        jFcm.add(MESSAGE_KEY, jMessage);

        return jFcm;
    }

    private static String inputStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    private void prettyPrint(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(jsonObject) + "\n");
    }

    public void sendMessageViaHttp(Notification notification) throws IOException {
        JsonObject notificationMessage = buildNotificationMessage(notification);
        System.out.println("FCM request body for message using common notification object:");
        prettyPrint(notificationMessage);
        sendMessage(notificationMessage);
    }

}