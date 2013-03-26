package net.gringrid.imgoing.location;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationUtil {
	private Context mContext;
	
	public LocationUtil(Context context){
		mContext = context;
	}
	
	public void getCurrentLocation(){
		LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		MyLocListener loc;
		loc = new MyLocListener(); 
		//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loc );
		
		
		Criteria criteria = new Criteria();
		Location location = null;
		
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String providerName = lm.getBestProvider(criteria, true);
		
		Log.d("jiho", "gps is enable? : "+lm.isProviderEnabled(LocationManager.GPS_PROVIDER));
		Log.d("jiho", "network is enable? : "+lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
		
		if ( lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ){
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}else if ( lm.isProviderEnabled(LocationManager.GPS_PROVIDER ) ){
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		//location = lm.getLastKnownLocation(providerName);
		if ( location == null ){
			Toast.makeText(mContext, "GPS가 꺼져있거나 위치를 찾을 수 없습니다.",Toast.LENGTH_LONG).show();
			return;
		}
		Log.d("jiho", providerName);
		//Log.d("jiho", "location getAccuracy : "+location.getAccuracy());
		//Log.d("jiho", "location getAltitude : "+location.getAltitude());
		Log.d("jiho", "location getLatitude : "+location.getLatitude());
		Log.d("jiho", "location getLongitude : "+location.getLongitude());
		
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( addresses != null ){ 
			for ( Address adr : addresses ){
				Log.d("jiho", "adr.getAddressLine(0) : "+adr.getAddressLine(0));
				Log.d("jiho", "adr.getAdminArea() : "+adr.getAdminArea());
				Log.d("jiho", "adr.getCountryName() : "+adr.getCountryName());
				Log.d("jiho", "adr.getMaxAddressLineIndex() : "+adr.getMaxAddressLineIndex());
				Log.d("jiho", "adr.getLocality() : "+adr.getLocality());
				Log.d("jiho", "adr.getSubLocality() : "+adr.getSubLocality());
				Log.d("jiho", "adr.getUrl : "+adr.getUrl());	
			}
		}else{
			Log.d("jiho", ">>> addresses is null");
		}
		
	}
	public class MyLocListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			Log.d("jiho", "location.getLatitude() : "+location.getLatitude());
			Log.d("jiho", "location.getgetLongitude() : "+location.getLongitude());
			
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
