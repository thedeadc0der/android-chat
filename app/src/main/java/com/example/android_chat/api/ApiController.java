package com.example.android_chat.api;

import com.example.android_chat.model.Color;
import com.example.android_chat.model.Conversation;
import com.example.android_chat.model.Message;
import com.example.android_chat.model.User;

import java.util.List;

public interface ApiController {
	interface Callback<T> {
		void onResponse(T obj);
		void onError(Error err);
	}
	
	User getCurrentUser();
	
	void login(String login, String pass, Callback<Void> cb);
	void logout(Callback<Void> cb);
	void signup(String pseudo, String pass, Callback<Void> cb);
	void listConversations(Callback<List<Conversation>> cb);
	void createConversation(String theme, Callback<Conversation> cb);
	void deleteConversation(Conversation conversation, Callback<Void> cb);
	void listMessages(Conversation conversation, Callback<List<Message>> cb);
	void listMessagesFrom(Conversation conversation, Message lastMessage, Callback<List<Message>> cb);
	void sendMessage(Conversation conversation, String msg, Callback<Message> cb);
	void deleteMessage(Message message, Callback<Void> cb);
	void updateAccountInfo(String login, Color color, Callback<Void> cb);
	void deleteUser(User user, Callback<Void> cb);
}
