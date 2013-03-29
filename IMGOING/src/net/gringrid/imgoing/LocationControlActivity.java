package net.gringrid.imgoing;

import java.util.Vector;

import net.gringrid.imgoing.location.SendCurrentLocationService;
import net.gringrid.imgoing.vo.ContactsVO;
import net.gringrid.imgoing.vo.UserVO;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.content.CursorLoader;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class LocationControlActivity extends Activity implements OnClickListener{

	private Intent mCurrentLocationServiceIntent = null;
	private boolean CURRENT_BUTTON;
	private final boolean START = true;
	private final boolean STOP = false;
	private ListView id_lv_contacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_control);		
		
		init();
		regEvent();
	}

	private void init() {
		id_lv_contacts = (ListView)findViewById(R.id.id_lv_contacts);
		
		
		if ( isLocationServiceRunning() ){
			CURRENT_BUTTON = STOP;
		}else{
			CURRENT_BUTTON = START;
		}
		setControlButtonText();
		
		
		
		// 주소록 Adapter에 세팅
		Vector<ContactsVO> contactsList = new Vector<ContactsVO>();  
		String projection[] = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
		
	    Uri uri = ContactsContract.Contacts.CONTENT_URI;

	    String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1" + 
	    " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + " =1";

	    String order = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

	    CursorLoader loader = new CursorLoader(this, uri, projection, selection, null, order);
	    Cursor cursor = loader.loadInBackground();
	    
	    int id_idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
	    int id_name = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	    String id_value = null;
	    String name_value = null;
	    
	    while ( cursor.moveToNext() ){
	    	
	    	
	    	id_value = cursor.getString( id_idx );
	    	name_value = cursor.getString( id_name );
	    	
	    	selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="+id_value;
	    	
		    Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, null, null);
		    while ( phoneCursor.moveToNext() ){
		    	
		    	String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		    	String numberType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
		    	String numberId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
		    	
		    	if ( Integer.parseInt(numberType) == Phone.TYPE_MOBILE ){
		    		ContactsVO contacts = new ContactsVO();
			    	contacts.name = name_value;			    	
		    		contacts.phoneNumber = number;
		    		contactsList.add(contacts);		    		
		    	}
		    }
	    }
	    
	    for ( ContactsVO vo : contactsList ){
	    	Log.d("jiho", "name : "+vo.name);
	    	Log.d("jiho", "phoneNumber : "+vo.phoneNumber);
	    }
	    
	    
	    
	}


	private void regEvent() {
		View view = findViewById(R.id.id_bt_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
	}

	private void setControlButtonText(){
		Button id_bt_control = (Button)findViewById(R.id.id_bt_control);
		if ( CURRENT_BUTTON == START ){
			id_bt_control.setText("Start");
		}else{
			id_bt_control.setText("Stop");
		}
	}
	
	private void toggleControlButton(){		
		
		if ( CURRENT_BUTTON == START ){
			CURRENT_BUTTON = STOP;			
		}else{
			CURRENT_BUTTON = START;			
		}
		
		setControlButtonText();
	}
	
	/**
	 * 서비스가 수행중인지 확인한다.
	 * @return 서비스 수행여부.
	 */
	private boolean isLocationServiceRunning(){
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {			
			if (Constants.LOCATION_SERVICE.equals(service.service.getClassName())) {					
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch( v.getId() ){
		case R.id.id_bt_control:
			// 시작버튼 Click 했을 경우.
			if ( CURRENT_BUTTON == START ){
				mCurrentLocationServiceIntent = new Intent(this, SendCurrentLocationService.class);
				mCurrentLocationServiceIntent.putExtra("TEST_DATA", "TEST03923");
				this.startService(mCurrentLocationServiceIntent);
				
			// 정지버튼 Click 했을 경우 
			}else if ( CURRENT_BUTTON == STOP ){
				if ( isLocationServiceRunning() ){
					this.stopService(new Intent(this, SendCurrentLocationService.class));
				}
			}
			
			toggleControlButton();
			break;
		}
	}
}
