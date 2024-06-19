package com.example.web_chatroom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Data@Document(collection = "pushalarm")@Builder@NoArgsConstructor@AllArgsConstructor
public class PushAlarm {
    @Id@Field("push_id")
    String pushId;
    @Field("user_id")
    String userId;
    String message;
    @Field("non_read")
    Boolean nonRead;
    @Field("push_type")
    Integer pushType;
    @Field("pub_date")
    LocalDateTime pubDate;

}
