package com.example.web_chatroom.repository;

import com.example.web_chatroom.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom,String> {
}
