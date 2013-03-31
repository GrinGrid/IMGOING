package net.gringrid.imgoing;


import java.util.Iterator;

import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.location.LocationUtil;
import net.gringrid.imgoing.location.Util;
import net.gringrid.imgoing.vo.MessageVO;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{
	
	public GCMIntentService(){} 
	
	// registration or unregistration 에러가 날 경우 호출
	@Override
	protected void onError(Context context, String errorId) {
		Log.d("jiho", "errorId : "+errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		
		//Vibrator vi = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	    //vi.vibrate(500);
		
	    Bundle bundle = intent.getExtras();
		 
		Iterator <String> iterator = bundle.keySet().iterator();
		while ( iterator.hasNext() ){
			String key = iterator.next();
			String value = bundle.get(key).toString();
			Log.d("jiho", "key : "+key+", value : "+value);
		}
		
		// 장소명을 가져오기 위해
		double latitude = Double.parseDouble(bundle.get("latitude").toString());
		double longitude = Double.parseDouble(bundle.get("longitude").toString());
		LocationUtil locationUtil = LocationUtil.getInstance(context);
		
		
		// DB에 저장
		MessageDao messageDAO = new MessageDao(context);
		MessageVO messageVO = new MessageVO();
		int resultCd = 0;
		
		messageVO.sender = bundle.get("sender").toString();
		messageVO.receiver = Util.getMyPhoneNymber(context);
		messageVO.send_time = "";
		messageVO.receive_time = "";
		messageVO.latitude = bundle.get("latitude").toString();
		messageVO.longitude	= bundle.get("longitude").toString();
		messageVO.interval = "";
		messageVO.provider = "";
		messageVO.location_name = locationUtil.getLocationName(latitude, longitude);
		messageVO.near_metro_name = "";
				
		resultCd = messageDAO.insert(messageVO);
		
		if ( resultCd == 0 ){
			Log.d("jiho", "insert success!");
		}else{
			Log.d("jiho", "[ERROR] insert fail!");
		}
		
		
	}

	@Override
	protected void onRegistered(Context arg0, String regId) {
		Log.d("jiho", "onRegistered - regId : "+regId);
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		
	}

}
