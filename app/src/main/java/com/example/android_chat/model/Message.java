package com.example.android_chat.model;

public class Message {
	private int id;
	private User author;
	private String content;
	
	public Message(int id, User author, String content){
		this.id = id;
		this.author = author;
		this.content = content;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public User getAuthor(){
		return author;
	}
	
	public String getContent(){
		return content;
	}
	
	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Message message = (Message) o;
		
		return id == message.id;
	}
	
	@Override
	public int hashCode(){
		return id;
	}
}
