package com.example.web_chatroom.controller;


import com.example.web_chatroom.DTO.ChatDTO;
import com.example.web_chatroom.DTO.Type;
import com.example.web_chatroom.service.MessageService;
import com.example.web_chatroom.service.RabbitMQService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class StompRabbitController {


    private final RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQService rabbitMQService;
    @Autowired
    private MessageService messageService;

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
//    private final static String CHAT_QUEUE_NAME = "chat.queue";


    @MessageMapping("chat.enter.{chatRoomId}")
    public void enter(@Payload ChatDTO chat, @DestinationVariable String chatRoomId){
        
        chat.setMessage(chat.getNickname()+"님께서 입장하셨습니다.");
        chat.setRegDate(LocalDateTime.now());
        chat.setType(Type.ENTER);

        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat); // exchange
        messageService.storeMessage(chat,"message-"+chatRoomId);
    }

    @MessageMapping("chat.message.{chatRoomId}")
    public void send(@Payload ChatDTO chat, @DestinationVariable String chatRoomId){
        chat.setRegDate(LocalDateTime.now());
        chat.setType(Type.TALK);
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
        messageService.storeMessage(chat,"message-"+chatRoomId);
    }

    //채팅방에 접속할때마다 읽음처리를 하고 북마크를 끼워놓 듯이 전송
    @MessageMapping("chat.read.{chatRoomId}")
    public void read(@Payload ChatDTO chat, @DestinationVariable String chatRoomId) {
        chat.setRegDate(LocalDateTime.now());
        chat.setType(Type.READ);
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME,"room."+chatRoomId,chat);
        messageService.storeMessage(chat,"message-"+chatRoomId);
    }



    //receive()는 단순히 큐에 들어온 메세지를 소비만 한다. (현재는 디버그용도)
    @RabbitListener(queues = "chat.queue")
    public void receive(ChatDTO chat){

        System.out.println("received : " + chat.getMessage() + " | chatting room : "+ chat.getMemberId());
    }

    @RabbitListener(queues = "chat.queue")
    public void handleUnsubscribeEvent(String eventMessage) {
        // 구독 취소 이벤트를 처리하는 로직을 여기에 구현합니다.
        System.out.println("구독이 취소되었습니다: " + eventMessage);
    }
}