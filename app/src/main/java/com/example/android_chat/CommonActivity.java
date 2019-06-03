package com.example.android_chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class CommonActivity extends AppCompatActivity {
	protected GlobalState gs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		gs = (GlobalState) getApplication();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(this,SettingsActivity.class));
				break;
			
			case R.id.action_account:
				startActivity(new Intent(this, MyAccountActivity.class));
				break;
				
			case R.id.action_profile:
				if( gs.getApiController().getCurrentUser() == null )
					break;
				
				Intent intent = new Intent(this, ProfileActivity.class);
				intent.putExtra("user.id", gs.getApiController().getCurrentUser().getId());
				startActivity(intent);
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
