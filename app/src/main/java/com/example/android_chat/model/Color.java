package com.example.android_chat.model;

public class Color {
	public int red=0, green=0, blue=0;
	
	public Color(){}
	
	public Color(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Color(String str){
		if( str.length() == 7 && str.substring(0, 1).equals("#") ){
			red = Integer.parseInt(str.substring(1, 3), 16);
			green = Integer.parseInt(str.substring(3, 5), 16);
			blue = Integer.parseInt(str.substring(5, 7), 16);
		}
	}
	
	public int toColorCode(){
		return blue | (green << 8) | (red << 16) | 0xFF000000;
	}
	
	public String toColorString(){
		return "#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
	}
	
	public static Color makeRandom(){
		return new Color(
				(int) (Math.random() * 255),
				(int) (Math.random() * 255),
				(int) (Math.random() * 255));
	}
}
