package com.example.android_chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import java.util.Timer;
import java.util.TimerTask;

class ConversationMessageAdapter extends RecyclerView.Adapter<ConversationMessageAdapter.CMViewHolder> {
    public static class CMViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView authorText;
        private TextView contentText;
        private View userColor;
        
        public CMViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView;
            authorText = itemView.findViewById(R.id.message_author);
            contentText = itemView.findViewById(R.id.message_content);
            userColor = itemView.findViewById(R.id.message_color);
        }
        
        public void setMessage(Message msg, boolean isFromUser){
            final Resources r = itemView.getContext().getResources();
            
            // Display the message data
            authorText.setText(msg.getAuthor() != null ? msg.getAuthor().getPseudo() : r.getString(R.string.actShowConv_deleted_user));
            contentText.setText(msg.getContent());
            
            // Display the user's messages in a different color
            cardView.setCardBackgroundColor(r.getColor(isFromUser ? R.color.colorPrimaryDark : R.color.blanc));
            authorText.setTextColor(r.getColor(isFromUser ? R.color.blanc : R.color.title));
            contentText.setTextColor(r.getColor(isFromUser ? R.color.blanc : R.color.text));
            
            // Display a band of the author's color
            if( msg.getAuthor() != null ){
                userColor.setVisibility(View.VISIBLE);
                userColor.setBackgroundColor(msg.getAuthor().getColor().toColorCode());
            } else {
                userColor.setVisibility(View.GONE);
            }
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
    private static final int REFRESH_DELAY = 1000 * 5;
    private static final int VIBRATION_TIME_MS = 500;
    
    private RecyclerView messageList;
    private EditText messageText;
    private Button sendButton;
    
    private ConversationMessageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    
    private User user;
    private Conversation conversation;
    private List<Message> messages;
    
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
        {
            final Intent intent = getIntent();
            final int id = intent.getIntExtra("conversation.id", 0);
            final String theme = intent.getStringExtra("conversation.theme");
            final boolean active = intent.getBooleanExtra("conversation.active", false);
            conversation = new Conversation(id, theme, active, null);
        }
        
        user = gs.getApiController().getCurrentUser();
        messages = new ArrayList<>();
        
        handler = new Handler();
    
        getSupportActionBar().setTitle(conversation.getTheme());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
        // Load the messages
        reloadMessages();
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        
        // We have to use a Handler so retrieveNewMessages is run on the main thread, otherwise
        // it won't be able to change UI elements reliably.
        timer = new Timer();
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
        }, REFRESH_DELAY, REFRESH_DELAY);
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
                        adapter = new ConversationMessageAdapter(ShowConvActivity.this, messages, user);
                        adapter.setHasStableIds(true);
                        messageList.setAdapter(adapter);
                        messageList.scrollToPosition(messages.size() - 1);
                    }
                });
            }
    
            @Override
            public void onError(Error err){
                gs.presentError(err);
            }
        });
    }
    
    private void addMessage(Message msg){
        messages.add(msg);
        adapter.notifyDataSetChanged();
        messageList.smoothScrollToPosition(messages.size() - 1);
    }
    
    private void retrieveNewMessages(){
        // If we don't have any messages, we can't give the "last" message's id so we just reload the whole thing.
        if( messages.isEmpty() ){
            reloadMessages();
            return;
        }
        
        // If we do, then only retrieve messages past the last one.
        final Message lastMessage = messages.get(messages.size() - 1);
        
        gs.getApiController().listMessagesFrom(conversation, lastMessage, new ApiController.Callback<List<Message>>() {
            @Override
            public void onResponse(final List<Message> msg){
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        if( !msg.isEmpty() ){
                            messages.addAll(msg);
                            adapter.notifyDataSetChanged();
                            messageList.smoothScrollToPosition(messages.size() - 1);
                        }
                    }
                });
            }
    
            @Override
            public void onError(Error err){
                gs.presentError(err);
            }
        });
    }
    
    private void askToDeleteMessage(final Message msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.actShowConv_delete_title));
        builder.setMessage(getResources().getString(R.string.actShowConv_delete_message));
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.actShowConv_delete_action), new DialogInterface.OnClickListener() {
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
                gs.presentError(err);
            }
        });
    }
    
    private void sendMessage(){
        final String content = messageText.getText().toString().trim();
    
        if( content.isEmpty() ){
            gs.alerter(getResources().getString(R.string.actShowConv_no_message));
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
                gs.presentError(err);
            }
        });
    }
    
    private void visitProfile(User user){
        final Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user.id", user.getId());
        startActivity(intent);
    }
    
    private void signalNewMessages(){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATION_TIME_MS);
    }
    
    void onMessageLongClick(final Message msg){
        final String [] options = {getResources().getString(R.string.actShowConv_action_show_profile), getResources().getString(R.string.actShowConv_action_delete)};
    
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.actShowConv_action_title));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                switch(which){
                    case 0:
                        if( msg.getAuthor() == null ){
                            gs.alerter(getResources().getString(R.string.actShowConv_no_profile));
                            return;
                        }
                        
                        visitProfile(msg.getAuthor());
                        break;
                        
                    case 1:
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
