package com.example.android_chat.api;

import com.example.android_chat.model.Conversation;
import com.example.android_chat.model.Message;
import com.example.android_chat.model.User;

import java.util.ArrayList;
import java.util.List;

public class MockApiController implements ApiController {
	private List<User> users;
	private List<Conversation> conversations;
	private List<Message> messages;
	
	private User currentUser = null;
	
	public MockApiController(){
		populateData();
	}

	public boolean isLoggedIn(){
		return false;
	}

	private String makeRandomColor(){
		final int r = (int) (Math.random() * 255);
		final int g = (int) (Math.random() * 255);
		final int b = (int) (Math.random() * 255);
		return "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
	}
	
	private void populateData(){
		users = new ArrayList<>();
		users.add(new User(1, "jessy", makeRandomColor(), true));
		users.add(new User(2, "smoke", makeRandomColor(), false));
		users.add(new User(3, "ryder", makeRandomColor(), false));
		users.add(new User(4, "sweet", makeRandomColor(), false));
		
		conversations = new ArrayList<>();
		conversations.add(new Conversation(1, "Conversation 1", true, "Lorem ipsum dolor sit amet, consectetur"));
		conversations.add(new Conversation(2, "Conversation 2", false, "voluptate velit esse cillum dolore"));
		conversations.add(new Conversation(3, "Conversation 3", true, "Ok"));
		conversations.add(new Conversation(4, "Conversation 4", false, "super long text super long text super long text super long text super long text super long text super long text super long text super long text super long text super long text super long text super long text super long text"));
		conversations.add(new Conversation(5, "Conversation 5", false, "I'm too bored to come up with these anymore"));
		conversations.add(new Conversation(6, "Conversation 6", false, "nobody ignores the smoke"));
		conversations.add(new Conversation(7, "Conversation 7", false, "yup"));
		conversations.add(new Conversation(8, "Conversation 8", false, "it's og loc"));
		conversations.add(new Conversation(9, "Conversation 9", false, "I think Big Smoke is the greatest of all time"));
		conversations.add(new Conversation(10, "And another one", false, "at some point it'll bite the dust"));
		conversations.add(new Conversation(11, "And another one", false, "at some point it'll bite the dust"));
		conversations.add(new Conversation(12, "And another one", false, "at some point it'll bite the dust"));
		conversations.add(new Conversation(13, "And another one", false, "at some point it'll bite the dust"));
		conversations.add(new Conversation(14, "And another one", false, "at some point it'll bite the dust"));
		conversations.add(new Conversation(15, "And another one", false, "at some point it'll bite the dust"));
		conversations.add(new Conversation(16, "And another one", false, "at some point it'll bite the dust"));
		conversations.add(new Conversation(17, "And another one", false, "at some point it'll bite the dust"));
		
		messages = new ArrayList<>();
		messages.add(new Message(1, users.get(0), "Hello"));
		messages.add(new Message(2, users.get(1), "YOU PICKED THE WRONG HOUSE"));
		messages.add(new Message(3, users.get(2), "I'm a genius"));
		messages.add(new Message(4, users.get(3), "I'm told cheese has to be earned…"));
	}

	@Override
	public User getCurrentUser() {
		return null;
	}

	@Override
	public void login(String login, String pass, Callback<Void> cb) {

	}


	/*@Override
	public void login(String login, String pass, Callback<User> cb){
		assert !isLoggedIn();
		
		// Look for an user with this login
		for(User curr: users){
			// If we find them, make sure they have the correct password
			if( curr.getPseudo().equals(login) && pass.equals("smoke") ){
				currentUser = curr;
				cb.onResponse(curr);
				return;
			}
		}
		
		// If they don't, give out an error
		cb.onError(new Error("bad credentials"));
	}
	*/
	@Override
	public void signup(String pseudo, String pass, Callback<Void> cb){
		assert !isLoggedIn();
		
		// Make sure no other user has that name
		for(User curr: users){
			if( curr.getPseudo().equals(pseudo) )
				cb.onError(new Error("name already taken"));
		}
		
		// Create the user and add them
		users.add(new User(users.size(), pseudo, pass, false));
		//cb.onResponse(users.get(users.size() - 1));
	}
	
	@Override
	public void listConversations(Callback<List<Conversation>> cb){
		assert isLoggedIn();
		cb.onResponse(conversations);
	}
	
	@Override
	public void createConversation(String theme, Callback<Conversation> cb){
		assert isLoggedIn();
		for(Conversation curr: conversations){
			if( curr.getTheme().equals(theme) )
				throw new Error("theme already exists");
		}
		
		Conversation conv = new Conversation(conversations.size(), theme, true, "(conversation vide)");
		conversations.add(conv);
		cb.onResponse(conv);
	}
	
	@Override
	public void listMessages(Conversation conversation, Callback<List<Message>> cb){
		assert isLoggedIn();
		cb.onResponse(messages);
	}

	@Override
	public void listMessagesFromId(Conversation conversation, Message lastMessage, Callback<List<Message>> cb) {

	}

	@Override
	public void sendMessage(Conversation conversation, String msg, Callback<Message> cb){
		assert isLoggedIn();
		final Message message = new Message(messages.size(), currentUser, msg);
		messages.add(message);
		cb.onResponse(message);
	}
}
