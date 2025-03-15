package me.grishka.dndtoggle;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{
	private Button openSettingsBtn, addTileBtn;
	private TextView permissionGrantedText, tileAddedText;
	private static MainActivity current;

	private final BroadcastReceiver permissionChangeReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			if(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED.equals(intent.getAction())){
				updateButtons();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		current=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		openSettingsBtn=findViewById(R.id.settings_btn);
		addTileBtn=findViewById(R.id.add_tile_btn);
		permissionGrantedText=findViewById(R.id.permission_granted);
		tileAddedText=findViewById(R.id.tile_added);

		openSettingsBtn.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)));
		addTileBtn.setOnClickListener(v->{
			StatusBarManager sbm=getSystemService(StatusBarManager.class);
			sbm.requestAddTileService(new ComponentName(this, DNDTileService.class), getString(R.string.do_not_disturb), Icon.createWithResource(this, R.drawable.ic_do_not_disturb), getMainExecutor(), result->updateButtons());
		});

		registerReceiver(permissionChangeReceiver, new IntentFilter(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED), RECEIVER_EXPORTED);
		updateButtons();
	}

	@Override
	protected void onDestroy(){
		current=null;
		unregisterReceiver(permissionChangeReceiver);
		super.onDestroy();
	}

	public static void updateFromService(){
		if(current!=null){
			current.runOnUiThread(current::updateButtons);
		}
	}

	private void updateButtons(){
		NotificationManager nm=getSystemService(NotificationManager.class);
		if(nm.isNotificationPolicyAccessGranted()){
			openSettingsBtn.setVisibility(View.INVISIBLE);
			permissionGrantedText.setVisibility(View.VISIBLE);
			if(DNDTileService.isTileAdded(this)){
				addTileBtn.setVisibility(View.INVISIBLE);
				tileAddedText.setVisibility(View.VISIBLE);
			}else{
				addTileBtn.setVisibility(View.VISIBLE);
				addTileBtn.setEnabled(true);
				tileAddedText.setVisibility(View.INVISIBLE);
			}
		}else{
			permissionGrantedText.setVisibility(View.INVISIBLE);
			tileAddedText.setVisibility(View.INVISIBLE);
			addTileBtn.setVisibility(View.VISIBLE);
			addTileBtn.setEnabled(false);
		}
	}
}
