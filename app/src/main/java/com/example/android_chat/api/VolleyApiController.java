package com.example.android_chat.api;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_chat.VolleyManager;
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

    public VolleyApiController(Context context){
        //Création de la queue de requête
        volleyRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Utilisateur connecté à l'application
     * @return
     */
    @Override
    public boolean isLoggedIn() {
        return !accessToken.isEmpty();
    }

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
     * TODO implémenter la fonction quand la route existera
     * Créé un utilisateur
     * @param pseudo
     * @param pass
     * @param cb
     */
    @Override
    public void signup(String pseudo, String pass, Callback<User> cb) {

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
                                    Boolean active = ((String) obj.getString("active")).contentEquals("1");
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

    /** TODO implémenter la fonction quand route créée
     * Créer une conversation dont le theme est passer en paramètre
     * @param theme
     * @param cb
     */
    @Override
    public void createConversation(String theme, Callback<Conversation> cb) {

    }

    /** TODO finaliser l'instanciation d'un USER
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
                                   JSONObject msg = (JSONObject) messages.get(i);
                                   int id = msg.getInt("id");
                                   String contenu =  msg.getString("contenu");

                                   int idAutheur =  msg.getInt("idAuteur");

                                   User user = new User(idAutheur, "","",false);
                                   Message m = new Message(id,user,contenu);
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

    /** TODO finaliser l'instanciation d'un USER
     * Renvoie les messages d'une conversation dont l'id est supérieur à idLastMessage
     * @param conversation
     * @param idLastMessage
     * @param cb
     */
    @Override
    public void listMessagesFromId(Conversation conversation, int idLastMessage, final Callback<List<Message>> cb) {
        String url = prefixURL + "refresh/"+conversation.getId()+"/"+idLastMessage;

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
                                    JSONObject msg = (JSONObject) messages.get(i);
                                    int id = msg.getInt("id");
                                    String contenu =  msg.getString("contenu");

                                    int idAutheur =  msg.getInt("idAuteur");

                                    User user = new User(idAutheur, "","",false);
                                    Message m = new Message(id,user,contenu);
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
     * TODO implémenter la fonction quand la route sera créée
     * Ajoute un nouveau message à la conversation
     * @param conversation
     * @param msg
     * @param cb
     */
    @Override
    public void sendMessage(Conversation conversation, String msg, Callback<Message> cb) {

    }
}
