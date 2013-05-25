package net.gringrid.imgoing.location;

import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.MessageVO;
import android.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

public class ResponseLocationServiceReceiver extends BroadcastReceiver {

	private LocationUtil locationUtil;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle mBundle = new Bundle();
		mBundle = intent.getExtras();
		
		// 위치전송 시작 
		if ( mBundle.get("MODE").equals("START") ){
			
			Location location = (Location)mBundle.get("LOCATION");
			
			Log.d("jiho", "onReceive");
			
			if ( location == null )
				return;
			
			String msg = "Updated Location: " +
	                Double.toString(location.getLatitude()) + "," +
	                Double.toString(location.getLongitude());
	        Log.d("jiho", msg);
		    
			// DB에 저장
			MessageDao messageDAO = new MessageDao(context);
			MessageVO messageVO = new MessageVO();
			int resultCd = 0;
			
			messageVO.sender = Util.getMyPhoneNymber(context);
			messageVO.receiver = mBundle.getString("RECEIVER");
			messageVO.receiver_id = mBundle.getString("RECEIVER_ID");
			messageVO.start_time = mBundle.getString("START_TIME");
			messageVO.send_time = Util.getCurrentTime();
			messageVO.receive_time = "";
			messageVO.latitude = Double.toString(location.getLatitude());
			messageVO.longitude	= Double.toString(location.getLongitude());
			messageVO.interval = Integer.toString(mBundle.getInt("INTERVAL"));
			messageVO.provider = location.getProvider();
			//messageVO.location_name = getLocationName(location.getLatitude(), location.getLongitude());
			messageVO.location_name = "";
			messageVO.near_metro_name = "";
					
			resultCd = messageDAO.insert(messageVO);
			
			if ( resultCd == 0 ){
				Log.d("jiho", "insert success!");
			}else{
				Log.d("jiho", "[ERROR] insert fail!");
			}
			
			
			
			
			/*
			locationUtil = LocationUtil.getInstance(context);
			locationUtil.start_time = mBundle.getString("START_TIME");
			locationUtil.receiver = mBundle.getString("RECEIVER");
			locationUtil.receiver_id = mBundle.getString("RECEIVER_ID");
			locationUtil.interval = mBundle.getInt("INTERVAL"); 
			locationUtil.sendLocation();
			*/
			
		// 위치전송 종료 	
		}else if ( mBundle.get("MODE").equals("STOP") ){
			
			locationUtil = LocationUtil.getInstance(context);
			locationUtil.stopLocationUpdate();
			Log.d("jiho", "STOP");
			
		}
	}
}
