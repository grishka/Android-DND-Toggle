package me.grishka.dndtoggle;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.service.quicksettings.TileService;

public class InterruptionFilterChangeReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent){
		if(DNDTileService.current!=null)
			DNDTileService.current.updateTile();
		else
			TileService.requestListeningState(context, new ComponentName(context, DNDTileService.class));
	}
}
