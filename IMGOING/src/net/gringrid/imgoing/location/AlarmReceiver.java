package net.gringrid.imgoing.location;

import net.gringrid.imgoing.util.Util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	public static String ACTION_ALARM = "com.alarammanager.alaram";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("jiho", "/**************************************");
		Log.d("jiho", "AlarmReceiver onReceive");
		
		Bundle bundle = intent.getExtras();
		Intent intentService = new Intent(context, SendCurrentLocationService.class);
		intentService.putExtras(bundle);
		context.startService(intentService);
 
	}
}
