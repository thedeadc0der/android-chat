package com.example.android_chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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


    EditText champLogin;
    EditText champPass;
    CheckBox champRemember;
    GlobalState gs;
    Button btnOK;

    class JSONAsyncTask extends AsyncTask<String, Void, JSONObject> {
        // Params, Progress, Result

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("L4-SI-Logs","onPreExecute");
        }

        @Override
        protected JSONObject doInBackground(String... qs) {
            // String... qs est une ellipse:
            // permet de récupérer des arguments passés sous forme de liste arg1, arg2, arg3...
            // dans un tableau
            // pas d'interaction avec l'UI Thread ici
            Log.i("L4-SI-Logs","doInBackground");
            String res = LoginActivity.this.gs.requete(qs[0]);

            JSONObject ob = null;
            try {
                // TODO: interpréter le résultat sous forme d'objet JSON
                ob = new JSONObject(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return ob; // TODO: renvoyer des JSONObject et pas des String
        }

        protected void onPostExecute(JSONObject result) {
            Log.i("L4-SI-Logs","onPostExecute");
            if (result != null ) {
                Log.i("L4-SI-Logs", result.toString());
                LoginActivity.this.gs.alerter(result.toString());

                // TODO: Vérifier la connexion ("connecte":true)
                try {
                    if (result.getBoolean("connecte")) {
                        LoginActivity.this.savePrefs();
                        // TODO: Changer d'activité vers choixConversation
                        Intent toChoixConv = new Intent(LoginActivity.this, ChoixConvActivity.class);
                        startActivity(toChoixConv);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState) getApplication();

        Log.i(gs.CAT,"onCreate");

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

        Log.i(gs.CAT,"onStart");

        // Récupérer les préférences, si la case 'remember' est cochée, on complète le formulaire
        // autres champs des préférences : urlData, login, passe

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean("remember",true)) {
            String login = prefs.getString("login", "");
            String passe = prefs.getString("passe", "");
            champLogin.setText(login);
            champPass.setText(passe);
            champRemember.setChecked(true);
        }

    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_cbRemember : // Clic sur case à cocher
                savePrefs();

                break;


            case R.id.login_btnOK :

                String login = champLogin.getText().toString();
                String passe = champPass.getText().toString();

                String qs = "action=connexion&login=" + login + "&passe=" +passe;

                // gs.requete(qs); // Ceci génère une exception : networkOnMainThread

                // A faire en utilisant une AsyncTask
                JSONAsyncTask js = new JSONAsyncTask();
                js.execute(qs);


                break;

        }

    }
}
