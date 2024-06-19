package com.example.web_chatroom.DTO;

public enum Type {
    ENTER,READ,TALK,QUIT;
    @Override
    public String toString() {
        return this.name();
    }
}
