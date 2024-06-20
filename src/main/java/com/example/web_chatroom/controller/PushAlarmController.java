package com.example.web_chatroom.controller;

import com.example.web_chatroom.DTO.AlarmDTO;
import com.example.web_chatroom.entity.PushAlarm;
import com.example.web_chatroom.service.AlarmService;
import com.example.web_chatroom.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;


@RestController
@RequiredArgsConstructor
@Log4j2
public class PushAlarmController {
    @Autowired@Qualifier("alarmRabbitTemplate")
    private final RabbitTemplate alarmRabbitTemplate;
    @Autowired
    private RabbitMQService rabbitMQService;
    private final static String ALARM_EXCHANGE_NAME = "alarm.exchange";
    private final static String ALARM_QUEUE_NAME = "alarm.queue";
    @Autowired
    private AlarmService alarmService;

    // 이거는 postService나 채팅이나 관련된 api에서 호출해야한다.
    @MessageMapping("alarm.publish.{userId}")
    public void pubAlarm(@Payload AlarmDTO alarmDTO, @DestinationVariable String userId) {
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
                .pubDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
        // exchange는 alarm.exchange // 라우팅 키는 user.user123
        alarmRabbitTemplate.convertAndSend("user."+userId,pushAlarm);
        System.out.println(userId);
        alarmService.storePersonalDB(pushAlarm,userId);
    }

    @RabbitListener(queues = ALARM_QUEUE_NAME)
    public void receive(PushAlarm alarm){

        System.out.println("received : " + alarm.getMessage() + " | type : "+ alarm.getPushType());
    }
}
