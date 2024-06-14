package com.example.web_chatroom.repository;

import com.example.web_chatroom.entity.PushAlarm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushAlarmRepository extends MongoRepository<PushAlarm,String> {
}
