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
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Base implements OnClickListener {

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
    	
		// SharedPreferences에 로그인 정보가 있는지 확인
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		boolean isAutoLogin = settings.getBoolean("AUTO_LOGIN", false);
		
		// 자동로그인 설정이 되어 있으면 회원가입/로그인 버튼을 감춘다.
		if ( isAutoLogin ){
			// join 버튼 감춘다.
			View view = findViewById(R.id.id_bt_join);
			view.setVisibility(View.GONE);
			view = findViewById(R.id.id_bt_login);
			view.setVisibility(View.GONE);
		}
	}
	
	private void regEvent() {
		
		// 위치관리 버튼
		View view = findViewById(R.id.id_ll_location_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		// 메시지 목록보기
		view = findViewById(R.id.id_ll_location_list);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		// 설정
		view = findViewById(R.id.id_ll_config);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		// 가입 버튼
		view = findViewById(R.id.id_bt_join);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		// 로그인 버튼
		view = findViewById(R.id.id_bt_login);
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
		case R.id.id_ll_location_control:		
			intent = new Intent(this, LocationControlActivity.class);
			startNewActivity(intent);			
			break;
		
		case R.id.id_ll_location_list:		
			intent = new Intent(this, MessageActivity.class);
			startNewActivity(intent);			
			break;
			
		case R.id.id_ll_config:		
			intent = new Intent(this, MessageActivity.class);
			startNewActivity(intent);			
			break;
			
		case R.id.id_bt_join:
			intent = new Intent(this, JoinActivity.class);
			startNewActivity(intent);			
			break;
			
		case R.id.id_bt_login:
			intent = new Intent(this, LoginActivity.class);
			startNewActivity(intent);			
			break;
			
		}
	}
}
