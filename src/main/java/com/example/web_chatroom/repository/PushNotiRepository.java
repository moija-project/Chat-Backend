package com.example.web_chatroom.repository;

import com.example.web_chatroom.entity.PushNoti;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushNotiRepository extends MongoRepository<PushNoti,String> {
}
