package com.example.android_chat.model;

/**
 * Représente une conversation.
 */
public class Conversation {
    private int id;
    private String theme;
    private Boolean active;
    private String lastMessage;
    
    /**
     * Constructeur par données.
     * @param id ID de la conversation dans l'API.
     * @param theme Thème de la conversation.
     * @param active Indique si la conversation est active.
     * @param lastMessage Le texte du dernier message envoyé.
     */
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
