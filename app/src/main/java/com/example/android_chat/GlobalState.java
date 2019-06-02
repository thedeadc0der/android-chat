package com.example.android_chat;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
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
}
