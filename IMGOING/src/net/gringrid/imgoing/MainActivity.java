package net.gringrid.imgoing;

import net.gringrid.imgoing.location.ResponseLocationServiceReceiver;
import net.gringrid.imgoing.location.SendCurrentLocationService;

import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Intent mCurrentLocationServiceIntent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("jiho", "onCreate");
		
		init();
		regEvent();
		setBroadCastReceiver();
	}

	@Override
	protected void onStart() {
		Log.d("jiho", "onStart");
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.d("jiho", "onResume");
		super.onResume();
	}

	// 초기화
	public void init(){
		
		// GCM서버에 단말정보를 세팅한다.
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);		
		final String regId = GCMRegistrar.getRegistrationId(this);
		if ( regId.equals("") ){
			GCMRegistrar.register(this, "877042154251");
		}else{
			Log.d("jiho", "oncreated regId = "+regId);
			Log.d("jiho", "already registered.");
		}
	}
	
	private void regEvent() {
		View view = findViewById(R.id.id_bt_start_service);
		if ( view != null ){
			view.setOnClickListener(this);
			if ( isLocationServiceRunning() ){
				view.setClickable(false);
			}
		}
		view = findViewById(R.id.id_bt_stop_service);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_bt_join);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		
	}
	

	private void setBroadCastReceiver() {
		
		ResponseLocationServiceReceiver mReceiver = new ResponseLocationServiceReceiver();
		IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mStatusIntentFilter);
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	@Override
	public void onClick(View v) {
		
		
		switch ( v.getId() ){
		case R.id.id_bt_start_service:		
			Log.d("jiho", "id_bt_start_service clicked!!");
			
			mCurrentLocationServiceIntent = new Intent(this, SendCurrentLocationService.class);
			mCurrentLocationServiceIntent.putExtra("TEST_DATA", "TEST03923");
			this.startService(mCurrentLocationServiceIntent);
			
			v.setClickable(false);
			break;
		
		case R.id.id_bt_stop_service:		
			Log.d("jiho", "id_bt_stop_service clicked!!");			
						
			//mCurrentLocationServiceIntent.putExtra("TEST_DATA", "STOP");
			//this.stopService(mCurrentLocationServiceIntent);
			
			if ( isLocationServiceRunning() ){
				this.stopService(new Intent(this, SendCurrentLocationService.class));
			}
			
			Button id_bt_start_service = (Button)findViewById(R.id.id_bt_start_service);
			id_bt_start_service.setClickable(true);

			break;
			
		case R.id.id_bt_join:
			Intent intent = new Intent(this, JoinActivity.class);
			startActivity(intent);			
			break;
		}
	}
	
	private boolean isLocationServiceRunning(){
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {			
			if (Constants.LOCATION_SERVICE.equals(service.service.getClassName())) {					
				return true;
			}
		}
		return false;
	}

}
