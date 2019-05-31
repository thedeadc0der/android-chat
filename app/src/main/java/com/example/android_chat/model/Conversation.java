package com.example.android_chat.model;

public class Conversation {
    private int id;
    private String theme;
    private Boolean active;
    private String lastMessage;
    
    public Conversation(int id, String theme, Boolean active, String lastMessage){
        this.id = id;
        this.theme = theme;
        this.active = active;
        this.lastMessage = lastMessage;
    }
    
    public int getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public Boolean isActive() {
        return active;
    }
    
    public String getLastMessage(){
        return lastMessage;
    }
}
