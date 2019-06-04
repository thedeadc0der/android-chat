package com.example.android_chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.example.android_chat.api.ApiController;
import com.example.android_chat.model.Color;
import com.example.android_chat.model.User;

/** Activité pour modifier son compte */
public class MyAccountActivity extends CommonActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
	private EditText loginEdit;
	private SeekBar colorRed;
	private SeekBar colorGreen;
	private SeekBar colorBlue;
	private View colorView;
	private Button saveButton;
	private Button deleteButton;
	
	/**
	 * Assigne la bande de couleur à la couleur représentée par les sliders RGB.
	 */
	private void updateColor(){
		Color color = new Color();
		color.red = colorRed.getProgress();
		color.green = colorGreen.getProgress();
		color.blue = colorBlue.getProgress();
		colorView.setBackgroundColor(color.toColorCode());
	}
	
	/**
	 * Récupère la couleur entrée sur les sliders RGB.
	 */
	private Color getColor(){
		final Color result = new Color();
		result.red = colorRed.getProgress();
		result.green = colorGreen.getProgress();
		result.blue = colorBlue.getProgress();
		return result;
	}
	
	/**
	 * Assigne les sliders et la bande de couleur à la couleur donnée.
	 * @param c La couleur à afficher.
	 */
	private void setColor(Color c){
		colorRed.setProgress(c.red);
		colorGreen.setProgress(c.green);
		colorBlue.setProgress(c.blue);
		colorView.setBackgroundColor(c.toColorCode());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		loginEdit = findViewById(R.id.account_edtPseudo);
		colorView = findViewById(R.id.account_color);
		colorRed = findViewById(R.id.account_colorRed);
		colorRed.setOnSeekBarChangeListener(this);
		colorGreen = findViewById(R.id.account_colorGreen);
		colorGreen.setOnSeekBarChangeListener(this);
		colorBlue = findViewById(R.id.account_colorBlue);
		colorBlue.setOnSeekBarChangeListener(this);
		saveButton = findViewById(R.id.account_btnSave);
		saveButton.setOnClickListener(this);
		deleteButton = findViewById(R.id.account_btnDelete);
		deleteButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		// This activity only makes sense when you're logged in
		if( gs.getApiController().getCurrentUser() == null ){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.actAccount_noAccount_title));
			builder.setMessage(getResources().getString(R.string.actAccount_noAccount_message));
			builder.setCancelable(true);
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog){
					finish();
					
				}
			});
			builder.create().show();;
			return;
		}
		
		final User user = gs.getApiController().getCurrentUser();
		loginEdit.setText(user.getPseudo());
		setColor(user.getColor());
	}
	
	@Override
	public boolean onSupportNavigateUp(){
		finish();
		return false;
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.account_btnSave:
				saveButton.setEnabled(false);
				
				gs.getApiController().updateAccountInfo(loginEdit.getText().toString(), getColor(), new ApiController.Callback<Void>() {
					@Override
					public void onResponse(Void obj){
						gs.alerter(getResources().getString(R.string.actAccount_noAccount_saved));
						saveButton.setEnabled(true);
						finish();
					}
					
					@Override
					public void onError(Error err){
						gs.presentError(err);
					}
				});
				
				break;
				
			case R.id.account_btnDelete:
				android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.actAccount_delete_title));
				builder.setMessage(getResources().getString(R.string.actAccount_delete_message));
				builder.setCancelable(true);
				builder.setPositiveButton(getResources().getString(R.string.actAccount_delete_action), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which){
						deleteAccount();
					}
				});
				builder.create().show();
				
				break;
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
		updateColor();
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar){
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar){
	}
	
	/**
	 * Effectue la suppression du compte utilisateur.
	 */
	private void deleteAccount(){
		gs.getApiController().deleteUser(gs.getApiController().getCurrentUser(), new ApiController.Callback<Void>() {
			@Override
			public void onResponse(Void obj){
				gs.restartApp();
			}
			
			@Override
			public void onError(Error err){
				gs.presentError(err);
			}
		});
	}
}
