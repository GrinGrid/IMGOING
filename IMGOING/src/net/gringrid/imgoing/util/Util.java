package net.gringrid.imgoing.util;


import java.util.Vector;

import net.gringrid.imgoing.Preference;
import net.gringrid.imgoing.vo.ContactsVO;
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
			// KT일경우 +8210xxxxxxx 형식으로 리턴되므로 처리
			phone_number = phone_number.replace("+82", "0");
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
	    
	    for ( ContactsVO vo : Preference.CONTACTS_LIST ){
	    	Log.d("jiho", "id : "+vo.id);
	    	Log.d("jiho", "name : "+vo.name);
	    	//Log.d("jiho", "phoneNumber : "+vo.phoneNumber);
	    }
	}
}
