package com.example.android_chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChoixConvActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Attributs
     */
    private GlobalState gs;
    private ListeConversations listeConvs;
    private Button btnOK;
    private Spinner sp;

    /***** Gestion de l'état de l'activité ****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);
        gs = (GlobalState) getApplication();

        btnOK = findViewById(R.id.choixConversation_btnOK);
        btnOK.setOnClickListener(this);

        sp = (Spinner) findViewById(R.id.choixConversation_choixConv);
        listeConvs = new ListeConversations();

        //Récupération des la liste des conversations
        VolleyManager.getInstance().getConversationsRequest(gs.getAccessToken(), new customListener<JSONArray>() {
            @Override
            public void getResult(JSONArray object) {
                Log.d(gs.CAT,object.toString());
                if (object != null) {
                    traiteReponse(object);
                }
            }
        });

    }
    /************************************/


    /***** Gestion des requêtes ****/

    /**
     * Anaylyse le JSON des conversations
     * @param o
     */
    public void traiteReponse(JSONArray o) {

        for (int i = 0; i < o.length(); i++) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(o.getString(i));

                //Récupération des attributs de la conversation
                int id = obj.getInt("id");
                String theme = obj.getString("theme");
                Boolean active = ((String) obj.getString("active")).contentEquals("1");

                //Création d'une conversation
                Conversation conversation = new Conversation(id,theme, active);
                listeConvs.addConversation(conversation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        btnOK.setEnabled(true);
        remplirSpinner();
    }

    /**
     * Rempli le spinner avec la liste des conversations récupérées
     */
    private void remplirSpinner() {
        ArrayAdapter<Conversation> dataAdapter =
                new ArrayAdapter<Conversation>(this,
                        android.R.layout.simple_spinner_item, listeConvs.getList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);
    }

    /************************************/

    /***** Gestion évènement ****/
    @Override
    public void onClick(View v) {

        Conversation convSelected = (Conversation) sp.getSelectedItem();

        //Passage à l'acitivté ShowConvActivity
        Intent versShowConv = new Intent(this,ShowConvActivity.class);
        Bundle bdl = new Bundle();
        bdl.putInt("idConversation",convSelected.getId());
        versShowConv.putExtras(bdl);
        startActivity(versShowConv);
    }

    /************************************/
}
