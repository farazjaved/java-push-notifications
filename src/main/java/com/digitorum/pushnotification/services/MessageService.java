package com.digitorum.pushnotification.services;

import com.digitorum.pushnotification.utils.FirebaseMessageUtil;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final FirebaseMessageUtil firebaseMessageUtil;

    public MessageService(FirebaseMessageUtil firebaseMessageUtil) {
        this.firebaseMessageUtil = firebaseMessageUtil;
    }

    public void sendMessage() {

        try {
            firebaseMessageUtil.sendCommonMessage();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }


}
