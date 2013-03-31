package net.gringrid.imgoing;

import net.gringrid.imgoing.location.ResponseLocationServiceReceiver;
import net.gringrid.imgoing.location.SendCurrentLocationService;
import net.gringrid.imgoing.util.DBHelper;

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
	// 앱 실행시 DB instance 생성
	private DBHelper dbHelper = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		regEvent();
		setBroadCastReceiver();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	// 초기화
	public void init(){
		
    	// DB확인
    	dbHelper = DBHelper.getInstance(this);
    	if ( dbHelper == null ){
    		Log.e("jiho", "DB Error!!");
    	}
    	
		// GCM서버에 단말정보를 세팅한다.
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);		
		final String regId = GCMRegistrar.getRegistrationId(this);
		if ( regId.equals("") ){
			GCMRegistrar.register(this, "877042154251");
		}else{
			Preference.GCM_REGISTRATION_ID = regId;
			Log.d("jiho", "oncreated regId = "+regId);
			Log.d("jiho", "already registered.");
		}
	}
	
	private void regEvent() {
		
		// 위치관리 버튼
		View view = findViewById(R.id.id_bt_location_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		// 가입 버튼
		view = findViewById(R.id.id_bt_join);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		// 메시지 목록보기
		view = findViewById(R.id.id_bt_message);
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
		
		Intent intent = null;
		
		switch ( v.getId() ){
		case R.id.id_bt_location_control:		
			intent = new Intent(this, LocationControlActivity.class);
			startActivity(intent);			
			break;
			
		case R.id.id_bt_join:
			intent = new Intent(this, JoinActivity.class);
			startActivity(intent);			
			break;
			
		case R.id.id_bt_message:
			intent = new Intent(this, MessageActivity.class);
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
