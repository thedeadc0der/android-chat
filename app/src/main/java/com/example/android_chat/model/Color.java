package com.example.android_chat.model;

/**
 * Représente une couleur.
 * Utilisé pour représenter la couleur des utilisateurs.
 */
public class Color {
	public int red=0, green=0, blue=0;
	
	/**
	 * Constructeur par défaut.
	 * Crée une couleur noire.
	 */
	public Color(){}
	
	/**
	 * Constructeur par données.
	 * @param red La quantité de rouge, de 0 à 255 inclus.
	 * @param green La quantité de vert, de 0 à 255 inclus.
	 * @param blue La quantité de bleu, de 0 à 255 inclus.
	 */
	public Color(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	/**
	 * Construit une couleur en lisant une chaîne de type #RRGGBB. Si la chaîne n'est pas valide,
	 * la couleur noire est retournée.
	 * @param str La couleur voulue.
	 */
	public Color(String str){
		if( str.length() == 7 && str.substring(0, 1).equals("#") ){
			red = Integer.parseInt(str.substring(1, 3), 16);
			green = Integer.parseInt(str.substring(3, 5), 16);
			blue = Integer.parseInt(str.substring(5, 7), 16);
		}
	}
	
	/**
	 * Retourne le code couleur AARRGGBB correspondant.
	 */
	public int toColorCode(){
		return blue | (green << 8) | (red << 16) | 0xFF000000;
	}
	
	static private String componentToString(int v){
		final String result = Integer.toHexString(v);
		return result.length() == 2 ? result : "0" + result;
	}
	
	/**
	 * Retourne la chaîne #RRGGBB correspondante
	 */
	public String toColorString(){
		return "#" + componentToString(red) + componentToString(green) + componentToString(blue);
	}
	
	/**
	 * Retourne une couleur aléatoire.
	 */
	public static Color makeRandom(){
		return new Color(
				(int) (Math.random() * 255),
				(int) (Math.random() * 255),
				(int) (Math.random() * 255));
	}
}
