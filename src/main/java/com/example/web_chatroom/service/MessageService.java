package com.example.web_chatroom.service;

import com.example.web_chatroom.DTO.ChatCreateDTO;
import com.example.web_chatroom.DTO.ChatDTO;
import com.example.web_chatroom.DTO.ChatRoomDTO;
import com.example.web_chatroom.entity.ChatMember;
import com.example.web_chatroom.entity.ChatRoom;
import com.example.web_chatroom.repository.ChatMemberRepository;
import com.example.web_chatroom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service@Slf4j@RequiredArgsConstructor
public class MessageService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatMemberRepository chatMemberRepository;
    @Autowired
    private MongoService mongoService;

    public List<ChatRoomDTO> myChatRoom(String userId) {
        //읽지않은 채팅 개수 알아오기
        //마지막 채팅 불러오기
        List<ChatRoom> chatRooms = chatMemberRepository.findAllByUserId(userId).stream().map(chatMember -> chatMember.getChatRoom()).collect(Collectors.toList());
        if(chatRooms.isEmpty()) {
            return List.of();
        }
        return chatRooms.stream().map(chatRoom -> {
            String chatRoomId = chatRoom.getChatRoomId();
            return new ChatRoomDTO(
                    (String)getLastChat(chatRoomId).get("message"),
                    (LocalDateTime) getLastChat(chatRoomId).get("receivedTime"),
                    getNonRead(chatRoom.getChatRoomId()),
                    chatRoom
            );
        }).collect(Collectors.toList());
    }

    private int getNonRead(String chatRoomId) {
        // "type"이 "READ"인 문서의 "Regdate" 값을 가져옵니다.
        Query readQuery = new Query(Criteria.where("type").is("READ"));
        readQuery.with(Sort.by(Sort.Direction.DESC, "regDate"));
        readQuery.fields().include("regDate");
        readQuery.limit(1);

        // "type"이 "READ"인 문서의 "Regdate" 값을 가져옵니다.
        ChatDTO chat = mongoService.findOne(readQuery, ChatDTO.class,"message-"+chatRoomId);
        if (chat == null) {
            return countTalk();
        }

        // "READ" 이후의 "Regdate"를 가져옵니다.
        LocalDateTime readRegdate = chat.getRegDate();

        countTalk(readRegdate);

        return countTalk(readRegdate);
    }
    private int countTalk() {
        // "type"이 "TALK"이고 "Regdate"가 "READ" 이후인 문서의 개수를 가져옵니다.
        Query talkCountQuery = new Query(Criteria.where("type").is("TALK"));
        int talkCount = (int) mongoService.count(talkCountQuery, ChatDTO.class);
        if(talkCount > 99) {
            return 100;
        }
        return talkCount;
    }

    private int countTalk(LocalDateTime readRegdate) {
        // "type"이 "TALK"이고 "Regdate"가 "READ" 이후인 문서의 개수를 가져옵니다.
        Query talkCountQuery = new Query(Criteria.where("type").is("TALK").and("Regdate").gt(readRegdate));
        int talkCount = (int) mongoService.count(talkCountQuery, ChatDTO.class);
        if(talkCount > 99) {
            return 100;
        }
        return talkCount;
    }

    private Map<String,Object> getLastChat(String chatRoomId) {
        Query readQuery = new Query(Criteria.where("type").is("TALK"));
        readQuery.with(Sort.by(Sort.Direction.DESC, "regDate"));
        //readQuery.fields().include("regDate");
        readQuery.limit(1);
        ChatDTO chat = mongoService.findOne(readQuery, ChatDTO.class,"message-"+chatRoomId);
        if(chat == null) {
            return Map.of("message","새로운 대화를 시작해보세요.");
        }

        return Map.of("message",chat.getMessage(),"receivedTime",chat.getRegDate());
    }

    public Map<String,String> newChatRoom(String userId, ChatCreateDTO chatCreateDTO) {
        String chatTitle = "["+chatCreateDTO.nickname() + "]님은 <" + chatCreateDTO.postTitle().substring(0,7) + "...> 에 들어가고 싶어요";

        // 채팅방 생성
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                        .chatName(chatTitle)
                        .recruitId(chatCreateDTO.postId())
                .build());
        String chatRoomId = chatRoom.getChatRoomId();
        // 방장을 멤버로 추가
        chatMemberRepository.save(
                ChatMember.builder()
                        .chatRoom(chatRoom)
                        .userId(userId)
                        .build()
        );
        // 요청자를 멤버로 추가
        chatMemberRepository.save(
                ChatMember.builder()
                        .chatRoom(chatRoom)
                        .userId(chatCreateDTO.userId())
                        .build()
        );


        return Map.of("chatRoomId",chatRoomId);
    }

    public void storeMessage(ChatDTO chat,String collectionName) {
        mongoService.storeMessage(chat,collectionName);
    }

    public Page getPreviousChat(String chatRoomId,Pageable pageable) {
        Query query = new Query()
                .with(pageable)
                .skip((long) pageable.getPageSize() * pageable.getPageNumber()) // offset
                .limit(pageable.getPageSize());
        query.with(Sort.by(Sort.Direction.DESC, "regDate"));
        List<ChatDTO> chats = mongoService.find(query, ChatDTO.class, "message-"+chatRoomId);
        Page<ChatDTO> chatPage = PageableExecutionUtils.getPage(
                chats,
                pageable,
                () -> mongoService.count(query.skip(-1).limit(-1), ChatDTO.class)
                // query.skip(-1).limit(-1)의 이유는 현재 쿼리가 페이징 하려고 하는 offset 까지만 보기에 이를 맨 처음부터 끝까지로 set 해줘 정확한 도큐먼트 개수를 구한다.
        );
        return chatPage;
    }
}
