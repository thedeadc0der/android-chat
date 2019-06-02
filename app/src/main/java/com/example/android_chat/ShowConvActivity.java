package com.example.android_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.model.Conversation;
import com.example.android_chat.model.Message;
import com.example.android_chat.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
            final Resources r = itemView.getContext().getResources();
            
            authorText.setText(msg.getAuthor() != null ? msg.getAuthor().getPseudo() : "(compte supprimé)");
            contentText.setText(msg.getContent());
            cardView.setCardBackgroundColor(r.getColor(isFromUser ? R.color.colorPrimaryDark : R.color.blanc));
            authorText.setTextColor(r.getColor(isFromUser ? R.color.blanc : R.color.title));
            contentText.setTextColor(r.getColor(isFromUser ? R.color.blanc : R.color.text));
        }
    }
    
    private ShowConvActivity activity;
    private List<Message> messages;
    private User user;
    
    public ConversationMessageAdapter(ShowConvActivity activity, List<Message> messages, User user){
        this.activity = activity;
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
        final boolean isFromUser = message.getAuthor() != null ? message.getAuthor().equals(user) : false;
        holder.setMessage(message, isFromUser);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v){
                activity.onMessageLongClick(message);
                return true;
            }
        });
    }
    
    @Override
    public int getItemCount(){
        return messages.size();
    }
    
    @Override
    public long getItemId(int position){
        return messages.get(position).getId();
    }
}

public class ShowConvActivity extends CommonActivity implements View.OnClickListener {
    private RecyclerView messageList;
    private EditText messageText;
    private Button sendButton;
    
    private ConversationMessageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    
    private Conversation conversation;
    private List<Message> messages;
    private User user;
    private Message lastMessage;
    private Timer timer;
    private Handler handler;
    
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
        
        timer = new Timer();
        handler = new Handler();
        
        // Load the messages
        getSupportActionBar().setTitle(conversation.getTheme());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reloadMessages();
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        
        // We have to use a Handler so retrieveNewMessages is run on the main thread, otherwise
        // it won't be able to change UI elements reliably.
        timer.schedule(new TimerTask() {
            @Override
            public void run(){
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        retrieveNewMessages();
                    }
                });
            }
        }, 0, 1000 * 5);
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        timer.cancel();
        timer.purge();
    }
    
    private void reloadMessages(){
        gs.getApiController().listMessages(conversation, new ApiController.Callback<List<Message>>() {
            @Override
            public void onResponse(final List<Message> obj){
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        messages = new ArrayList<>(obj);
                        lastMessage = obj.isEmpty() ? null : obj.get(obj.size() - 1);
                        adapter = new ConversationMessageAdapter(ShowConvActivity.this, messages, user);
                        adapter.setHasStableIds(true);
                        messageList.setAdapter(adapter);
                        messageList.scrollToPosition(messages.size() - 1);
                    }
                });
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
    
    private void retrieveNewMessages(){
        if( lastMessage == null ){
            reloadMessages();
            return;
        }
        
        gs.getApiController().listMessagesFrom(conversation, lastMessage, new ApiController.Callback<List<Message>>() {
            @Override
            public void onResponse(final List<Message> msg){
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        if( !msg.isEmpty() ){
                            messages.addAll(msg);
                            lastMessage = messages.get(messages.size() - 1);
                            adapter.notifyDataSetChanged();
                            messageList.smoothScrollToPosition(messages.size() - 1);
                        }
                    }
                });
            }
    
            @Override
            public void onError(Error err){
                gs.alerter("Erreur: " + err.getMessage());
            }
        });
    }
    
    private void askToDeleteMessage(final Message msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suppression");
        builder.setMessage("Voulez-vous vraiment supprimer ce message?");
        builder.setCancelable(true);
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                deleteMessage(msg);
            }
        });
        builder.create().show();
    }
    
    private void deleteMessage(final Message msg){
        gs.getApiController().deleteMessage(msg, new ApiController.Callback<Void>() {
            @Override
            public void onResponse(Void obj){
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        messages.remove(msg);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            
            @Override
            public void onError(Error err){
                gs.alerter("Erreur: " + err.getMessage());
            }
        });
    }
    
    private void sendMessage(){
        final String content = messageText.getText().toString().trim();
    
        if( content.isEmpty() ){
            gs.alerter("Tapez un message");
            return;
        }
    
        gs.getApiController().sendMessage(conversation, content, new ApiController.Callback<Message>() {
            @Override
            public void onResponse(final Message msg){
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        addMessage(msg);
                        messageText.setText("");
                    }
                });
            }
        
            @Override
            public void onError(Error err){
                gs.alerter("Erreur: " + err.getMessage());
            }
        });
    
    
    }
    
    void onMessageLongClick(final Message msg){
        final String [] options = {"Supprimer"};
    
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                switch(which){
                    case 0:
                        askToDeleteMessage(msg);
                        break;
                }
            }
        });
    
        builder.create().show();
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.showConv_btnSend:
                sendMessage();
                break;
        }
    }
    
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
