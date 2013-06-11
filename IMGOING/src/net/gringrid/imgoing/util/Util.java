package net.gringrid.imgoing.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.gringrid.imgoing.Constants;
import net.gringrid.imgoing.Preference;
import net.gringrid.imgoing.vo.ContactsVO;
import net.gringrid.imgoing.vo.UserVO;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Util {

	// added as an instance method to an Activity
	public static boolean isNetworkConnectionAvailable(Context context) {  
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info = cm.getActiveNetworkInfo();     
	    if (info == null) return false;
	    State network = info.getState();
	    return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
	}
	
	
	
	/**
	 * 단말기의 전화번호를 얻어온다. 
	 * @param context
	 * @return 단말기 전화번호
	 */
	public static String getMyPhoneNymber(Context context){
		
		String phone_number;
		
		if ( Preference.PHONE_NUMBER == null ){
			TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			phone_number = tMgr.getLine1Number();
			if ( phone_number != null ){
				// KT일경우 +8210xxxxxxx 형식으로 리턴되므로 처리
				phone_number = phone_number.replace("+82", "0");
			}else{
				phone_number = null;
			}
			Preference.PHONE_NUMBER = phone_number;
		}else{
			phone_number = Preference.PHONE_NUMBER;
		}
		
		return phone_number;
	}
	
	
	/**
	 * 주소록을 세팅한다.
	 * @param mContext
	 */
	public static void setContacts(Context mContext){
		// 초기화
		Preference.CONTACTS_LIST = new Vector<ContactsVO>();
		
		String projection[] = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
		
	    Uri uri = ContactsContract.Contacts.CONTENT_URI;

	    String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1" + 
	    " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " =1";

	    String order = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	    
	    CursorLoader loader = new CursorLoader(mContext, uri, projection, selection, null, order);
	    Cursor cursor = loader.loadInBackground();
	    
	    int id_idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
	    int id_name = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	    String id_value = null;
	    String name_value = null;
	    
	    while ( cursor.moveToNext() ){
	    	ContactsVO contacts = new ContactsVO();
	    	id_value = cursor.getString( id_idx );
	    	name_value = cursor.getString( id_name );
	    	contacts.id = id_value;
	    	contacts.name = name_value;
	    	Preference.CONTACTS_LIST.add(contacts);
	    }
	    
	    cursor.close();
	    /*
	    for ( ContactsVO vo : Preference.CONTACTS_LIST ){
	    	Log.d("jiho", "id : "+vo.id);
	    	Log.d("jiho", "name : "+vo.name);
	    	Log.d("jiho", "phoneNumber : "+vo.phoneNumber);
	    }
	    */
	}
	
	
	/**
	 * String 이 비어 있는지 확인한다.
	 */
	public static boolean isEmpty(String str, int minLength){
		boolean result = false;
		if ( str == null || str.equals("") || str.length() < minLength){
			result = true;
		}
		return result;
	}
	
	
	
	/**
	 * 현재 시간을 return 한다. ex) 2013-04-08 22:31:23
	 */
	public static String getCurrentTime(){
		long time = System.currentTimeMillis(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(new Date(time));
		return currentTime;
		
	}
	
	/**
	 * Geocoder 클래스의 getFromLocation이 null일경우 HTTP를 통해 얻어온다.
	 * @return
	 */
	public static JSONObject requestHttp(String url, List < NameValuePair >  nameValuePairs) {

		HttpPost httpPost = new HttpPost(url);
        HttpClient client = new DefaultHttpClient();
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
	
	
	public String getContactName(final String phoneNumber) 
	{  
		String contactName = "";
		return contactName; 
	}
}
