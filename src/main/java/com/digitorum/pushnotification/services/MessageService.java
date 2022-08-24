package com.digitorum.pushnotification.services;

import com.digitorum.pushnotification.models.Notification;
import com.digitorum.pushnotification.utils.FirebaseMessageUtil;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final FirebaseMessageUtil firebaseMessageUtil;

    public MessageService(FirebaseMessageUtil firebaseMessageUtil) {
        this.firebaseMessageUtil = firebaseMessageUtil;
    }

    public void sendMessageViaHttp() {

        Notification notification = new Notification();
        notification.setTitle("New Order");
        notification.setBody("A new order received of worth 50$");
        notification.setTopic("new-orders");

        try {
            firebaseMessageUtil.sendMessageViaHttp(notification);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void sendMessageViaSDK() {

        Notification notification = new Notification();
        notification.setTitle("New Order");
        notification.setBody("A new order received of worth 50$");
        notification.setTopic("new-orders");

        try {
            firebaseMessageUtil.sendMessageViaSDK(notification);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }


}
