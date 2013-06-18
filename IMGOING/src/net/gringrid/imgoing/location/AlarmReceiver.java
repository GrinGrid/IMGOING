package net.gringrid.imgoing.location;

import net.gringrid.imgoing.Constants;
import net.gringrid.imgoing.Preference;
import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.MessageVO;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	public static String ACTION_ALARM = "com.alarammanager.alaram";
	private Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		Log.d("jiho", "/**************************************");
		Log.d("jiho", "AlarmReceiver onReceive");
		Bundle bundle = intent.getExtras();
		MessageVO messageVO = (MessageVO)bundle.getParcelable("MESSAGEVO");
		
		// 서비스가 실행중이면 마지막위치 가져와서 DB Insert
		if ( isLocationServiceRunning() ){
			Log.d("jiho", "Service is running..");
			Log.d("jiho", "RECEIVER : "+messageVO.receiver);
			Log.d("jiho", "RECEIVER_ID : "+messageVO.receiver_id);
			Log.d("jiho", "INTERVAL : "+messageVO.interval);
			Log.d("jiho", "START_TIME : "+messageVO.start_time);
			
			// 실행중일 경우 마지막 location 받아와서 처리
			MessageDao messageDAO = new MessageDao(mContext);
			int resultCd = 0;
			
			SharedPreferences settings = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			String latitude = settings.getString("LATITUDE", null);
			String longitude = settings.getString("LONGITUDE", null);
			String provider = settings.getString("PROVIDER", null);
			
			
			
			if ( latitude != null && longitude != null && provider !=null ){
				messageVO.sender = Util.getMyPhoneNymber(mContext);
				messageVO.send_time = Util.getCurrentTime();
				messageVO.receive_time = "";
				messageVO.latitude = latitude;
				messageVO.longitude	= longitude;
				messageVO.provider = provider;
				messageVO.location_name = "";
				messageVO.near_metro_name = "";
				resultCd = messageDAO.insert(messageVO);		
				
				if ( resultCd == 0 ){
					Log.d("jiho", "insert success! provider : "+provider);
					
					editor.putString("LATITUDE", null);
					editor.putString("LONGITUDE", null);
					editor.putString("PROVIDER", null);
					editor.commit();
				}else{
					Log.d("jiho", "[ERROR] insert fail!");
				}
				
			}else{
				Log.d("jiho", "AlarmReceiver location is null");
			}
			
		// 실행중이 아닐경우 서비스 호출	
		}else{
			Log.d("jiho", "Service is not running..");
			Intent intentService = new Intent(context, SendCurrentLocationService.class);
			intentService.putExtras(bundle);
			context.startService(intentService);
		}
		
		
		
 
	}
	
	
	private boolean isLocationServiceRunning(){
		
		ActivityManager manager = (ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {			
			if (Constants.LOCATION_SERVICE.equals(service.service.getClassName())) {					
				return true;
			}
		}
		return false;
	}
	
}
