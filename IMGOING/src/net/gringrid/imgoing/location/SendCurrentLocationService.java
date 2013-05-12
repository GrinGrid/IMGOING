package net.gringrid.imgoing.location;

import java.sql.Date;
import java.text.SimpleDateFormat;

import net.gringrid.imgoing.Constants;
import net.gringrid.imgoing.util.Util;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SendCurrentLocationService extends IntentService {
	
	private boolean isSendLocation = false;
	private String receiver;
	private String receiver_id;
	private int interval;
	
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
		receiver = mBundle.getString("RECEIVER");
		receiver_id = mBundle.getString("RECEIVER_ID");
		interval = mBundle.getInt("INTERVAL");
		
		String start_time = Util.getCurrentTime();
		
		Log.d("jiho", "INTERVAL : "+mBundle.getInt("INTERVAL"));;
		
		while( isSendLocation ){
			try {
				
				//Vibrator vi = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);
			    //vi.vibrate(500);
			    
				// IMGOING앱만 받을 수 있도록 broadcasting 한다.
				Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
				localIntent.putExtra("START_TIME", start_time);
				localIntent.putExtra("RECEIVER", receiver);
				localIntent.putExtra("RECEIVER_ID", receiver_id);
				localIntent.putExtra("INTERVAL", interval);
				localIntent.putExtra("MODE", "START");
				LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
				Log.d("jiho", "execute service interval : "+interval);
					
				// 사용자가 요청한 전송간격만큰 시간을 둔다.
				//Thread.sleep(1000 * 60 * interval);
				Thread.sleep(5000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//LocalBroadcastManager.getInstance(getApplicationContext()
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		Log.d("jiho", "onDestroy");
		isSendLocation = false;
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
		.putExtra("MODE", "STOP");
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	};
	
}
