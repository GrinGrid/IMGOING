package net.gringrid.imgoing;


import java.util.Vector;

import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.vo.MessageVO;

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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class MapActivity extends FragmentActivity{

	private GoogleMap mMap;
	
	// 송/수신된 위치 데이타
	Vector<MessageVO> message_data = new Vector<MessageVO>();

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
		String receiver = null;
		String start_time = null;
		
		if ( bundle != null ){
			receiver = bundle.getString("RECEIVER");
			start_time = bundle.getString("START_TIME");
			Log.d("jiho", "MapActivity receiver : "+receiver);
		
		
			MessageDao messageDao = new MessageDao(this);
			Cursor cursor = messageDao.querySendListForOne(receiver, start_time);
			
			int index_no = cursor.getColumnIndex("no");
			int index_sender = cursor.getColumnIndex("sender"); 
			int index_receiver = cursor.getColumnIndex("receiver"); 
			int index_send_time = cursor.getColumnIndex("send_time"); 
			int index_latitude = cursor.getColumnIndex("latitude");
			int index_longitude = cursor.getColumnIndex("longitude");
			int index_provider = cursor.getColumnIndex("provider");
			int index_location_name = cursor.getColumnIndex("location_name");
			
			do{
				MessageVO messageVO = new MessageVO();
				messageVO.no = cursor.getInt(index_no);
				messageVO.sender = cursor.getString(index_sender);
				messageVO.receiver = cursor.getString(index_receiver);
				messageVO.send_time = cursor.getString(index_send_time);
				messageVO.latitude = cursor.getString(index_latitude);
				messageVO.longitude = cursor.getString(index_longitude);
				messageVO.provider = cursor.getString(index_provider);
				messageVO.location_name = cursor.getString(index_location_name);
				
				message_data.add(messageVO);	
			
			}while(cursor.moveToNext());
		}else{
			Log.d("jiho", "Bundle is null!!");
		}
	}
	
	private void regEvent(){
		
	}
	
	@Override
	protected void onResume() {
		setUpMapIfNeeded();
		drawLine();
		super.onResume();
	}

	private void drawLine() {
		// TODO Auto-generated method stub
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
    			location_name = data.location_name;
    			LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    			Log.d("jiho", "["+data.provider+"] ["+latitude+"] ["+longitude+"]");
    			rectOptions.add( latLng );
    			
    	    	mMap.addMarker(new MarkerOptions()
    			.position(latLng)
    			.title(Integer.toString(markerIndex++))    			
    			.snippet("1")
    			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))    			
    			);
    			
    			
    		}
    	}
    	
    	// Get back the mutable Polyline
    	Polyline polyline = mMap.addPolyline(rectOptions);
    	moveThere(Double.parseDouble(latitude), Double.parseDouble(longitude), location_name);
	}

	
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
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
    	
    	mMap.addMarker(new MarkerOptions()
    			.position(latLng)
    			.title("마지막위치")    			
    			.snippet(location_name)
    			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))    			
    			);
    	
    }

}
