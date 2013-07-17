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
			
			if ( location == null )
				return;
			
			String msg = "Updated Location: " +
	                Double.toString(location.getLatitude()) + "," +
	                Double.toString(location.getLongitude());
	        
		    
			// DB에 저장
			MessageDao messageDAO = new MessageDao(context);
			MessageVO messageVO = new MessageVO();
			int resultCd = 0;
			
			messageVO.sender = Util.getMyPhoneNymber(context);
			messageVO.receiver = mBundle.getString("RECEIVER");			
			messageVO.start_time = mBundle.getString("START_TIME");						
			messageVO.latitude = Double.toString(location.getLatitude());
			messageVO.longitude	= Double.toString(location.getLongitude());
			messageVO.interval = Integer.toString(mBundle.getInt("INTERVAL"));
			messageVO.provider = location.getProvider();
			messageVO.wrk_time = Util.getCurrentTime();
			messageVO.trans_yn = "";			
					
			resultCd = messageDAO.insert(messageVO);
			
		// 위치전송 종료 	
		}else if ( mBundle.get("MODE").equals("STOP") ){
			
			locationUtil = LocationUtil.getInstance(context);
			locationUtil.stopLocationUpdate();
			
		}
	}
}
