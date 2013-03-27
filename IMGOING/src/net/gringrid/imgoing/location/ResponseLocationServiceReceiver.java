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
		// TODO Auto-generated method stub
		Bundle mBundle = new Bundle();
		mBundle = intent.getExtras();
		Log.d("jiho", "RESULT_DATA : "+mBundle.get("RESULT_DATA"));
		LocationUtil locationUtil = new LocationUtil(context);
		locationUtil.getCurrentLocation();
		/*
		LayoutInflater mInflater = LayoutInflater.from(context);
		MainActivity activity = (Activity)context;
		activity.findViewById(R.id.id_tv_hello);
		*/
		
	}

}
