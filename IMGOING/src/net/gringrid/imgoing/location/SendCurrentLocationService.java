/**
 * File : SendCurrentLocationService.class
 * Description : 5초 간격으로 3번 조회해서 location 값이 null이 아니면 DB insert하고 서버보내고 정상응답오면 서비스 종료
 * 1. 5초 간격으로 location을 3번 조회
 * 2. location이 null이 아니면 DB에 insert
 * 3. 서버로 데이타 전송
 * 4. 정상응답 받으면 서비스 종료 
 */
package net.gringrid.imgoing.location;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import net.gringrid.imgoing.Constants;
import net.gringrid.imgoing.Preference;
import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.MessageVO;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class SendCurrentLocationService extends Service implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener{
	
	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	Location mLocation;
	
	private int mUpdateCount;
	
	private MessageVO mMessageVO;
	//private WakeLock wl;
	
	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 5;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
    // screen 
    private BroadcastReceiver mScreenReceiver;
    
	@Override
	public void onCreate() {
		/*
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
	    wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "IMGOING");
	    wl.acquire();
	    */
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.setPriority(Integer.MAX_VALUE);
        mScreenReceiver = new ScreenReceiver();
        registerReceiver(mScreenReceiver, filter);
        
		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		super.onCreate();
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		mLocationClient.connect();
		super.onStart(intent, startId);
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("jiho", "call onStartCommand");
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();
		mMessageVO = (MessageVO)bundle.getParcelable("MESSAGEVO");
		
		
		// Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		
        /*
        do{
        	try {
        		
				//Thread.sleep(20000);
				//isSendLocation = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }while( mIsContinue );
        */
        
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		Log.d("jiho", "onDestroy");
		Log.d("jiho", "****************************************/");
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("LATITUDE", null);
		editor.putString("LONGITUDE", null);
		editor.putString("PROVIDER", null);
		editor.commit();
	
		unregisterReceiver(mScreenReceiver);
		
		/*
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
		.putExtra("MODE", "STOP");
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
		*/
		mLocationClient.disconnect();
		//wl.release();
	}
	
	public class ScreenReceiver extends BroadcastReceiver {

		@Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	            Log.d("jiho", "ACTION_SCREEN_OFF");
	            abortBroadcast();
	        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	        	Log.d("jiho", "ACTION_SCREEN_ON");
	        }
	        
	    }
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d("jiho", "onConnectionFailed");	
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocation = mLocationClient.getLastLocation();
		if ( mLocation !=null ){
			//Log.d("jiho", "Latitude : "+mLocation.getLatitude());
			//Log.d("jiho", "Longitude : "+mLocation.getLongitude());
		}else{
			Log.d("jiho", "onConnected location is null");
			
		}
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		if ( location != null ){
			Log.d("jiho", "onLocationChanged Location is not null. ["+location.getLatitude()+"] ["+location.getLongitude()+"]");
			// 배터리 절약 모드일경우 : update 2번하고 insert
			if ( Preference.SETTING_LOCATION_SEARCH == Preference.SETTING_LOCATION_SEARCH_BATTERY ){
				mUpdateCount++;
				
				if ( mUpdateCount >= 2 ){
					if ( insertMessage(location) == true ){
						if ( sendServer() == true ){
							this.stopSelf();
						}
					}
				}
			// 정확도 우선일경우 환경변수에 현재위치 저장	
			}else if ( Preference.SETTING_LOCATION_SEARCH == Preference.SETTING_LOCATION_SEARCH_ACCURATE ){
				SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("LATITUDE", Double.toString(location.getLatitude()));
				editor.putString("LONGITUDE", Double.toString(location.getLongitude()));
				editor.putString("PROVIDER", location.getProvider());
				editor.commit();
				Log.d("jiho", "SharedPreferences saved : ["+location.getLatitude()+"] ["+location.getLongitude()+"]");
			}
		}else{
			Log.d("jiho", "onLocationChanged location null ):");
		}
	}
	
	
	private boolean insertMessage(Location location){
		boolean result = false;
		
		// DB에 저장
		MessageDao messageDAO = new MessageDao(this);
		MessageVO messageVO = new MessageVO();
		int resultCd = 0;
		
		messageVO.sender = Util.getMyPhoneNymber(this);
		messageVO.receiver = mMessageVO.receiver;
		messageVO.start_time = mMessageVO.start_time;
		messageVO.latitude = Double.toString(location.getLatitude());
		messageVO.longitude	= Double.toString(location.getLongitude());
		messageVO.interval = mMessageVO.interval;
		messageVO.provider = location.getProvider();
		messageVO.wrk_time = Util.getCurrentTime();
		messageVO.trans_yn = "";		
				
		resultCd = messageDAO.insert(messageVO);
		
		if ( resultCd == 0 ){
			Log.d("jiho", "insert success! provider : "+location.getProvider());
			result = true;
		}else{
			Log.d("jiho", "[ERROR] insert fail!");
			result = false;
		}
		return result;
	}
	
	private boolean sendServer(){
		boolean result = false;
		result = true;
		Log.d("jiho", "sendServer Success!!");
		return result;
	}
	
	
	private void sendBroadCast(){
		// IMGOING앱만 받을 수 있도록 broadcasting 한다.
		// BroadcastReceiver 에서 하는일
		// 1. DB insert
		// 2. Server insert
		// 3. 화면이 열려있을경우 화면 갱신
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
		localIntent.putExtra("START_TIME", mMessageVO.start_time);
		localIntent.putExtra("RECEIVER", mMessageVO.receiver);
		localIntent.putExtra("INTERVAL", mMessageVO.interval);
		localIntent.putExtra("MODE", "START");
		localIntent.putExtra("LOCATION", mLocation);
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);	
	}
	
	@Override
	public void onDisconnected() {
		Log.d("jiho", "onDisconnected");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	};


}

