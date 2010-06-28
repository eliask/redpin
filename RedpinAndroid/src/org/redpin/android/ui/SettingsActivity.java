package org.redpin.android.ui;

import org.redpin.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends Activity {
	
	/**
	 * Called when the activity is starting inflating the activity's UI. This is
	 * where most initialization should go.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {			
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings_view);
		
	}
	
	
	/**
	 * Starts the setting screen
	 * 
	 * @param target {@link View} that called this method
	 */
	public void button_Mapview(View target) {
		Intent intent = new Intent(this, MapViewActivity.class);
		startActivity(intent);
		/*
		Intent intent = new Intent();
		intent.setType(Intent.ACTION_VIEW);
		//intent.setClassName(this, Preferences.class.getName());
		this.startActivity(intent);*/
	}

	
	public void button_ServerPreferences(View target) {
		Intent intent = new Intent(this, ServerPreferences.class);
		startActivity(intent);
	}

}
