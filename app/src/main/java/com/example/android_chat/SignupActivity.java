package com.example.android_chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.model.User;

public class SignupActivity extends CommonActivity implements View.OnClickListener {
	private EditText loginField;
	private EditText passField;
	private Button submitButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		getSupportActionBar().setTitle("Inscription");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		loginField = findViewById(R.id.signup_edtLogin);
		passField = findViewById(R.id.signup_edtPass);
		submitButton = findViewById(R.id.signup_btnSubmit);
		
		submitButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.signup_btnSubmit:
				final String login = loginField.getText().toString();
				final String pass = passField.getText().toString();
				
				if( login.isEmpty() || pass.isEmpty() ){
					gs.alerter("Remplissez tous les champs");
					return;
				}
				
				gs.getApiController().signup(login, pass, new ApiController.Callback<Void>() {
					@Override
					public void onResponse(Void obj){
						gs.alerter("Vous êtes inscrit!");
						finish();
					}
					
					@Override
					public void onError(Error err){
						gs.alerter("Erreur: " + err.toString());
					}
				});
				
				break;
		}
	}
	
	@Override
	public boolean onSupportNavigateUp(){
		finish();
		return true;
	}
}
