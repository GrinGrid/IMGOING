package net.gringrid.imgoing.location;

import net.gringrid.imgoing.MainActivity;
import android.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

public class ResponseLocationServiceReceiver extends BroadcastReceiver {

	private LocationUtil locationUtil;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle mBundle = new Bundle();
		mBundle = intent.getExtras();
		
		// 위치전송 시작 
		if ( mBundle.get("MODE").equals("START") ){
			Log.d("jiho", "onReceive");
			locationUtil = LocationUtil.getInstance(context);
			locationUtil.start_time = mBundle.getString("START_TIME");
			locationUtil.receiver = mBundle.getString("RECEIVER");
			locationUtil.receiver_id = mBundle.getString("RECEIVER_ID");
			locationUtil.interval = mBundle.getInt("INTERVAL"); 
			locationUtil.sendLocation();
			
		// 위치전송 종료 	
		}else if ( mBundle.get("MODE").equals("STOP") ){
			
			locationUtil = LocationUtil.getInstance(context);
			locationUtil.stopLocationUpdate();
			Log.d("jiho", "STOP");
			
		}
	}
}
