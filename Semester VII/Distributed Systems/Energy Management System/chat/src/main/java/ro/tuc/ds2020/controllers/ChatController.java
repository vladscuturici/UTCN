package ro.tuc.ds2020.controllers;

import ro.tuc.ds2020.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import ro.tuc.ds2020.dtos.*;
@Controller
@CrossOrigin
public class ChatController {

    @Autowired
    private SimpMessagingTemplate template;

    private final ConcurrentHashMap<String, List<Message>> messageStore = new ConcurrentHashMap<>();

    @MessageMapping("/add")
    public void handleMessage(@Payload Message message) {
        try {
            System.out.println(message);
            String chatKey = generateChatKey(message.getSenderId(), message.getReceiverId());
            messageStore.putIfAbsent(chatKey, new CopyOnWriteArrayList<>());

            message.setRead(false);
            messageStore.get(chatKey).add(message);

            template.convertAndSend("/message/" + message.getSenderId() + "/" + message.getReceiverId(), message);

            template.convertAndSend("/notification/" + message.getReceiverId(), message);
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
        }
    }

    @MessageMapping("/read")
    public void markMessagesAsRead(@Payload MessageReadRequest readRequest) {
        String chatKey = generateChatKey(readRequest.getSenderId(), readRequest.getReceiverId());

        List<Message> messages = messageStore.getOrDefault(chatKey, new ArrayList<>());
        for (Message message : messages) {
            if (!message.isRead()) {
                message.setRead(true);
                template.convertAndSend("/message/" + readRequest.getReceiverId() + "/" + readRequest.getSenderId(), message);
            }
        }
    }


    private String generateChatKey(String senderId, String receiverId) {
        return senderId + ":" + receiverId;
    }
}
