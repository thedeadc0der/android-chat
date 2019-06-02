package com.example.android_chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.android_chat.api.ApiController;

public class LoginActivity extends CommonActivity implements View.OnClickListener {
    private EditText champLogin;
    private EditText champPass;
    private CheckBox champRemember;
    private Button btnOK;
    private Button btnSignup;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(getResources().getString(R.string.actLogin_title));
        
        champLogin = findViewById(R.id.login_edtLogin);
        champPass = findViewById(R.id.login_edtPasse);
        champRemember = findViewById(R.id.login_cbRemember);
        btnOK = findViewById(R.id.login_btnOK);
        btnSignup = findViewById(R.id.login_btnSignup);

        champRemember.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Only enable the "submit" button if we have an internet connection
        btnOK.setEnabled(gs.verifReseau());
    }

    @Override
    protected void onStart() {
        super.onStart();
        showPrefs();
    }
    
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_cbRemember :
                savePrefs();
                break;

            case R.id.login_btnOK :
                final String login = champLogin.getText().toString();
                final String passe = champPass.getText().toString();

                // Sauvegarde des identifiants de connexion
                if(this.champRemember.isChecked())
                    savePrefs();

                // On vérifie que les champs sont remplis
                if( login.isEmpty() || passe.isEmpty() ){
                    gs.alerter(getResources().getString(R.string.missing_fields));
                    return;
                }
                
                gs.getApiController().login(login, passe, new ApiController.Callback<Void>() {
                    @Override
                    public void onResponse(Void obj){
                        startActivity(new Intent(LoginActivity.this, ChoixConvActivity.class));
                    }
    
                    @Override
                    public void onError(Error err){
                        gs.presentError(err);
                        err.printStackTrace();
                    }
                });
                
                break;
                
            case R.id.login_btnSignup:
                startActivity(new Intent(this, SignupActivity.class));
                break;
        }
    }
}
