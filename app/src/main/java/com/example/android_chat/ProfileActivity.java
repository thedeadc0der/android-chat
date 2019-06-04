package com.example.android_chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.model.Conversation;
import com.example.android_chat.model.User;

import java.util.List;

/** Activité de visualisation d'un profil utilisateur. */
public class ProfileActivity extends CommonActivity implements AdapterView.OnItemClickListener {
	private User user;
	private List<Conversation> conversations;
	
	private TextView pseudoLabel;
	private View colorStrip;
	private ListView convList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		pseudoLabel = findViewById(R.id.actProfile_pseudo);
		colorStrip = findViewById(R.id.actProfile_color);
		convList = findViewById(R.id.actProfile_convs);
		convList.setOnItemClickListener(this);
		
		final Intent intent = getIntent();
		final User user = new User(intent.getIntExtra("user.id", 0), "", "", false);
		
		gs.getApiController().getUserConversations(user, new ApiController.Callback<List<Conversation>>() {
			@Override
			public void onResponse(final List<Conversation> obj){
				new Handler().post(new Runnable() {
					@Override
					public void run(){
						setProfile(user, obj);
					}
				});
			}
			
			@Override
			public void onError(Error err){
				gs.presentError(err);
				finish();
			}
		});
		
		getSupportActionBar().setTitle(getResources().getString(R.string.actProfile_title));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * Affiche les détails donnés dans les vues.
	 * @param user L'utilisateur dont c'est le profil.
	 * @param conversations La liste des conversations où il participe.
	 */
	private void setProfile(User user, List<Conversation> conversations){
		this.user = user;
		this.conversations = conversations;
		
		pseudoLabel.setText(user.getPseudo());
		colorStrip.setBackgroundColor(user.getColor().toColorCode());
		convList.setAdapter(new ConversationListAdapter(this, conversations));
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		final Conversation conversation = conversations.get(position);
		
		final Intent intent = new Intent(this, ShowConvActivity.class);
		intent.putExtra("conversation.id", conversation.getId());
		intent.putExtra("conversation.theme", conversation.getTheme());
		intent.putExtra("conversation.active", conversation.isActive());
		startActivity(intent);
	}
	
	@Override
	public boolean onSupportNavigateUp(){
		finish();
		return true;
	}
}
