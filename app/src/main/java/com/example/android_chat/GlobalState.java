package com.example.android_chat;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.api.MockApiController;
import com.example.android_chat.api.VolleyApiController;

public class GlobalState extends Application  {
	private final String DEFAULT_API_URL = "http://10.0.2.2:5000/";
	public final String CAT = "GroveChat";
	
	private ApiController apiController;
	
	@Override
	public void onCreate(){
		super.onCreate();
		recreateApiController();
	}
	
	public void recreateApiController(){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		if( sharedPrefs.getBoolean("mockApi", false) ){
			apiController = new MockApiController(getApplicationContext());
		} else {
			final String apiBase = sharedPrefs.getString("urlData", DEFAULT_API_URL);
			alerter("Using API on " + apiBase);
			apiController = new VolleyApiController(getApplicationContext(), apiBase);
		}
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
	public enum NetworkType {
		None,
		MobileData,
		Wifi,
		Other,
	}
	
	public NetworkType getNetworkType(){
		NetworkInfo netInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		
		if( netInfo == null || !netInfo.isConnected() )
			return NetworkType.None;
		
		switch(netInfo.getType()){
			case ConnectivityManager.TYPE_MOBILE:
				return NetworkType.MobileData;
				
			case ConnectivityManager.TYPE_WIFI:
				return NetworkType.Wifi;
				
			default:
				return NetworkType.Other;
		}
	}
	
	public void presentError(Throwable err){
		alerter(getResources().getString(R.string.error) + err.getMessage());
	}
	
	public ApiController getApiController(){
		return apiController;
	}
	
	public void restartApp(){
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
