package net.gringrid.imgoing;


import java.util.Vector;

import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.vo.MessageVO;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MapActivity extends FragmentActivity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener{

	private int MESSAGE_MODE;
	private final int MESSAGE_MODE_RECEIVE = 0;		// 받은메시지
	private final int MESSAGE_MODE_SEND = 1;		// 보낸메시지 
	private ImageView mCurrentPointer;
	
	
	private String mPerson = null;
	private String mStart_time = null;
	
	private GoogleMap mMap;
	
	// 송/수신된 위치 데이타
	Vector<MessageVO> message_data = new Vector<MessageVO>();
	
	// 마지막 위치
	double mLastLatitude;
	double mLastLongitude;
	Marker mLastMarker;
	
	// 현재위치
	private Marker mCurrentLocationMaker;
	
	//current location
	Location mLocation;
	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		init();
		regEvent();
		
	}
	
	private void init(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		if ( bundle != null ){
			MESSAGE_MODE = bundle.getInt("MODE");
			mPerson = bundle.getString("PERSON");
			mStart_time = bundle.getString("START_TIME");
		}
		
		if ( MESSAGE_MODE == MESSAGE_MODE_RECEIVE ){
			viewReceiveMode();	
		}else if ( MESSAGE_MODE == MESSAGE_MODE_SEND ){
			viewSendMode();
		}
		
		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
	
	}
	
	@Override
	protected void onStart() {
		mLocationClient.connect();
		super.onStart();
	}
	
	
	@Override
	protected void onResume() {

		setUpMapIfNeeded();
		drawLine();
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}

	
	private void viewReceiveMode(){
		MessageDao messageDao = new MessageDao(this);
		Cursor cursor = messageDao.queryReceiveListForOne(mPerson, mStart_time);
		findViewById(R.id.id_ll_top_info).setVisibility(View.VISIBLE);
		
		int index_no = cursor.getColumnIndex("no");
		int index_sender = cursor.getColumnIndex("sender"); 
		int index_receiver = cursor.getColumnIndex("receiver"); 		 
		int index_latitude = cursor.getColumnIndex("latitude");
		int index_longitude = cursor.getColumnIndex("longitude");
		int index_provider = cursor.getColumnIndex("provider");
		int index_wrk_time = cursor.getColumnIndex("wrk_time");
		int index_trans_yn = cursor.getColumnIndex("trans_yn");
		
		do{
			MessageVO messageVO = new MessageVO();
			messageVO.no = cursor.getInt(index_no);
			messageVO.sender = cursor.getString(index_sender);
			messageVO.receiver = cursor.getString(index_receiver);			
			messageVO.latitude = cursor.getString(index_latitude);
			messageVO.longitude = cursor.getString(index_longitude);
			messageVO.provider = cursor.getString(index_provider);
			messageVO.wrk_time = cursor.getString(index_wrk_time);
			messageVO.trans_yn = cursor.getString(index_trans_yn);
			
			message_data.add(messageVO);	
		
		}while(cursor.moveToNext());
	}
	
	
	
	private void viewSendMode(){
		MessageDao messageDao = new MessageDao(this);
		Cursor cursor = messageDao.querySendListForOne(mPerson, mStart_time);
		findViewById(R.id.id_ll_top_info).setVisibility(View.GONE);
		
		int index_no = cursor.getColumnIndex("no");
		int index_sender = cursor.getColumnIndex("sender"); 
		int index_receiver = cursor.getColumnIndex("receiver"); 		 
		int index_latitude = cursor.getColumnIndex("latitude");
		int index_longitude = cursor.getColumnIndex("longitude");
		int index_provider = cursor.getColumnIndex("provider");
		int index_wrk_time = cursor.getColumnIndex("wrk_time");
		int index_trans_yn = cursor.getColumnIndex("trans_yn");
		
		do{
			MessageVO messageVO = new MessageVO();
			messageVO.no = cursor.getInt(index_no);
			messageVO.sender = cursor.getString(index_sender);
			messageVO.receiver = cursor.getString(index_receiver);
			
			messageVO.latitude = cursor.getString(index_latitude);
			messageVO.longitude = cursor.getString(index_longitude);
			messageVO.provider = cursor.getString(index_provider);
			messageVO.wrk_time = cursor.getString(index_wrk_time);
			messageVO.trans_yn = cursor.getString(index_trans_yn);
			
			message_data.add(messageVO);	
		
		}while(cursor.moveToNext());
	}
	
	
	
	
	private void regEvent(){
		
	}
	
	private void drawLine() {
		// Instantiates a new Polyline object and adds points to define a rectangle
    	PolylineOptions rectOptions = new PolylineOptions();
    	String latitude = "";
    	String longitude = "";
    	String location_name = "";
    	int markerIndex = 1;
    	
    	for ( MessageVO data : message_data ){
    		if ( data.latitude.equals(latitude) == true && 
    			 data.longitude.equals(longitude) == true	){
    			continue;
    		}else{
    			latitude = data.latitude;
    			longitude = data.longitude;    			
    			LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    			rectOptions.add( latLng );
    			rectOptions.color(R.color.imgoing_sky);
    			rectOptions.geodesic(true);
    			
    			String snippet = null;
    			
    			if ( MESSAGE_MODE == MESSAGE_MODE_RECEIVE ){
    				snippet = "수신시간 : "+data.wrk_time;
    			}else if ( MESSAGE_MODE == MESSAGE_MODE_SEND ){
    				snippet = "송신시간 : "+data.wrk_time;
    			}
    			
    			
    			Marker marker = mMap.addMarker(new MarkerOptions()
    			.position(latLng)
    			.title(Integer.toString(markerIndex++))    			
    			.snippet(snippet)
    			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))    			
    			);
    			marker.showInfoWindow();
    			mLastMarker = marker;	
    			
    		}
    	}
    	
    	mLastLatitude = Double.parseDouble(latitude);
    	mLastLongitude = Double.parseDouble(longitude);
    	
    	 
    	// Get back the mutable Polyline
    	Polyline polyline = mMap.addPolyline(rectOptions);
    	
    	moveThere(mLastLatitude, mLastLongitude, location_name);
	}

	
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
            	mMap.getUiSettings().setCompassEnabled(false);
            	mMap.getUiSettings().setRotateGesturesEnabled(false);
            	//setUpMap();
            }
        }
    	mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Log.d("Test", "marker.getId() = "+marker.getId());
				return false;
			}
		});	
    }

	
	
	/**
	 * 마지막 위치로 이동하고 마커 생
	 * @param lat : 위도 
	 * @param lng : 경도 
	 * @param location_name : 위치명 
	 */
    private void moveThere(double lat, double lng, String location_name){
    	LatLng latLng = new LatLng(lat, lng);
    	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    	/*
    	mMap.addMarker(new MarkerOptions()
    			.position(latLng)
    			.title("마지막위치")    			
    			.snippet(location_name)
    			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))    			
    			);
    	*/
    	
    }

	@Override
	public void onLocationChanged(Location location) {
		
		if ( location != null ){
			mLocation = location;

			if ( MESSAGE_MODE == MESSAGE_MODE_RECEIVE ){
				float[] resultArray = new float[99];
			    Location.distanceBetween(mLastLatitude, mLastLongitude, location.getLatitude(), location.getLongitude(), resultArray);
			    String unit = null;
			    float distance = 0;
			    if ( resultArray[0] > 1000 ){
			    	distance = resultArray[0] / 1000;
			    	unit = "km";
			    }else{
			    	distance = resultArray[0];
			    	unit = "m";
			    }
			    
			    TextView id_tv_distance = (TextView)findViewById(R.id.id_tv_distance);
			    id_tv_distance.setText(String.format("%.2f", distance)+unit);
			    mLastMarker.setTitle("내 위치와의 거리 : "+String.format("%.2f", distance)+unit);
			    mLastMarker.showInfoWindow();
			}
			
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			if ( mCurrentLocationMaker == null ){
				mCurrentLocationMaker = mMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("현재위치")    			
				.snippet("")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_indicator_current_position))			    			
				);
			}else{
				mCurrentLocationMaker.setPosition(latLng);
			}
			mCurrentLocationMaker.showInfoWindow();
			//ic_maps_indicator_current_position
			/*
			int latitude = (int) (location.getLatitude() * 1e6);
		    int longitude = (int) (location.getLongitude() * 1e6);
		    
		    MapView.LayoutParams lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, 
		            MapView.LayoutParams.WRAP_CONTENT, new GeoPoint(latitude, longitude),
		            MapView.LayoutParams.CENTER);
		    
		    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		    
		    if (mCurrentPointer == null) {
		        // If "current location" pin is null, we haven't added it
		        // to MapView yet. So instantiate it and add it to MapView:
		        mCurrentPointer = new ImageView(this); 
		        mCurrentPointer.setImageResource(R.drawable.friend_icon);
		        RelativeLayout root = (RelativeLayout)findViewById(R.id.root);
		        root.addView(mCurrentPointer, lp);
		    } else {
		        // If it's already added, just update its location
		        mCurrentPointer.setLayoutParams(lp);
		    }
		    */
		    
			
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocation = mLocationClient.getLastLocation();
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

}
