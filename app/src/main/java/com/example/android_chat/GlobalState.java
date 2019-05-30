package com.example.android_chat;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GlobalState extends Application {

    /**
     * Attributs
     */
    public String CAT = "L4-SI-Logs";
    private CookieManager cookieManager;
    private String accessToken;

    /**
     * Création de l'activité
     */
    @Override
    public void onCreate() {
        super.onCreate();
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        //Instanciation de la classe VolleyManager
        VolleyManager.getInstance(this);
    }

    /**
     * Créer un toast
     * @param s
     */
    public void alerter(String s) {
        Log.i(CAT,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_LONG);
        t.show();
    }

    /**
     * Vérifier la connectivité réseau
     * @return
     */
    public boolean verifReseau()
    {
        // On vérifie si le réseau est disponible,
        // si oui on change le statut du bouton de connexion
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();

        String sType = "Aucun réseau détecté";
        Boolean bStatut = false;
        if (netInfo != null)
        {

            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0)
            {
                bStatut = true;
                int netType= netInfo.getType();
                switch (netType)
                {
                    case ConnectivityManager.TYPE_MOBILE :
                        sType = "Réseau mobile détecté"; break;
                    case ConnectivityManager.TYPE_WIFI :
                        sType = "Réseau wifi détecté"; break;
                }

            }
        }

        this.alerter(sType);
        return bStatut;
    }

    /**
     * Accesseur access token
     * @return
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Setteur access token
     * @param accessToken
     */
    public void setAccessToken(String accessToken){
        if(!accessToken.isEmpty())
            this.accessToken = accessToken;
    }
}
