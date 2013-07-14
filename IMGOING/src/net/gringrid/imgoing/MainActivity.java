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
import android.widget.TextView;

public class MainActivity extends Base implements OnClickListener {

	private final int REQUEST_LOGIN = 0;
	private final int REQUEST_JOIN = 1;
	
	private Intent mCurrentLocationServiceIntent = null;
	// 앱 실행시 DB instance 생성
	private DBHelper dbHelper = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("jiho", "MainActivity Oncreage : "+Preference.GCM_REGISTRATION_ID);
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
		inflateLoginInfo();
		super.onResume();
	}

	// 초기화
	public void init(){
		
    	// DB확인
    	dbHelper = DBHelper.getInstance(this);
    	if ( dbHelper == null ){
    		Log.e("jiho", "DB Error!!");
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
		
		// 로그아웃 버튼
		/*
		view = findViewById(R.id.id_bt_logout);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		*/
	}
	
	

	/**
	 * 로그인,회원가입,로그아웃,로그인 이메일 정보를 세팅한다.
	 */
	private void inflateLoginInfo(){
		// SharedPreferences에 로그인 정보가 있는지 확인
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		boolean isAutoLogin = settings.getBoolean("AUTO_LOGIN", false);
		String loginEmail = settings.getString("EMAIL", null);
		
		View id_bt_join = findViewById(R.id.id_bt_join);
		View id_bt_login = findViewById(R.id.id_bt_login);
		TextView id_tv_login_email = (TextView)findViewById(R.id.id_tv_login_email);
		//View id_bt_logout = findViewById(R.id.id_bt_logout);
		
		
		id_bt_join.setVisibility(View.GONE);
		id_bt_login.setVisibility(View.GONE);
		id_tv_login_email.setVisibility(View.GONE);
		//id_bt_logout.setVisibility(View.GONE);
		
		
		// 저장된 로그인 정보가 있는경우
		// 로그인된 이메일과 로그아웃 버튼을 보여주고 회원가입,로그인 버튼은 숨긴다.
		if ( isAutoLogin && loginEmail != null ){
			// 로그인된 이메일 주소와 로그아웃 버튼을 보여준다.
			Preference.IS_LOGIN = true;
			//id_bt_logout.setVisibility(View.VISIBLE);
			id_tv_login_email.setText(loginEmail);
			id_tv_login_email.setVisibility(View.VISIBLE);
		}else{
			// 회원가입, 로그인 버튼을 보여준다
			Preference.IS_LOGIN = false;
			id_bt_join.setVisibility(View.VISIBLE);
			id_bt_login.setVisibility(View.VISIBLE);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.d("jiho", "requestCode : "+requestCode+", resultCode : "+resultCode);
		
		switch (requestCode) {
		case REQUEST_JOIN:
			inflateLoginInfo();
			break;
		case REQUEST_LOGIN:
			if ( resultCode == RESULT_OK ){
				TextView id_tv_login_email = (TextView)findViewById(R.id.id_tv_login_email);
				id_tv_login_email.setText(data.getStringExtra("EMAIL"));
			}
			inflateLoginInfo();
			break;
		default:
			break;
		}
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		
		switch ( v.getId() ){
		case R.id.id_ll_location_control:		
			if ( Preference.IS_LOGIN == false ){
				showAlert(R.string.alert_need_login);
				return;
			}
			intent = new Intent(this, LocationControlActivity.class);
			startNewActivity(intent);			
			break;
		
		case R.id.id_ll_location_list:
			if ( Preference.IS_LOGIN == false ){
				showAlert(R.string.alert_need_login);
				return;
			}
			intent = new Intent(this, MessageActivity.class);
			startNewActivity(intent);			
			break;
			
		case R.id.id_ll_config:		
			intent = new Intent(this, ConfigActivity.class);
			startNewActivity(intent);			
			break;
			
		case R.id.id_bt_join:
			intent = new Intent(this, JoinActivity.class);
			startActivityForResult(intent, REQUEST_JOIN);
			break;
			
		case R.id.id_bt_login:
			intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, REQUEST_LOGIN);
			break;
		
		/*	
		case R.id.id_bt_logout:
			SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("AUTO_LOGIN", false);
			editor.putString("EMAIL", null);
			editor.putString("PHONE_NUMBER", null);
			editor.putString("GCM_REG_ID", null);
			editor.commit();
			showAlert("로그아웃 되었습니다.");
			inflateLoginInfo();
			break;
		*/
			
		}
	}
}
