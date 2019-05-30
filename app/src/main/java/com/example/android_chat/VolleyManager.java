package com.example.android_chat;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VolleyManager {

    /**
     * Attributs
     */
    private static final String TAG = "VolleyManager";
    private static VolleyManager instance = null;
    private static final String prefixURL = "http://10.0.2.2:5000/";
    public RequestQueue volleyRequestQueue;
    private int lastIdMessage = 0;

    private Timer timer;

    /**
     * Constructeur
     * @param context
     */
    private VolleyManager(Context context){
        //Création de la queue de requête
        volleyRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Récupérer une instance de VolleyManager utilisant le contexte de l'appellant
     * Utiliser à la première instantiation de la classe
     * @param context
     * @return
     */
    public static synchronized VolleyManager getInstance(Context context){
        if(instance == null){
            instance = new VolleyManager(context);
        }
        return instance;
    }

    /**
     * Utilisable une fois la classe instancié, permet de ne pas repasser le contexte à chaque fois
     * @return
     */
    public static synchronized VolleyManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(VolleyManager.class.getSimpleName() +
                    " n'est pas initialisé, appeler getInstance(context) avant");
        }
        return instance;
    }


    /***** Requêtes vers l'API ****/

    /**
     * Requête de connexion à l'application
     * @param login
     * @param password
     * @param listener
     */
    public void loginRequest(String login, String password, final customListener<JSONObject> listener)
    {

        String url = prefixURL + "login";

        //Définition des paramètres
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("login", login);
        jsonParams.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(TAG + ": ", "Login request response : " + response.toString());
                        //Si réponse
                        if(response != null)
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Json parse error", error.toString());
                        JSONObject jsonError = null;
                        try {
                            jsonError = new JSONObject(error.toString());
                            listener.getResult(jsonError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
     * Récupérer la liste de toutes les conversations
     * @param accessToken
     * @param listener
     */
    public void getConversationsRequest(final String accessToken, final customListener<JSONArray> listener)
    {

        String url = prefixURL + "conversations";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        Log.d(TAG + ": ", "getConversations response : " + response.toString());
                        //Si réponse
                        if(response != null)
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Json parse error", error.toString());
                        JSONArray jsonError = null;
                        try {
                            jsonError = new JSONArray(error.toString());
                            listener.getResult(jsonError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
     * Récupérer la liste des messages d'une conversation
     * @param accessToken
     * @param idConversation
     * @param lastIdMesssage
     * @param listener
     */
    public void getMessageFromConversationRequest(final String accessToken, final int idConversation,
                                                  final int lastIdMesssage, final customListener<JSONObject> listener)
    {

        String url = prefixURL + "refresh/"+idConversation+"/"+lastIdMesssage;
        int idMessageRecu = 0;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(TAG + ": ", "getMessageFromConversation response : " + response.toString());
                        //Si réponse
                        if(response != null)
                            listener.getResult(response);

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Json parse error", error.toString());
                        JSONObject jsonError = null;
                        try {
                            jsonError = new JSONObject(error.toString());
                            listener.getResult(jsonError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
     * Envoyer un message
     * @param message
     * @param auteur
     * @param listener
     */
   /* public void sendMessage(String message, String auteur, final customListener<JSONObject> listener)
    {

        String url = prefixURL + "login";

        //Définition des paramètres
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("login", login);
        jsonParams.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(TAG + ": ", "Login request response : " + response.toString());
                        //Si réponse
                        if(response != null)
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Json parse error", error.toString());
                        JSONObject jsonError = null;
                        try {
                            jsonError = new JSONObject(error.toString());
                            listener.getResult(jsonError);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
    }*/

    /************************************/

    /***** Gestion des requêtes périodiques ****/
    /**
     * Permet de récupérer les conversations périodiquement
     * @param periode
     * @param accessToken
     * @param idConv
     * @param listener
     */
    public void recupMessagesPeriodiquement(int periode, final String accessToken, final int idConv,
                                             final customListener<JSONObject> listener ) {

        TimerTask doAsynchronousTask;
        final Handler handler = new Handler();
        timer = new Timer();

        doAsynchronousTask = new TimerTask() {

            @Override
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        getMessageFromConversationRequest(accessToken,idConv,lastIdMessage,listener);
                    }
                });

            }

        };

        timer.schedule(doAsynchronousTask, 0, 1000 * periode);
    }

    /**
     * Changer le dernier id du message reçu
     * @param id
     */
    public void setLastIdMessage(int id){
        this.lastIdMessage = id;
    }

    /**
     * Arrête l'exécution des requêtes périodiques
     */
    public void stopRequetePeriodique(){
        if(timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    /************************************/

}
