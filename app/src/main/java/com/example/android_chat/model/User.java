package com.example.android_chat.model;

public class User {
	private int id;
	private String pseudo;
	private String color;
	private boolean admin;
	
	public User(int id, String pseudo, String color, boolean admin){
		this.id = id;
		this.pseudo = pseudo;
		this.color = color;
		this.admin = admin;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getPseudo(){
		return pseudo;
	}
	
	public void setPseudo(String pseudo){
		this.pseudo = pseudo;
	}
	
	public String getColor(){
		return color;
	}
	
	public void setColor(String color){
		this.color = color;
	}
	
	public boolean isAdmin(){
		return admin;
	}
	
	public void setAdmin(boolean admin){
		this.admin = admin;
	}
}