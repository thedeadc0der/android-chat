package com.example.android_chat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowConvActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Attributs
     */
    private GlobalState gs;
    private int idConv;
    private int idLastMessage = 0;

    private LinearLayout msgLayout;
    private Button btnOK;
    private EditText edtMsg;

    /***** Gestion de l'état de l'activité ****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversation);
        gs = (GlobalState) getApplication();


        msgLayout = findViewById(R.id.conversation_svLayoutMessages);
        btnOK = findViewById(R.id.conversation_btnOK);
        btnOK.setOnClickListener(this);
        edtMsg = findViewById(R.id.conversation_edtMessage);

        //Récupération de l'id de la conversations sélectionnée
        Bundle bdl = getIntent().getExtras();
        idConv = bdl.getInt("idConversation");

        //VolleyManager.getInstance().setLastIdMessage(0);
        //Récupération de tous les messages
        /*VolleyManager.getInstance().recupMessagesPeriodiquement(10, gs.getAccessToken(), idConv,
                new customListener<JSONObject>() {
                    @Override
                    public void getResult(JSONObject object) {
                        if(object != null)
                            traiteReponse(object);
                    }
        });*/
    }

    protected void onStop() {
        super.onStop();
        //VolleyManager.getInstance().stopRequetePeriodique();
    }

    /************************************/


    /***** Gestion des requêtes ****/

    /**
     * Analyse le JSON des messages
     * @param o
     */
    public void traiteReponse(JSONObject o) {
       /* {"connecte":true,
        "action":"getMessages",
        "feedback":"entrez action: logout, setPasse(passe),setPseudo(pseudo),
        setCouleur(couleur),getConversations,
        getMessages(idConv,[idLastMessage]),
        setMessage(idConv,contenu), ...",
        "messages":[{"id":"35",
                        "contenu":"Que pensez-vous des cours en IAM ?",
                        "auteur":"Tom",
                        "couleur":"#ff0000"}]
        ,"idLastMessage":"35"}
        */
        try {
            //Pour tous les messages reçus
            JSONArray messages = o.getJSONArray("messages");
            int i;
            for(i=0;i<messages.length();i++) {
                JSONObject msg = (JSONObject) messages.get(i);
                String contenu =  msg.getString("contenu");
                String auteur =  msg.getString("idAuteur");
                //String couleur =  msg.getString("couleur");

                TextView tv = new TextView(this);
                tv.setText("[" + auteur + "] " + contenu);
                //tv.setTextColor(Color.parseColor(couleur));

                msgLayout.addView(tv);
            }

            //Sauvegarde du dernier message
            idLastMessage = Integer.parseInt(o.getString("lastMessageId"));
            VolleyManager.getInstance().setLastIdMessage(idLastMessage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /************************************/

    /***** Gestion évènement ****/

    @Override
    public void onClick(View v) {
        String msg = edtMsg.getText().toString();

        //Envoyer message avec Volley

        edtMsg.setText("");
    }

    /************************************/
}
