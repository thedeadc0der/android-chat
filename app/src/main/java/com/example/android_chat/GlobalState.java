package com.example.android_chat;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.api.MockApiController;
import com.example.android_chat.api.VolleyApiController;

import java.net.CookieHandler;
import java.net.CookieManager;

public class GlobalState extends Application {
	public String CAT = "L4-SI-Logs";
	private ApiController apiController;
	
	@Override
	public void onCreate(){
		super.onCreate();
		//apiController = new MockApiController();
		apiController = new VolleyApiController(getApplicationContext());
	}
	
	/**
	 * Créer un toast
	 *
	 * @param s
	 */
	public void alerter(String s){
		Log.i(CAT, s);
		Toast t = Toast.makeText(this, s, Toast.LENGTH_LONG);
		t.show();
	}
	
	/**
	 * Vérifier la connectivité réseau
	 *
	 * @return
	 */
	public boolean verifReseau(){
		// On vérifie si le réseau est disponible,
		// si oui on change le statut du bouton de connexion
		ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();
		
		String sType = "Aucun réseau détecté";
		Boolean bStatut = false;
		if (netInfo != null){
			NetworkInfo.State netState = netInfo.getState();
			
			if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0){
				bStatut = true;
				int netType = netInfo.getType();
				switch (netType) {
					case ConnectivityManager.TYPE_MOBILE:
						sType = "Réseau mobile détecté";
						break;
					case ConnectivityManager.TYPE_WIFI:
						sType = "Réseau wifi détecté";
						break;
				}
			}
		}
		
		this.alerter(sType);
		return bStatut;
	}
	
	public void presentError(Throwable err){
		alerter(getResources().getString(R.string.error) + err.getMessage());
	}
	
	public ApiController getApiController(){
		return apiController;
	}
}
