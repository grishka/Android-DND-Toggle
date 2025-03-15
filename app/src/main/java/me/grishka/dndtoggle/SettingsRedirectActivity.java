package me.grishka.dndtoggle;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public class SettingsRedirectActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		startActivity(new Intent(Settings.ACTION_ZEN_MODE_PRIORITY_SETTINGS));
		finish();
	}
}
