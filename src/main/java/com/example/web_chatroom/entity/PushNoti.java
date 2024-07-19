package com.example.web_chatroom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

@Data@Document(collection = "pushnoti")@Builder@NoArgsConstructor@AllArgsConstructor
public class PushNoti {
    @Id
    String pushId;
    String message;
    @Field("non_read")
    Boolean nonRead;
    @Field("push_type")
    Integer pushType;
    String link;
    @Field("pub_date")
    ZonedDateTime pubDate;

}
