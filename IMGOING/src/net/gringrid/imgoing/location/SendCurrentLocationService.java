package net.gringrid.imgoing.location;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import net.gringrid.imgoing.Constants;
import net.gringrid.imgoing.util.Util;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class SendCurrentLocationService extends IntentService implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener{
	
	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	Location mLocation;
	
	private boolean isSendLocation = false;
	private String receiver;
	private String receiver_id;
	private int interval;
	private String mStartTime;
	
	
	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    
    
	public SendCurrentLocationService() {
		super(null);
		Log.d("jiho", "SendCurrentLocationService");
		
	}
	
	public SendCurrentLocationService(String name) {
		super(name);
		Log.d("jiho", "SendCurrentLocationService(String name)");
	}
	
	@Override
	public void onCreate() {
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
	protected void onHandleIntent(Intent intent) {
		
		Bundle mBundle = new Bundle();
		mBundle = intent.getExtras();
		isSendLocation = true;
		receiver = mBundle.getString("RECEIVER");
		receiver_id = mBundle.getString("RECEIVER_ID");
		interval = mBundle.getInt("INTERVAL");
		
		mStartTime = Util.getCurrentTime();
		
		Log.d("jiho", "INTERVAL : "+mBundle.getInt("INTERVAL"));;
		
		
		// Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		
		
		
		while( isSendLocation ){
			/*
			try {
				
				//Vibrator vi = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);
			    //vi.vibrate(500);
			    /*
				// IMGOING앱만 받을 수 있도록 broadcasting 한다.
				// BroadcastReceiver 에서 하는일
				// 1. DB insert
				// 2. Server insert
				// 3. 화면이 열려있을경우 화면 갱신
				Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
				localIntent.putExtra("START_TIME", start_time);
				localIntent.putExtra("RECEIVER", receiver);
				localIntent.putExtra("RECEIVER_ID", receiver_id);
				localIntent.putExtra("INTERVAL", interval);
				localIntent.putExtra("MODE", "START");
				localIntent.putExtra("LOCATION", mLocation);
				LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
				Log.d("jiho", "execute service interval : "+interval);
					
				// 사용자가 요청한 전송간격만큰 시간을 둔다.
				//Thread.sleep(1000 * 60 * interval);
				
				//Thread.sleep(5000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
		
		//LocalBroadcastManager.getInstance(getApplicationContext()
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		Log.d("jiho", "onDestroy");
		isSendLocation = false;
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
		.putExtra("MODE", "STOP");
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
		mLocationClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d("jiho", "onConnectionFailed");	
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocation = mLocationClient.getLastLocation();
		if ( mLocation !=null ){
			Log.d("jiho", "Latitude : "+mLocation.getLatitude());
			Log.d("jiho", "Longitude : "+mLocation.getLongitude());
		}
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		Log.d("jiho", "onConnected");
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if ( location != null ){
			mLocation = location;
			String msg = "Updated Location: " +
	                Double.toString(location.getLatitude()) + "," +
	                Double.toString(location.getLongitude());
	        //Log.d("jiho", msg);
			//Log.d("jiho", getLocationName(location.getLatitude(), location.getLongitude()));
	        sendBroadCast();
		}else{
			Log.d("jiho", "onLocationChanged location numm ):");
		}
	}
	
	private void sendBroadCast(){
		// IMGOING앱만 받을 수 있도록 broadcasting 한다.
		// BroadcastReceiver 에서 하는일
		// 1. DB insert
		// 2. Server insert
		// 3. 화면이 열려있을경우 화면 갱신
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
		localIntent.putExtra("START_TIME", mStartTime);
		localIntent.putExtra("RECEIVER", receiver);
		localIntent.putExtra("RECEIVER_ID", receiver_id);
		localIntent.putExtra("INTERVAL", interval);
		localIntent.putExtra("MODE", "START");
		localIntent.putExtra("LOCATION", mLocation);
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);	
	}
	
	@Override
	public void onDisconnected() {
		Log.d("jiho", "onDisconnected");
	};
	
}

