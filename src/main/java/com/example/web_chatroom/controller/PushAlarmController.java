package com.example.web_chatroom.controller;

import com.example.web_chatroom.DTO.AlarmDTO;
import com.example.web_chatroom.DTO.ChatDTO;
import com.example.web_chatroom.entity.PushAlarm;
import com.example.web_chatroom.service.AlarmService;
import com.example.web_chatroom.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Log4j2
public class PushAlarmController {
    @Autowired
    private final RabbitTemplate alarmRabbitTemplate;
    @Autowired
    private RabbitMQService rabbitMQService;
    private final static String ALARM_EXCHANGE_NAME = "alarm.exchange";
    @Autowired
    private AlarmService alarmService;

    // 이거는 postService나 채팅이나 관련된 api에서 호출해야한다.
    @MessageMapping("alarm.publish.{userId}")
    public void pubAlarm(@Payload AlarmDTO alarmDTO, @PathVariable String userId) {
        /**
         * type 0 : 받은 채팅
         * type 1 : 가입 요청 승낙됨
         * type 2 : 내 글이 스크랩됨
         * type 3 : 내 글에 요청이 생김
         *
         * **/
        PushAlarm pushAlarm = PushAlarm.builder()
                .pushType(alarmDTO.pushType())
                .message(alarmDTO.message())
                .nonRead(true)
                .pubDate(LocalDateTime.now())
                .build();
        // exchange는 alarm.exchange // 라우팅 키는 user.user123
        alarmRabbitTemplate.convertAndSend(ALARM_EXCHANGE_NAME,"user."+userId,pushAlarm);
        alarmService.storePersonalDB(pushAlarm);
    }

    @RabbitListener(queues = "alarm.queue")
    public void receive(PushAlarm alarm){

        System.out.println("received : " + alarm.getMessage() + " | type : "+ alarm.getPushType());
    }
}
