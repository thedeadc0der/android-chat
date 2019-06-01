package com.example.android_chat.model;

public class User {
	private int id;
	private String pseudo;
	private Color color;
	private boolean admin;
	
	public User(int id, String pseudo, Color color, boolean admin){
		this.id = id;
		this.pseudo = pseudo;
		this.color = color;
		this.admin = admin;
	}
	
	public User(int id, String pseudo, String color, boolean admin){
		this(id, pseudo, new Color(color), admin);
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
	
	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public boolean isAdmin(){
		return admin;
	}
	
	public void setAdmin(boolean admin){
		this.admin = admin;
	}
	
	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		User user = (User) o;
		
		return id == user.id;
	}
	
	@Override
	public int hashCode(){
		return id;
	}
}
