package net.gringrid.imgoing.location;


import net.gringrid.imgoing.Preference;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;

public class Util {

	// added as an instance method to an Activity
	public static boolean isNetworkConnectionAvailable(Context context) {  
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info = cm.getActiveNetworkInfo();     
	    if (info == null) return false;
	    State network = info.getState();
	    return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
	}
	
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
}
