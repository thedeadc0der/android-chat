package com.example.android_chat;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public abstract class RestActivity extends AppCompatActivity {

    protected GlobalState gs;

    // Une classe capable de faire des requêtes simplement
    // Si elle doit faire plusieurs requetes,
    // comment faire pour controler quelle requete se termine ?
    // on passe une seconde chaine à l'appel asynchrone

    public void envoiRequete(String qs, String action) {
        // En instanciant à chaque fois, on peut faire autant de requetes que l'on veut...

        RestRequest req = new RestRequest(this);
        req.execute(qs,action);
    }

    public String urlPeriodique(String action) {
        // devrait être abstraite, mais dans ce cas doit être obligatoirement implémentée...
        // On pourrait utiliser une interface ?
        return "";
    }

    // http://androidtrainningcenter.blogspot.fr/2013/12/handler-vs-timer-fixed-period-execution.html
    // Try AlarmManager running Service
    // http://rmdiscala.developpez.com/cours/LesChapitres.html/Java/Cours3/Chap3.1.htm
    // La requete elle-même sera récupérée grace à l'action demandée dans la méthode urlPeriodique
    public void requetePeriodique(int periode, final String action) {

        TimerTask doAsynchronousTask;
        final Handler handler = new Handler();
        Timer timer = new Timer();

        doAsynchronousTask = new TimerTask() {

            @Override
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        envoiRequete(urlPeriodique(action),action);
                    }
                });

            }

        };

        timer.schedule(doAsynchronousTask, 0, 1000 * periode);

    }



    public abstract void traiteReponse(JSONObject o, String action);
    // devra être implémenté dans la classe fille

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getApplication();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_account : gs.alerter("Non implementé"); break;
            case R.id.action_settings :
                Intent versPrefs = new Intent(this,SettingsActivity.class);
                startActivity(versPrefs);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}