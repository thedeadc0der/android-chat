package com.example.android_chat.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_chat.model.Color;
import com.example.android_chat.model.Conversation;
import com.example.android_chat.model.Message;
import com.example.android_chat.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyApiController implements ApiController {

    /**
     * Attributs
     */
    private static final String prefixURL = "http://192.168.1.20:5000/";
    private String accessToken;
    public RequestQueue volleyRequestQueue;
    private User currentUser = null;

    /**
     * Constructeur de VolleyApiController
     * Il reçoit le contexte de l'application pour créer la queue de requête
     * @param context
     */
    public VolleyApiController(Context context){
        //Création de la queue de requête
        volleyRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Retourne l'utilisateur courant
     * @return
     */
    public User getCurrentUser(){return currentUser;}

    /**
     * Connexion à l'application
     * @param login
     * @param pass
     * @param cb
     */
    @Override
    public void login(String login, String pass, final Callback<Void> cb){
        final Map<String, String> params = new HashMap<>();
        params.put("login", login);
        params.put("password", pass);
        
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        
        makeJsonObjectRequest(Request.Method.POST, "login", params, headers, new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {
                    if( !response.getString("status").equals("success") ){
                        cb.onError(new Error("erreur serveur"));
                        return;
                    }
        
                    // Store the access token
                    accessToken = response.getString("token");
        
                    // Then reconstitute the user
                    JSONObject jsonUser = response.getJSONObject("userInfo");
                    currentUser = new User(
                            jsonUser.getInt("id"),
                            jsonUser.getString("pseudo"),
                            jsonUser.getString("couleur"),
                            jsonUser.getInt("admin") == 1
                    );
        
                    cb.onResponse(null);
                } catch (JSONException e) {
                    cb.onError(new Error("json invalide"));
                }
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("mauvais identifiants"));
            }
        });
    }

    /**
     * Créer un utilisateur
     * @param pseudo
     * @param pass
     * @param cb
     */
    @Override
    public void signup(String pseudo, String pass, final Callback<Void> cb){
        Map<String, String> params = new HashMap<>();
        params.put("login", pseudo);
        params.put("password", pass);
    
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        
        makeJsonObjectRequest(Request.Method.POST, "signup", params, headers, new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {
                    if( !response.getString("status").equals("success") ){
                        cb.onError(new Error("erreur serveur"));
                        return;
                    }
    
                    // XXX: This method used to store the access token, but it shouldn't have?
                    cb.onResponse(null);
                } catch (JSONException e) {
                    cb.onError(new Error("json invalide"));
                }
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("inscription impossible"));
            }
        });
    }

    /**
     * Déconnection de l'utilisateur
     * @param cb
     */
    @Override
    public void logout(final Callback<Void> cb){
        Map<String, String> headers = getStandardHeaders();
        
        makeJsonObjectRequest(Request.Method.DELETE, "logout", null, headers, new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj){
                cb.onResponse(null);
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("erreur de déconnexion"));
            }
        });
    }

    /**
     * Supprimer un utilisateur
     * @param user
     * @param cb
     */
    @Override
    public void deleteUser(User user, final Callback<Void> cb){
        makeJsonObjectRequest(Request.Method.DELETE, "user/" + user.getId(), null, getStandardHeaders(), new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj){
                cb.onResponse(null);
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("suppression impossible"));
            }
        });
    }
    
    /**
     * Renvoyer la liste de toutes les conversations
     * @param cb
     */
    @Override
    public void listConversations(final Callback<List<Conversation>> cb){
        Map<String, String> headers = getStandardHeaders();
        
        makeJsonArrayRequest(Request.Method.GET, "conversations", headers, new RequestCallback<JSONArray>() {
            @Override
            public void onResponse(JSONArray response){
                try {
                    List<Conversation> conversations = new ArrayList<Conversation>();
    
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject curr = response.getJSONObject(i);
        
                        conversations.add(new Conversation(
                                curr.getInt("id"),
                                curr.getString("theme"),
                                curr.getInt("active") == 1,
                                "" // TODO
                        ));
                    }
    
                    cb.onResponse(conversations);
                } catch(JSONException ex){
                    cb.onError(new Error("json invalide"));
                }
            }
            
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("Impossible de lister les conversations"));
            }
        });
    }
    
    /**
     * Créer une conversation dont le theme est passer en paramètre
     * @param theme
     * @param cb
     */
    @Override
    public void createConversation(final String theme, final Callback<Conversation> cb){
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("theme", theme);
    
        Map<String, String> headers = getStandardHeaders();
        
        makeJsonObjectRequest(Request.Method.POST, "conversation/new", jsonParams, headers, new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj){
                try {
                    cb.onResponse(new Conversation(
                            obj.getInt("idConversation"),
                            theme, true, "")
                    );
                } catch(JSONException ex){
                    cb.onError(new Error("json invalide"));
                }
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("Impossible de créer la conversation"));
            }
        });
    }
    
    /**
     * Renvoie tous les messages d'une conversation
     * @param conversation
     * @param cb
     */
    @Override
    public void listMessages(final Conversation conversation, final Callback<List<Message>> cb){
        Map<String, String> headers = getStandardHeaders();
        
        makeJsonArrayRequest(Request.Method.GET, "conversations/" + conversation.getId(), headers, new RequestCallback<JSONArray>() {
            @Override
            public void onResponse(JSONArray obj){
                try {
                    List<Message> messages = new ArrayList<>();
                    
                    for(int i=0; i < obj.length(); ++i){
                        JSONObject jsonMessage = obj.getJSONObject(i);
                        JSONObject jsonAuthor = jsonMessage.getJSONObject("auteur");
                        
                        final User author = new User(
                                jsonAuthor.getInt("id"),
                                jsonAuthor.getString("pseudo"),
                                jsonAuthor.getString("couleur"),
                                jsonAuthor.getInt("admin") == 1);
                        
                        messages.add(new Message(
                                jsonMessage.getInt("id"),
                                author,
                                jsonMessage.getString("contenu")
                        ));
                    }
                    cb.onResponse(messages);
                } catch(JSONException ex){
                    cb.onError(new Error("json invalide"));
                }
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("Impossible de récupérer les messages"));
            }
        });
    }

    /**
     * Renvoie les messages d'une conversation dont l'id est supérieur à l'id du dernier message
     * @param conversation
     * @param lastMessage
     * @param cb
     */
    @Override
    public void listMessagesFrom(Conversation conversation, Message lastMessage, final Callback<List<Message>> cb){
        final String url = "refresh/" + conversation.getId() + "/" + lastMessage.getId();
        Map<String, String> headers = getStandardHeaders();
        
        makeJsonObjectRequest(Request.Method.GET, url, null, headers, new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj){
                try {
                    JSONArray messages = obj.getJSONArray("messages");
                    List<Message> result = new ArrayList<>();
                    
                    for(int i=0; i < messages.length(); ++i){
                        JSONObject jsonMessage = messages.getJSONObject(i);
                        JSONObject jsonAuthor = jsonMessage.getJSONObject("auteur");
    
                        final User author = new User(
                                jsonAuthor.getInt("id"),
                                jsonAuthor.getString("pseudo"),
                                jsonAuthor.getString("couleur"),
                                jsonAuthor.getInt("admin") == 1);
    
                        result.add(new Message(
                                jsonMessage.getInt("id"),
                                author,
                                jsonMessage.getString("contenu")
                        ));
                    }
                    
                    cb.onResponse(result);
                } catch(JSONException ex){
                    cb.onError(new Error("json invalide"));
                }
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("impossible de récupérer les messages"));
            }
        });
    }
    
    /**
     * Ajoute un nouveau message à la conversation
     * @param conversation
     * @param msg
     * @param cb
     */
    @Override
    public void sendMessage(Conversation conversation, String msg, final Callback<Message> cb){
        final String url = "conversation/" + conversation.getId() + "/message";
        
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("content", msg);
    
        Map<String, String> headers = getStandardHeaders();
        
        makeJsonObjectRequest(Request.Method.POST, url, jsonParams, headers, new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj){
                try {
                    JSONObject jsonMessage = obj.getJSONObject("insertedMessage");
                    JSONObject jsonAuthor = jsonMessage.getJSONObject("auteur");
                    
                    final User author = new User(
                            jsonAuthor.getInt("id"),
                            jsonAuthor.getString("pseudo"),
                            jsonAuthor.getString("couleur"),
                            jsonAuthor.getInt("admin") == 1
                    );
                    
                    cb.onResponse(new Message(
                            jsonMessage.getInt("id"),
                            author,
                            jsonMessage.getString("contenu")
                    ));
                } catch(JSONException ex){
                    cb.onError(new Error("json invalide"));
                }
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("impossible d'envoyer le message"));
            }
        });
    }
    
    /**
     * Supprimer le message passé en paramètre
     * @param msg
     * @param cb
     */
    @Override
    public void deleteMessage(Message msg, final Callback<Void> cb){
        makeJsonObjectRequest(Request.Method.DELETE, "message/" + msg.getId(), null, getStandardHeaders(), new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj){
                cb.onResponse(null);
            }
    
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("impossible de supprimer le message"));
            }
        });
    }
    
    @Override
    public void deleteConversation(Conversation conv, final Callback<Void> cb){
        makeJsonObjectRequest(Request.Method.DELETE, "conversation/" + conv.getId(), null, getStandardHeaders(), new RequestCallback<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj){
                cb.onResponse(null);
            }
        
            @Override
            public void onError(Throwable exc){
                cb.onError(new Error("impossible de supprimer la conversation"));
            }
        });
    }
    
    @Override
    public void updateAccountInfo(String login, Color color, Callback<Void> cb){
    
    }
    
    private interface RequestCallback<T> {
        void onResponse(T obj);
        void onError(Throwable exc);
    }
    
    private void makeJsonObjectRequest(int method, String url, Map<String, String> params, final Map<String, String> headers, final RequestCallback<JSONObject> cb){
        volleyRequestQueue.add(new JsonObjectRequest(method, prefixURL + url,
                params == null ? new JSONObject() : new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        if (response == null){
                            cb.onError(new Error("aucune réponse"));
                            return;
                        }
            
                        cb.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        System.err.print(error);
                        System.err.println("data: " + new String(error.networkResponse.data));
                        cb.onError(new Error("erreur réseau"));
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                return headers == null ? super.getHeaders() : headers;
            }
        });
    }
    
    private void makeJsonArrayRequest(int method, String url, final Map<String, String> headers, final RequestCallback<JSONArray> cb){
        volleyRequestQueue.add(new JsonArrayRequest(method, prefixURL + url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        if (response == null){
                            cb.onError(new Error("aucune réponse"));
                            return;
                        }
                        
                        cb.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        System.err.print(error);
                        System.err.println("data: " + new String(error.networkResponse.data));
                        cb.onError(new Error("erreur réseau"));
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                return headers == null ? super.getHeaders() : headers;
            }
        });
    }
    
    private Map<String, String> getStandardHeaders(){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
