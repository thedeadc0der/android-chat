package com.example.android_chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Attributs
     */
    private EditText champLogin;
    private EditText champPass;
    private CheckBox champRemember;
    private GlobalState gs;
    private Button btnOK;

    /***** Gestion de l'état de l'activité ****/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState) getApplication();
        setContentView(R.layout.activity_login);

        champLogin = findViewById(R.id.login_edtLogin);
        champPass = findViewById(R.id.login_edtPasse);
        champRemember = findViewById(R.id.login_cbRemember);
        btnOK = findViewById(R.id.login_btnOK);

        champRemember.setOnClickListener(this);
        btnOK.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(gs.CAT,"onResume");

        // Verif Réseau, si dispo, on active le bouton
        btnOK.setEnabled(gs.verifReseau());
    }

    @Override
    protected void onStart() {
        super.onStart();
        showPrefs();
    }
    /************************************/


    /***** Gestion des préférences ****/
    /**
     * Récupère les préférences
     */
    private void showPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean("remember",true)) {
            String login = prefs.getString("login", "");
            String passe = prefs.getString("passe", "");
            champLogin.setText(login);
            champPass.setText(passe);
            champRemember.setChecked(true);
        }
    }

    /**
     * Enregistrer les identifiants dans les préférences de l'application
     */
    private void savePrefs() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();

        if (champRemember.isChecked()) {
            editor.putBoolean("remember", true);
            editor.putString("login", champLogin.getText().toString());
            editor.putString("passe", champPass.getText().toString());
        } else {
            editor.putBoolean("remember", false);
            editor.putString("login", "");
            editor.putString("passe", "");
        }
        editor.commit();
    }
    /************************************/


    /***** Gestion du menu hamburger ****/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings : gs.alerter("preferences");
            // afficher l'activité "préférences"
                Intent toSettings = new Intent(this,SettingsActivity.class);
                startActivity(toSettings);
            break ;
            case R.id.action_account : gs.alerter("compte"); break ;

        }
        return super.onOptionsItemSelected(item);
    }
    /************************************/

    /***** Gestion des évènements ****/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_cbRemember :
                savePrefs();
            break;

            case R.id.login_btnOK :

                String login = champLogin.getText().toString();
                String passe = champPass.getText().toString();

                //Sauvegarde des identifiants de connexion
                if(this.champRemember.isChecked()){
                    savePrefs();
                }

                //Requête de connexion
                if(!login.isEmpty() && !passe.isEmpty()){
                    VolleyManager.getInstance().loginRequest(login, passe, new customListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject object) {
                            if(object != null){
                                Log.d(gs.CAT, object.toString());
                                try {
                                    //Connexion réussie
                                    if(object.getString("status").equals("success")){
                                        gs.setAccessToken(object.getString("token"));
                                        Intent versChoixConversation = new Intent(LoginActivity.this, ChoixConvActivity.class);
                                        startActivity(versChoixConversation);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                break;
        }
    }
    /************************************/
}
