package net.gringrid.imgoing.location;

import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.gringrid.imgoing.Constants;
import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.MessageVO;
import net.gringrid.imgoing.vo.UserVO;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
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
	
	private static LocationUtil instance;
	
	private Context mContext;
	
	// 현재위치 관련
	private LocationManager locationManager;
	private Location location;
	private String provider;
	
	// 전송정보 
	public String start_time;	// 시작시긴
	public String receiver;		// 수신자 
	public int interval;		// 전송간격 
	
	// 현재위치 업데이트 조건 (60초에 한번씩 100m이상 이동시)
	private static final int properInterval = 1000 * 60 * 1; 	// 1분
	private static final int propertMeters = 100; 		 		// 100m
	private static final int TWO_MINUTES = 1000 * 60 * 2;		// 2분 
	
	// 현재위치 전송 시작 여부 
	private boolean isStarted = false;
	
	/**
	 * 생성자 SINGLETON
	 * @param context
	 */
	private LocationUtil(Context context){
		
		mContext = context;
		init();
		
	}

	/**
	 * LocationUtil의 instance를 반환한다. SINGLETON
	 * @param context
	 * @return instance
	 */
	public static LocationUtil getInstance(Context context){
		
		if ( instance == null ){
			instance = new LocationUtil(context);
		}
		
		return instance;
	}
	
	
	/**
	 * 초기화 ( LocationManager, Location, Provider )
	 * Location 지속적 update
	 */
	private void init(){
	
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		//Criteria criteria = new Criteria();
		
		//provider = locationManager.getBestProvider(criteria, false);
		//Log.d("jiho", "init provider : "+provider);
		
		//locationManager.requestLocationUpdates(provider, properInterval, propertMeters, this);
	}

	
	
	/**
	 * 최신 location을 얻어와 DB에 저장하고 서버로 전송한다.
	 */
	public void sendLocation(){
		
		// 중지 했다가 다시 실행할 경우 locationUpdate다시 세팅 
		if ( isStarted == false ){
			setLocationUpdater();
			isStarted = true;
		}
		
		if ( DEBUG ){
			Log.d("jiho", "==============================================");
			Log.d("jiho", "onLocationChanged provider  : "+location.getProvider());
			Log.d("jiho", "location.getLatitude() : "+location.getLatitude());
			Log.d("jiho", "location.getgetLongitude() : "+location.getLongitude());
			Log.d("jiho", "getLocationName : "+getLocationName(location.getLatitude(), location.getLongitude()));
			Log.d("jiho", "==============================================");
		}
		
		
		
		if ( location != null ){
			location = locationManager.getLastKnownLocation(location.getProvider());
			
			// DB에 저장
			MessageDao messageDAO = new MessageDao(mContext);
			MessageVO messageVO = new MessageVO();
			int resultCd = 0;
			
			messageVO.sender = Util.getMyPhoneNymber(mContext);
			messageVO.receiver = this.receiver;
			messageVO.start_time = this.start_time;
			messageVO.send_time = Util.getCurrentTime();
			messageVO.receive_time = "";
			messageVO.latitude = Double.toString(location.getLatitude());
			messageVO.longitude	= Double.toString(location.getLongitude());
			messageVO.interval = Integer.toString(interval);
			messageVO.provider = location.getProvider();
			messageVO.location_name = getLocationName(location.getLatitude(), location.getLongitude());
			messageVO.near_metro_name = "";
					
			resultCd = messageDAO.insert(messageVO);
			
			if ( resultCd == 0 ){
				Log.d("jiho", "insert success!");
			}else{
				Log.d("jiho", "[ERROR] insert fail!");
			}
			
			// 서버로 전송
			String url = "http://choijiho.com/gringrid/imgoing/imgoing.php";
	        List < NameValuePair > inputData = new ArrayList < NameValuePair > (9);
	        inputData.add(new BasicNameValuePair("mode","SEND_GCM"));
	        inputData.add(new BasicNameValuePair("sender",messageVO.sender));
	        inputData.add(new BasicNameValuePair("receiver_phone_number",receiver));
	        inputData.add(new BasicNameValuePair("start_time",messageVO.start_time));
	        inputData.add(new BasicNameValuePair("send_time",messageVO.send_time));
	        inputData.add(new BasicNameValuePair("latitude",messageVO.latitude));
	        inputData.add(new BasicNameValuePair("longitude",messageVO.longitude));
	        inputData.add(new BasicNameValuePair("interval",messageVO.interval));
	        inputData.add(new BasicNameValuePair("provider",messageVO.provider));
	        
	        JSONObject resultData = Util.requestHttp(url, inputData);
			
	        try {
	        	Log.d("jiho", "success : "+resultData.getString("success"));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Log.d("jiho", "!!!!!!!!!! Location is null !!!!!!!!!!!");
			setLocationUpdater();
		}
		
	}
	
	
	/**
	 * Geocoder 클래스의 getFromLocation이 null일경우 HTTP를 통해 얻어온다.
	 * @param lat
	 * @param lng
	 * @return
	 */
	public JSONObject requestHttp(MessageVO messageVO) {

		HttpPost httpPost = new HttpPost("http://choijiho.com/gringrid/imgoing/message_send.php");
        HttpClient client = new DefaultHttpClient();
        List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > (4);
        
        nameValuePairs.add(new BasicNameValuePair("sender", messageVO.sender));
        nameValuePairs.add(new BasicNameValuePair("receiver_phone_number", messageVO.receiver));
        nameValuePairs.add(new BasicNameValuePair("latitude", messageVO.latitude));
        nameValuePairs.add(new BasicNameValuePair("longitude", messageVO.longitude));
        
        HttpResponse response;
        
        StringBuilder stringBuilder = new StringBuilder();

        try {        	
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = client.execute(httpPost);
 
            HttpEntity entity = response.getEntity();            
            InputStream stream = entity.getContent();
            
            // 한글을 위해
            Reader reader=new InputStreamReader(stream);
         
            int b;
            while ((b = reader.read()) != -1) {
                stringBuilder.append((char) b);
            }
            Log.d("jiho", "stringBuilder : "+stringBuilder);
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
	
	/**
	 * 장소 업데이트터를 세팅한다.
	 */
	private void setLocationUpdater(){
		Log.d("jiho", "setLocationUpdater");
		if ( locationManager == null ){
			return;
		}
		
		if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER ) ){
			Log.d("jiho", "GPS Enabled");
			//criteria.setAccuracy(Criteria.ACCURACY_FINE);
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, properInterval, propertMeters, this);
		}
		if ( locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ){
			Log.d("jiho", "NETWORK Enabled");
			//criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, properInterval, propertMeters, this);
		}
		
	}
	
	
	/**
	 * 경도/위도로 장소명을 얻어온다.
	 * @param location
	 * @return 장소
	 */
	public String getLocationName(double latitude, double longitude){
		
		// 네트워크 사용 가능여부 체크 
		if ( Util.isNetworkConnectionAvailable(mContext) == false ){
			return "[Error] Network is not available.";
		}
		
		String locationName = "";
		
		if ( DEBUG ){
			Log.d("jiho", "location getLatitude : "+latitude);
			Log.d("jiho", "location getLongitude : "+longitude);
		}
		
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		List<Address> addresses = null;
		
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
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
	public void onLocationChanged(Location newLocation) {
		
		Log.d("jiho", "["+newLocation.getProvider()+"] onLocationChanged");
		
		if ( isBetterLocation(newLocation, location)){
			this.location = newLocation;
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("jiho", provider+"onStatusChanged");
	}

	@Override
	public void onProviderEnabled(String provider) {
		locationManager.removeUpdates(this);
		setLocationUpdater();		
		Log.d("jiho", provider+" onProviderEnabled");
	}

	@Override
	public void onProviderDisabled(String provider) {
		locationManager.removeUpdates(this);
		setLocationUpdater();
		Log.d("jiho", provider+" onProviderDisabled");		
	}
	
	public void stopLocationUpdate(){
		locationManager.removeUpdates(this);
		isStarted = false;
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}
	
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}
