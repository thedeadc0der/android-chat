package com.example.android_chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.model.Conversation;

import java.util.List;

class ConversationListAdapter extends ArrayAdapter {
    private List<Conversation> conversations;
    private Activity activity;
    
    public ConversationListAdapter(Activity context, List<Conversation> conversations){
        super(context, R.layout.conversation_item, conversations);
        this.activity = context;
        this.conversations = conversations;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Conversation conversation = conversations.get(position);
        
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.conversation_item, null, true);
        
        TextView convTitle = rowView.findViewById(R.id.conv_lblTitle);
        convTitle.setText(conversation.getTheme());
        
        TextView convLastMsg = rowView.findViewById(R.id.conv_lblLastMsg);
        convLastMsg.setText(conversation.getLastMessage());
        
        ColorView convColor = rowView.findViewById(R.id.conv_color);
        convColor.setColor(conversation.isActive() ? "#ff4db6ac" : "#ffbdbdbd");
        return rowView;
    }
}

public class ChoixConvActivity extends CommonActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {
    private List<Conversation> conversations;
    private ListView conversationList;
    private FloatingActionButton newButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);
        getSupportActionBar().setTitle("Conversations");
        
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
			    gs.alerter("Erreur: " + err.getMessage());
		    }
	    });
    }
    
    private void askToDeleteConversation(final Conversation conversation){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suppression");
        builder.setMessage("Voulez-vous vraiment supprimer la conversation '" + conversation.getTheme() + "' ?\nCette opération est irréversible!");
	    builder.setCancelable(true);
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
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
	        	gs.alerter("Erreur: " + err.getMessage());
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
		final String [] options = {"Supprimer"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Conversation " + conversation.getTheme());
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
