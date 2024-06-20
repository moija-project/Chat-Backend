package com.example.web_chatroom.controller;

import com.example.web_chatroom.DTO.*;
import com.example.web_chatroom.entity.ChatDTO;
import com.example.web_chatroom.global.BaseException;
import com.example.web_chatroom.global.BaseResponse;
import com.example.web_chatroom.global.BaseResponseStatus;
import com.example.web_chatroom.service.MessageService;
import com.example.web_chatroom.service.UserService;
import lombok.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private StompRabbitController rabbitController;

    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @PostMapping("/box")
    public BaseResponse<List> myChatRoom(@RequestBody String userId) {

        return new BaseResponse<List>(messageService.myChatRoom(userId ) );
    }

    //앞으로 exchange에 사용될 아이디를 뱉어준다. 참고로 시큐리티가 연결되지 않아서 매우 취약하다.
    @PostMapping(value = "/create/one-to-one",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<Map> newChatRoom(@RequestPart(name = "userId") String userId, @RequestPart(name = "chat") ChatCreateDTO chatCreateDTO ) {
        Map<String,String> response = messageService.newChatRoom(userId,chatCreateDTO);
        // 최초의 메시지를 보내놓는
        rabbitController.send(ChatDTO.builder()
                        .type(Type.TALK)//edit
                        .memberId(userId)
                        .nickname(chatCreateDTO.nickname())
                        .regDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                        .message("<"+chatCreateDTO.postTitle()+">의 모임장 님이 "+chatCreateDTO.nickname()+"님의 채팅 요청을 수락했어요!")
                .build(), response.get("chatRoomId"));
        return new BaseResponse<>(response);
    }

    // 이전에 내가 선택한 그 대화방에 이전에 있었던 대화를 로드해준다.(이때 소켓 연결 후 대화는 치지 않음)
    // 근데 페이지네이션 될때는 어떡하지.. 아마 프론트에서...소켓으로 받은 메시지 + 이전 리스트로 받은 메시지 잘 분배해서 처리해줘야할듯...
    @PostMapping(value="/list",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<Page> getPreviousChat(@RequestPart(name = "chat") ChatListDTO chatListDTO,@RequestPart(name = "userId") String userId) throws BaseException {
        //위임받은 인증
        if(!userService.isRoomOwner(userId,chatListDTO.chatRoomId()))
            throw new BaseException(BaseResponseStatus.NOT_PRIVILEGE);
        Pageable pageable = PageRequest.of(chatListDTO.page_number(), chatListDTO.page_size());
        return new BaseResponse<Page>(messageService.getPreviousChat(chatListDTO.chatRoomId(),pageable));
    }

}