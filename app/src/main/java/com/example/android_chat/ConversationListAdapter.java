package com.example.android_chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android_chat.model.Conversation;

import java.util.List;

/** Adapter pour ListView pour afficher une liste de conversations */
public class ConversationListAdapter extends ArrayAdapter {
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
		
		View convColor = rowView.findViewById(R.id.conv_color);
		convColor.setBackgroundColor(conversation.isActive()
				? activity.getResources().getColor(R.color.active_conversation)
				: activity.getResources().getColor(R.color.inactive_conversation));
		
		return rowView;
	}
}