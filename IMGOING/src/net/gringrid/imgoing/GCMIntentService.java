package net.gringrid.imgoing;


import java.util.Iterator;

import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.location.LocationUtil;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.MessageVO;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{
	
	String[] mSenderIds = null;
	
	public GCMIntentService() {
        //super(Preference.GCM_REGISTRATION_ID);
	}
	
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
		messageVO.start_time = bundle.get("start_time").toString();				
		messageVO.latitude = bundle.get("latitude").toString();
		messageVO.longitude	= bundle.get("longitude").toString();
		messageVO.interval = bundle.get("interval").toString();;
		messageVO.provider = "";
		messageVO.wrk_time = bundle.get("wrk_time").toString();;
		messageVO.trans_yn = "";
				
		resultCd = messageDAO.insert(messageVO);
		
		if ( resultCd == 0 ){
			Log.d("jiho", "insert success!");
			// TODO Notification
			
			Intent resultIntent = new Intent(this, IntroActivity.class);
			resultIntent.putExtra("IS_FROM_RECEIVE_LOCATION", true);
			// 앱 실행하고 다른 메뉴로 이동후 noti 클릭하면 새로 앱을 띄움
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			PendingIntent notifyIntent =
			        PendingIntent.getActivity(
			        this,
			        0,
			        resultIntent,
			        PendingIntent.FLAG_UPDATE_CURRENT
			);
			
			
			NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this);
			notiBuilder.setSmallIcon(R.drawable.ic_launcher);
			notiBuilder.setContentTitle("I'm Going");
			notiBuilder.setContentText("Last : "+messageVO.wrk_time);
			notiBuilder.setContentIntent(notifyIntent);
			notiBuilder.setNumber((int)messageDAO.queryReceiveOneRouteCount());
			
			Notification notification = notiBuilder.build();
			notification.ledARGB = 1;
			
			int notificationID = Integer.parseInt(messageVO.start_time.substring(messageVO.start_time.length()-8).replace(":", ""));
			Log.d("jiho", "notificationID : "+notificationID);
			
			NotificationManager notificationManager = null;
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(notificationID, notiBuilder.build());
			
			
			
		}else{
			Log.d("jiho", "[ERROR] insert fail!");
		}
		
		
	}
	

	@Override
	protected void onRegistered(Context arg0, String regId) {
		Preference.GCM_REGISTRATION_ID = regId;
		Log.d("jiho", "onRegistered - regId : "+regId);
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		
	}

}
