package net.gringrid.imgoing;

import net.gringrid.imgoing.location.SendCurrentLocationService;
import net.gringrid.imgoing.vo.UserVO;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LocationControlActivity extends Activity implements OnClickListener{

	private Intent mCurrentLocationServiceIntent = null;
	private boolean CURRENT_BUTTON;
	private final boolean START = true;
	private final boolean STOP = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_control);		
		
		init();
		regEvent();
	}

	private void init() {
		CURRENT_BUTTON = START;
	}


	private void regEvent() {
		View view = findViewById(R.id.id_bt_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
	}

	private void toggleControlButton(){
		Button id_bt_control = (Button)findViewById(R.id.id_bt_control);
		
		if ( CURRENT_BUTTON == START ){
			CURRENT_BUTTON = STOP;
			id_bt_control.setText("Stop");
		}else{
			CURRENT_BUTTON = START;
			id_bt_control.setText("Start");
		}
	}
	
	/**
	 * 서비스가 수행중인지 확인한다.
	 * @return 서비스 수행여부.
	 */
	private boolean isLocationServiceRunning(){
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {			
			if (Constants.LOCATION_SERVICE.equals(service.service.getClassName())) {					
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch( v.getId() ){
		case R.id.id_bt_control:
			// 시작버튼 Click 했을 경우.
			if ( CURRENT_BUTTON == START ){
				mCurrentLocationServiceIntent = new Intent(this, SendCurrentLocationService.class);
				mCurrentLocationServiceIntent.putExtra("TEST_DATA", "TEST03923");
				this.startService(mCurrentLocationServiceIntent);
				
			// 정지버튼 Click 했을 경우 
			}else if ( CURRENT_BUTTON == STOP ){
				if ( isLocationServiceRunning() ){
					this.stopService(new Intent(this, SendCurrentLocationService.class));
				}
			}
			
			toggleControlButton();
			break;
		}
	}
}
