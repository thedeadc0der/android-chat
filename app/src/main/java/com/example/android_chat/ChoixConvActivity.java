package com.example.android_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.model.Conversation;

import java.util.List;

public class ChoixConvActivity extends CommonActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {
    private List<Conversation> conversations;
    private ListView conversationList;
    private FloatingActionButton newButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);
        getSupportActionBar().setTitle(getResources().getString(R.string.actConvList_title));
        
        conversationList = findViewById(R.id.choixConversation_list);
        conversationList.setOnItemLongClickListener(this);
        conversationList.setOnItemClickListener(this);
        newButton = findViewById(R.id.choixConversation_new);
        newButton.setOnClickListener(this);
        
        reloadConversationList();
    }
	
	@Override
	protected void onResume(){
		super.onResume();
		reloadConversationList();
	}
	
	private void reloadConversationList(){
	    gs.getApiController().listConversations(new ApiController.Callback<List<Conversation>>() {
		    @Override
		    public void onResponse(List<Conversation> obj){
			    conversations = obj;
			    conversationList.setAdapter(new ConversationListAdapter(ChoixConvActivity.this, conversations));
		    }
		
		    @Override
		    public void onError(Error err){
			    gs.presentError(err);
		    }
	    });
    }
    
    private void askToDeleteConversation(final Conversation conversation){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.actConvList_delete_title));
        builder.setMessage(String.format(getResources().getString(R.string.actConvList_delete_message), conversation.getTheme()));
	    builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.actConvList_delete_action), new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which){
	        	deleteConversation(conversation);
	        }
        });
        builder.create().show();
    }
    
    private void deleteConversation(Conversation conversation){
        gs.getApiController().deleteConversation(conversation, new ApiController.Callback<Void>() {
	        @Override
	        public void onResponse(Void obj){
		        reloadConversationList();
	        }
	
	        @Override
	        public void onError(Error err){
	        	gs.presentError(err);
	        }
        });
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
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
		final Conversation conversation = conversations.get(position);
		final String [] options = {getResources().getString(R.string.actConvList_action_delete)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(String.format(getResources().getString(R.string.actConvList_action_title), conversation.getTheme()));
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which){
				switch(which){
					case 0:
						askToDeleteConversation(conversation);
						break;
				}
			}
		});
		
		builder.create().show();
		return true;
	}
	
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.choixConversation_new:
                startActivity(new Intent(this, NewConversationActivity.class));
                break;
        }
    }
}
