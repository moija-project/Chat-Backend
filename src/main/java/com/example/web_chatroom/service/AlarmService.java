package com.example.web_chatroom.service;

import com.example.web_chatroom.entity.PushAlarm;
import com.example.web_chatroom.repository.PushAlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service@RequiredArgsConstructor@Slf4j
public class AlarmService {
    @Autowired
    private MongoService mongoService;

    public void storePersonalDB(PushAlarm pushAlarm) {
        mongoService.storeMessage(pushAlarm,"pushalarm-"+pushAlarm.getUserId());
    }
}
