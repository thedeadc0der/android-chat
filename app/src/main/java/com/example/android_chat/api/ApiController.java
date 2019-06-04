package com.example.android_chat.api;

import com.example.android_chat.model.Color;
import com.example.android_chat.model.Conversation;
import com.example.android_chat.model.Message;
import com.example.android_chat.model.User;

import java.util.List;

/**
 * Cette interface permet d'accéder à la source de données, que ce soit une API ou une autre source.
 */
public interface ApiController {
	/**
	 * Cette interface doit être implémentée pour recevoir la réponse à une requête.
	 * @param <T> Le type d'objet retourné.
	 */
	interface Callback<T> {
		/** Appelé quand la réponse est valide */
		void onResponse(T obj);
		
		/** Appelé si une erreur se produit */
		void onError(Error err);
	}
	
	/**
	 * Récupère l'utilisateur actuellement connecté.
	 * @return L'utilisateur, ou null si personne n'est connecté.
	 */
	User getCurrentUser();
	
	/**
	 * Connecte un utilisateur par login et mot de passe
	 * @param login L'identifiant de l'utilisateur.
	 * @param pass Mot de passe de l'utilisateur, en clair.
	 * @param cb Callback.
	 */
	void login(String login, String pass, Callback<Void> cb);
	
	/**
	 * Déconnecte l'utilisateur actuellement connecté.
	 * @param cb Callback.
	 */
	void logout(Callback<Void> cb);
	
	/**
	 * Inscrit un utilisateur.
	 * @param pseudo Le pseudo désiré.
	 * @param pass Le mot de passe du compte.
	 * @param cb Callback.
	 */
	void signup(String pseudo, String pass, Callback<Void> cb);
	
	/**
	 * Récupère la liste des conversations.
	 * @param cb Callback.
	 */
	void listConversations(Callback<List<Conversation>> cb);
	
	/**
	 * Créée une conversation.
	 * @param theme Thème de la conversation.
	 * @param cb Calback.
	 */
	void createConversation(String theme, Callback<Conversation> cb);
	
	/**
	 * Supprime une conversation.
	 * @param conversation La conversation à supprimer.
	 * @param cb Callback.
	 */
	void deleteConversation(Conversation conversation, Callback<Void> cb);
	
	/**
	 * Récupère les messages d'une conversation.
	 * @param conversation La conversation dont on veut les messages.
	 * @param cb Callback.
	 */
	void listMessages(Conversation conversation, Callback<List<Message>> cb);
	
	/**
	 * Récupère les messages d'une conversation après un message donné.
	 * @param conversation La conversation dont on veut les messages.
	 * @param lastMessage Seuls les messages postés après celui ci seront retournés.
	 * @param cb Callback.
	 */
	void listMessagesFrom(Conversation conversation, Message lastMessage, Callback<List<Message>> cb);
	
	/**
	 * Envoie un message.
	 * @param conversation La conversation où poster le message.
	 * @param msg Le message à envoyer.
	 * @param cb Callback.
	 */
	void sendMessage(Conversation conversation, String msg, Callback<Message> cb);
	
	/**
	 * Supprime un message.
	 * @param message Le message à supprimer.
	 * @param cb Callback.
	 */
	void deleteMessage(Message message, Callback<Void> cb);
	
	/**
	 * Modifie les infos d'un compte.
	 * @param login Le nouveau login.
	 * @param color La nouvelle couleur.
	 * @param cb Callback.
	 */
	void updateAccountInfo(String login, Color color, Callback<Void> cb);
	
	/**
	 * Supprimer un compte utilisateur.
	 * @param user L'utilisateur à supprimer.
	 * @param cb Callback.
	 */
	void deleteUser(User user, Callback<Void> cb);
	
	/**
	 * Récupère les conversations dans lesquelles un utilisateur a participé.
	 * @param user L'utilisateur participant.
	 * @param cb Callback.
	 */
	void getUserConversations(User user, Callback<List<Conversation>> cb);
}
