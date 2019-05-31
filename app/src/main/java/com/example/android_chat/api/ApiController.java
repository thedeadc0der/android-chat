package com.example.android_chat.api;

import com.example.android_chat.model.Conversation;
import com.example.android_chat.model.Message;
import com.example.android_chat.model.User;

import java.util.List;

public interface ApiController {
	public interface Callback<T> {
		void onResponse(T obj);
		void onError(Error err);
	}
	
	boolean isLoggedIn();
	void login(String login, String pass, Callback<User> cb);
	void signup(String pseudo, String pass, Callback<User> cb);
	void listConversations(Callback<List<Conversation>> cb);
	void createConversation(String theme, Callback<Conversation> cb);
	void listMessages(Conversation conversation, Callback<List<Message>> cb);
	void sendMessage(Conversation conversation, String msg, Callback<Message> cb);
}
