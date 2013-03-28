package net.gringrid.imgoing.location;

import net.gringrid.imgoing.Constants;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SendCurrentLocationService extends IntentService {
	
	private boolean isSendLocation = false;

	
	public SendCurrentLocationService() {
		super(null);
	}
	
	public SendCurrentLocationService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Bundle mBundle = new Bundle();
		mBundle = intent.getExtras();
		isSendLocation = true;
		Log.d("jiho", "TEST_DATA : "+mBundle.get("TEST_DATA"));;
		
		while(isSendLocation){
			try {
				
				Thread.sleep(50000);
				//Vibrator vi = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);
			    //vi.vibrate(500);
			    
				Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
				.putExtra("MODE", "START");
				
				LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//LocalBroadcastManager.getInstance(getApplicationContext()
		
	}
	
	@Override
	public void onDestroy() {
		Log.d("jiho", "onDestroy");
		isSendLocation = false;
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
		.putExtra("MODE", "STOP");
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	};
	
	Handler sendBroadcastHandler = new Handler(){
		/*
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
		.putExtra("RESULT_DATA", "RESULT234022");
		
		LocalBroadcastManager.getInstance().sendBroadcast(localIntent);
		*/		
	};

}
