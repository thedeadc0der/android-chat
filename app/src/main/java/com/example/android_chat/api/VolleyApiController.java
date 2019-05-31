package com.example.android_chat.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
    private static final String prefixURL = "http://10.0.2.2:5000/";
    private String accessToken;
    public RequestQueue volleyRequestQueue;
    private User currentUser = null;

    public VolleyApiController(Context context){
        //Création de la queue de requête
        volleyRequestQueue = Volley.newRequestQueue(context);
    }

    public User getCurrentUser(){return currentUser;}

    /**
     * Connexion à l'application
     * @param login
     * @param pass
     * @param cb
     */
    @Override
    public void login(String login, String pass, final Callback<Void> cb) {
        String url = prefixURL + "login";

        //Définition des paramètres
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("login", login);
        jsonParams.put("password", pass);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(response != null){
                            try {
                                if(response.getString("status").equals("success")){
                                    accessToken = response.getString("token");
                                    //Save the current user
                                    JSONObject userInfos = response.getJSONObject("userInfo");
                                    int id = userInfos.getInt("id");
                                    String pseudo = userInfos.getString("pseudo");
                                    String color = userInfos.getString("couleur");
                                    boolean admin = ((String) userInfos.getString("admin")).contentEquals("1");
                                    currentUser = new User(id, pseudo, color, admin);

                                    cb.onResponse(null);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If they don't, give out an error
                        cb.onError(new Error("bad credentials"));
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };

        volleyRequestQueue.add(request);
    }

    /**
     * Créé un utilisateur
     * @param pseudo
     * @param pass
     * @param cb
     */
    @Override
    public void signup(String pseudo, String pass, final Callback<Void> cb) {
        String url = prefixURL + "signup";

        //Définition des paramètres
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("login", pseudo);
        jsonParams.put("password", pass);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(response != null){
                            try {
                                if(response.getString("status").equals("success")){
                                    accessToken = response.getString("token");

                                    cb.onResponse(null);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If they don't, give out an error
                        cb.onError(new Error("pseudo already used"));
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };

        volleyRequestQueue.add(request);
    }

    /**
     * Renvoyer la liste de toutes les conversations
     * @param cb
     */
    @Override
    public void listConversations(final Callback<List<Conversation>> cb) {
        String url = prefixURL + "conversations";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        if(response != null){
                            List<Conversation> conversations = new ArrayList<Conversation>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject(response.getString(i));
                                    //Create a conversation
                                    int id =Integer.parseInt(obj.getString("id"));
                                    String theme = obj.getString("theme");
                                    boolean active = ((String) obj.getString("active")).contentEquals("1");
                                    Conversation c = new Conversation(id,theme,active,"");
                                    conversations.add(c);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            cb.onResponse(conversations);
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If they don't, give out an error
                        cb.onError(new Error("error while retrieving conversations"));
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        volleyRequestQueue.add(request);
    }

    /**
     * Créer une conversation dont le theme est passer en paramètre
     * @param theme
     * @param cb
     */
    @Override
    public void createConversation(final String theme, final Callback<Conversation> cb) {
        String url = prefixURL + "conversation/new";

        //Définition des paramètres
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("theme", theme);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(response != null){
                            try {
                                int idConv = response.getInt("idConversation");

                                Conversation c = new Conversation(idConv, theme, true, "");
                                cb.onResponse(c);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If they don't, give out an error
                        cb.onError(new Error("error while creating conversation"));
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        volleyRequestQueue.add(request);
    }

    /**
     * Renvoie tous les messages d'une conversation
     * @param conversation
     * @param cb
     */
    @Override
    public void listMessages(final Conversation conversation, final Callback<List<Message>> cb) {
        String url = prefixURL + "conversations/"+conversation.getId();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                       if(response != null){
                           try {
                               JSONArray messages = null;
                               messages = response.getJSONArray("messages");
                               List<Message> listMessages = new ArrayList<Message>();
                               for(int i=0;i<messages.length();i++) {

                                   //Find messages infos
                                   JSONObject msg = (JSONObject) messages.get(i);
                                   int id = msg.getInt("id");
                                   String contenu =  msg.getString("contenu");

                                   //Find auteur infos
                                   JSONObject auteurInfos = response.getJSONObject("autheur");
                                   int idAuteur = auteurInfos.getInt("id");
                                   String pseudo = auteurInfos.getString("pseudo");
                                   String color = auteurInfos.getString("couleur");
                                   boolean admin = ((String) auteurInfos.getString("admin")).contentEquals("1");

                                   //Create user & message
                                   User user = new User(idAuteur, pseudo,color,admin);
                                   Message message = new Message(id,user,contenu);

                                   listMessages.add(message);
                               }

                               cb.onResponse(listMessages);

                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If they don't, give out an error
                        cb.onError(new Error("error while retrieving messsages"));
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        volleyRequestQueue.add(request);
    }

    /**
     * Renvoie les messages d'une conversation dont l'id est supérieur à l'id du dernier message
     * @param conversation
     * @param lastMessage
     * @param cb
     */
    @Override
    public void listMessagesFromId(Conversation conversation, Message lastMessage, final Callback<List<Message>> cb) {
        String url = prefixURL + "refresh/"+conversation.getId()+"/"+ lastMessage.getId();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(response != null){
                            try {
                                JSONArray messages = null;
                                messages = response.getJSONArray("messages");
                                List<Message> listMessages = new ArrayList<Message>();
                                for(int i=0;i<messages.length();i++) {

                                    //Find messages infos
                                    JSONObject msg = (JSONObject) messages.get(i);
                                    int id = msg.getInt("id");
                                    String contenu =  msg.getString("contenu");

                                    //Find auteur infos
                                    JSONObject auteurInfos = response.getJSONObject("autheur");
                                    int idAuteur = auteurInfos.getInt("id");
                                    String pseudo = auteurInfos.getString("pseudo");
                                    String color = auteurInfos.getString("couleur");
                                    boolean admin = ((String) auteurInfos.getString("admin")).contentEquals("1");

                                    //Create user & message
                                    User user = new User(idAuteur, pseudo,color,admin);
                                    Message message = new Message(id,user,contenu);

                                    listMessages.add(message);
                                }

                                cb.onResponse(listMessages);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If they don't, give out an error
                        cb.onError(new Error("error while retrieving messsages"));
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        volleyRequestQueue.add(request);
    }

    /**
     * TODO Récupérer l'id du message depuis l'API
     * Ajoute un nouveau message à la conversation
     * @param conversation
     * @param msg
     * @param cb
     */
    @Override
    public void sendMessage(Conversation conversation, final String msg, final Callback<Message> cb) {
        String url = prefixURL + "conversation/"+conversation.getId()+"/message";

        //Définition des paramètres
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("idAuteur", String.valueOf(currentUser.getId()));
        jsonParams.put("content", msg);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(response != null){
                            Message message = new Message(0, currentUser,msg);
                            cb.onResponse(message);
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If they don't, give out an error
                        cb.onError(new Error("error while posting the message"));
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        volleyRequestQueue.add(request);
    }
}
