package net.gringrid.imgoing.location;

import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Locale;

import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.vo.MessageVO;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

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

public class LocationUtil implements LocationListener{
	
	private final boolean DEBUG = false;
	
	private Context mContext;
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private static LocationUtil instance;
	
	
	private String currentLocationName;
	
	
	private LocationUtil(Context context){
		Log.d("jiho", "************ CONSTRUCTOR");
		mContext = context;
		init();
		getCurrentLocation();
		//locationManager.requestLocationUpdates(provider, 5000, 0, this);
	}
	 
	public static LocationUtil getInstance(Context context){
		Log.d("jiho", "************ getInstance()");
		if ( instance == null ){
			instance = new LocationUtil(context);
		}else{
			instance.init();
			instance.getCurrentLocation();
			instance.locationManager.requestLocationUpdates(instance.provider, 10000, 0, instance);
		}
		return instance;
	}
	
	private void init(){
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		
		// 우선순위 GPS > NETWORK
		if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER ) ){
			Log.d("jiho", "GPS Enabled");
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}else if ( locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ){
			Log.d("jiho", "NETWORK Enabled");
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		
		provider = locationManager.getBestProvider(criteria, false);
		Log.d("jiho", "init provider : "+provider);
		
	}
	
	
	public void getCurrentLocation(){
		
		if ( location == null ){
			Toast.makeText(mContext, "GPS가 꺼져있거나 위치를 찾을 수 없습니다.",Toast.LENGTH_LONG).show();
			return;
		}else{
			//onLocationChanged(location);
			//currentLocationName = getLocationName(location);
			//Log.d("jiho", "currentLocationName : "+currentLocationName);
		}
	}
	
	/**
	 * 경도/위도로 장소명을 얻어온다.
	 * @param location
	 * @return
	 */
	public String getLocationName(Location location){
		String locationName = "";
		
		double latitude;
		double longitude;
		
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		
		if ( DEBUG ){
			Log.d("jiho", "location getLatitude : "+latitude);
			Log.d("jiho", "location getLongitude : "+longitude);
		}
		/*
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
				if ( DEBUG ){
					Log.d("jiho", "adr.getAddressLine(0) : "+adr.getAddressLine(0));
					Log.d("jiho", "adr.getAdminArea() : "+adr.getAdminArea());
					Log.d("jiho", "adr.getCountryName() : "+adr.getCountryName());
					Log.d("jiho", "adr.getMaxAddressLineIndex() : "+adr.getMaxAddressLineIndex());
					Log.d("jiho", "adr.getLocality() : "+adr.getLocality());
					Log.d("jiho", "adr.getSubLocality() : "+adr.getSubLocality());
					Log.d("jiho", "adr.getUrl : "+adr.getUrl());
				}
				locationName = adr.getAddressLine(0);
				Toast.makeText(mContext, "Adr not null, "+adr.getAddressLine(0),Toast.LENGTH_SHORT).show();
			}
		}else{
			Log.d("jiho", ">>> addresses is null");
		}
		
		if ( addresses == null ){
			JSONObject ret = getLocationInfo(latitude, longitude); 
			JSONObject retLocation;
			String location_string;
			try {
				retLocation = ret.getJSONArray("results").getJSONObject(0);
			    location_string = retLocation.getString("formatted_address");
			    locationName = location_string;
			    Toast.makeText(mContext, "Adr null : "+location_string,Toast.LENGTH_SHORT).show();
			    Log.d("jiho", "Adr null, "+"formattted address:" + location_string);
			} catch (JSONException e1) {
			    e1.printStackTrace();

			}
		}
		
		*/
		return locationName;
	}
	
	/**
	 * Geocoder 클래스의 getFromLocation이 null일경우 HTTP를 통해 얻어온다.
	 * @param lat
	 * @param lng
	 * @return
	 */
	public JSONObject getLocationInfo(double lat, double lng) {

		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=true&language=ko");
        HttpClient client = new DefaultHttpClient();
        
        HttpResponse response;
        
        StringBuilder stringBuilder = new StringBuilder();

        try {        	
            response = client.execute(httpGet);
 
            HttpEntity entity = response.getEntity();            
            InputStream stream = entity.getContent();
            
            // 한글을 위해
            Reader reader=new InputStreamReader(stream);
         
            int b;
            while ((b = reader.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }	
	
	
	@Override
	public void onLocationChanged(Location location) {
		Log.d("jiho", "onLocationChanged");
		if ( DEBUG ){
			Log.d("jiho", "==============================================");
			Log.d("jiho", "onLocationChanged provider  : "+provider);
			Log.d("jiho", "location.getLatitude() : "+location.getLatitude());
			Log.d("jiho", "location.getgetLongitude() : "+location.getLongitude());
			Log.d("jiho", "getLocationName : "+getLocationName(location));
			Log.d("jiho", "==============================================");
		}
		if ( location != null ){
			MessageDao messageDAO = new MessageDao(mContext);
			MessageVO messageVO = new MessageVO();
			
			messageVO.sender = "nisdlan@hotmail.com";
			messageVO.receiver = "grigrng@gmail.com";
			messageVO.send_time = "";
			messageVO.receive_time = "";
			messageVO.latitude = Double.toString(location.getLatitude());
			messageVO.longitude	= Double.toString(location.getLongitude());
			messageVO.interval = "";
			messageVO.provider = provider;
			messageVO.location_name = getLocationName(location);
			messageVO.near_metro_name = "";
					
			messageDAO.insert(messageVO);
		}
		stopUpdate();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("jiho", "onStatusChanged");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("jiho", "onProviderEnabled");		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("jiho", "onProviderEnabled");		
	}
	
	public void stopUpdate() {
		locationManager.removeUpdates(this);
	}
}
