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

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle mBundle = new Bundle();
		mBundle = intent.getExtras();
		
		if ( mBundle.get("MODE").equals("START") ){
			LocationUtil.getInstance(context);
		}else if ( mBundle.get("MODE").equals("STOP") ){
			Log.d("jiho", "activeTaskKey");
		}
	}
}
