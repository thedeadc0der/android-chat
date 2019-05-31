package com.example.android_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.model.Conversation;

public class NewConversationActivity extends CommonActivity implements View.OnClickListener {
	private EditText themeEdit;
	private Button submitButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_conversation);
		
		themeEdit = findViewById(R.id.newConv_edtTheme);
		submitButton = findViewById(R.id.newConv_btnSubmit);
		
		submitButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.newConv_btnSubmit:
				final String theme = themeEdit.getText().toString();
				
				if( theme.isEmpty() ){
					gs.alerter("Remplissez tous les champs");
					return;
				}
				
				gs.getApiController().createConversation(theme, new ApiController.Callback<Conversation>() {
					@Override
					public void onResponse(Conversation conversation){
						NewConversationActivity.this.finish();
						/*
						final Intent intent = new Intent(NewConversationActivity.this, ShowConvActivity.class);
						intent.putExtra("conversation.id", conversation.getId());
						intent.putExtra("conversation.theme", conversation.getTheme());
						intent.putExtra("conversation.active", conversation.isActive());
						startActivity(intent);
						*/
					}
					
					@Override
					public void onError(Error err){
						gs.alerter("Erreur: " + err.getMessage());
					}
				});
				
				break;
		}
	}
}
