package com.example.android_chat;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.model.Conversation;
import com.example.android_chat.model.Message;
import com.example.android_chat.model.User;

import java.util.ArrayList;
import java.util.List;

class ConversationMessageAdapter extends RecyclerView.Adapter<ConversationMessageAdapter.CMViewHolder> {
    public static class CMViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView authorText;
        private TextView contentText;
        
        public CMViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView;
            authorText = itemView.findViewById(R.id.message_author);
            contentText = itemView.findViewById(R.id.message_content);
        }
        
        public void setMessage(Message msg, boolean isFromUser){
            authorText.setText(msg.getAuthor().getPseudo());
            contentText.setText(msg.getContent());
            
            if( isFromUser ){
                Resources r = itemView.getContext().getResources();
                cardView.setCardBackgroundColor(r.getColor(R.color.colorPrimaryDark));
                authorText.setTextColor(r.getColor(R.color.blanc));
                contentText.setTextColor(r.getColor(R.color.blanc));
            }
        }
    }
    
    private List<Message> messages;
    private User user;
    
    public ConversationMessageAdapter(List<Message> messages, User user){
        this.messages = messages;
        this.user = user;
    }
    
    @Override
    public CMViewHolder onCreateViewHolder(ViewGroup parent, int i){
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new CMViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(CMViewHolder holder, int position){
        final Message message = messages.get(position);
        holder.setMessage(message, message.getAuthor().equals(user));
    }
    
    @Override
    public int getItemCount(){
        return messages.size();
    }
}

public class ShowConvActivity extends CommonActivity implements View.OnClickListener {
    private RecyclerView messageList;
    private EditText messageText;
    private Button sendButton;
    
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    
    private Conversation conversation;
    private List<Message> messages;
    private User user;
    private Message lastMessage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the view layout and retrieve the views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversation);
        
        messageList = findViewById(R.id.showConv_messages);
        messageText = findViewById(R.id.showConv_edtMessage);
        sendButton = findViewById(R.id.showConv_btnSend);
        sendButton.setOnClickListener(this);
    
        layoutManager = new LinearLayoutManager(this);
        messageList.setLayoutManager(layoutManager);
        
        // Retrieve the conversation
        final Intent intent = getIntent();
        final int id = intent.getIntExtra("conversation.id", 0);
        final String theme = intent.getStringExtra("conversation.theme");
        final boolean active = intent.getBooleanExtra("conversation.active", false);
        conversation = new Conversation(id, theme, active, null);
        user = gs.getApiController().getCurrentUser();
        messages = new ArrayList<>();
        
        // Load the messages
        getSupportActionBar().setTitle(conversation.getTheme());
        reloadMessages();
    }
    
    private void reloadMessages(){
        gs.getApiController().listMessages(conversation, new ApiController.Callback<List<Message>>() {
            @Override
            public void onResponse(List<Message> obj){
                messages = new ArrayList<>(obj);
                lastMessage = obj.get(obj.size() - 1);
                adapter = new ConversationMessageAdapter(obj, user);
                messageList.setAdapter(adapter);
            }
    
            @Override
            public void onError(Error err){
                gs.alerter("Erreur: " + err.getMessage());
            }
        });
    }
    
    private void addMessage(Message msg){
        lastMessage = msg;
        messages.add(msg);
        adapter.notifyDataSetChanged();
        messageList.smoothScrollToPosition(messages.size() - 1);
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.showConv_btnSend:
                final String content = messageText.getText().toString();
                
                if( content.trim().isEmpty() ){
                    gs.alerter("Tapez un message");
                    return;
                }
                
                gs.getApiController().sendMessage(conversation, content, new ApiController.Callback<Message>() {
                    @Override
                    public void onResponse(Message msg){
                        addMessage(msg);
                        messageText.setText("");
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
