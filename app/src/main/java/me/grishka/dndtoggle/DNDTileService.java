package me.grishka.dndtoggle;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class DNDTileService extends TileService{
	private static final String TAG="DNDTileService";
	public static DNDTileService current;

	@Override
	public void onStartListening(){
		current=this;
		super.onStartListening();
		updateTile();
	}

	@Override
	public void onStopListening(){
		current=null;
		super.onStopListening();
	}

	@Override
	public void onClick(){
		super.onClick();
		NotificationManager nm=getSystemService(NotificationManager.class);
		boolean isDND=nm.getCurrentInterruptionFilter()==NotificationManager.INTERRUPTION_FILTER_PRIORITY;
		nm.setInterruptionFilter(isDND ? NotificationManager.INTERRUPTION_FILTER_ALL : NotificationManager.INTERRUPTION_FILTER_PRIORITY);
		updateTile();
	}

	@Override
	public void onTileAdded(){
		super.onTileAdded();
		getSharedPrefs(this).edit().putBoolean("added", true).apply();
		MainActivity.updateFromService();
	}

	@Override
	public void onTileRemoved(){
		super.onTileRemoved();
		getSharedPrefs(this).edit().remove("added").apply();
		MainActivity.updateFromService();
	}

	public void updateTile(){
		NotificationManager nm=getSystemService(NotificationManager.class);
		Tile tile=getQsTile();
		if(!nm.isNotificationPolicyAccessGranted()){
			tile.setState(Tile.STATE_UNAVAILABLE);
			tile.setSubtitle(getString(R.string.tile_no_permission));
			tile.setIcon(Icon.createWithResource(this, R.drawable.ic_do_not_disturb));
		}else{
			boolean isDND=nm.getCurrentInterruptionFilter()==NotificationManager.INTERRUPTION_FILTER_PRIORITY;
			tile.setSubtitle(null);
			tile.setState(isDND ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
			tile.setIcon(Icon.createWithResource(this, isDND ? R.drawable.ic_do_not_disturb_on_fill1_24px : R.drawable.ic_do_not_disturb_on_24px));
		}
		tile.updateTile();
	}

	private static SharedPreferences getSharedPrefs(Context context){
		return context.getSharedPreferences("tile", MODE_PRIVATE);
	}

	public static boolean isTileAdded(Context context){
		return getSharedPrefs(context).getBoolean("added", false);
	}
}
