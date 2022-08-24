package com.digitorum.pushnotification.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.digitorum.pushnotification.services.MessageService;

@RestController
@RequestMapping("test/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/http/send")
    public void sendMessageViaHttp(){
        messageService.sendMessageViaHttp();
    }

    @GetMapping("/sdk/send")
    public void sendMessageViaSDK(){
        messageService.sendMessageViaSDK();
    }


}
