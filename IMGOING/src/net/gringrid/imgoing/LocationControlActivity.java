package net.gringrid.imgoing;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.gringrid.imgoing.adapter.ContactsListAdapter;
import net.gringrid.imgoing.location.SendCurrentLocationService;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.ContactsVO;
import net.gringrid.imgoing.vo.UserVO;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationControlActivity extends Activity implements OnClickListener, OnItemClickListener{

	private Intent mCurrentLocationServiceIntent = null;
	
	// 시작 / 정지버튼 세팅
	private boolean CURRENT_BUTTON;
	private final boolean START = true;
	private final boolean STOP = false;
	
	// 주소록 Listview
	private ListView id_lv_contacts;
	private ContactsListAdapter contactsListAdapter;
	
	// 전송 간격을 세팅
	private int[] timeList = new int[]{1, 3, 5, 10, 15, 20, 25, 30, 40, 50, 60, 120};
	private int currentTime = 5;
	private TextView id_tv_send_message;
	private String receiverPhoneNumber;
	private String receiverName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_control);		
		
		init();
		regEvent();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void init() {
		// 주소록을 가져와 vector에 담는다.
		setContacts();
		
		// 주소록 리스트를 리스트뷰에 출력한다.
		id_lv_contacts = (ListView)findViewById(R.id.id_lv_contacts);
		contactsListAdapter = new ContactsListAdapter(this);
		id_lv_contacts.setAdapter(contactsListAdapter);
		contactsListAdapter.setAll(Preference.CONTACTS_LIST);
		
		// 전송알림메시지 
		id_tv_send_message = (TextView)findViewById(R.id.id_tv_send_message);
		id_tv_send_message.setText( makeLocationAlertMessage() );
		
		// Start / Stop 버튼을 세팅한다.
		if ( isLocationServiceRunning() ){
			CURRENT_BUTTON = STOP;
		}else{
			CURRENT_BUTTON = START;
		}
		setControlButtonText();
	}


	private void regEvent() {
		if ( id_lv_contacts != null ){
			id_lv_contacts.setOnItemClickListener(this);
		}
		View view = findViewById(R.id.id_bt_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_iv_number_up);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_iv_number_down);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_menu_location_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_menu_location_list);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
	}
	
	/**
	 * 주소록 data가 없을 경우 주소록 data 를 세팅한다.
	 */
	private void setContacts(){
		if ( Preference.CONTACTS_LIST == null ){
			Util.setContacts(this);
		}	
	}

	/**
	 * 시작 / 정지 버튼의 텍스트를 세팅한다.
	 */
	private void setControlButtonText(){
		Button id_bt_control = (Button)findViewById(R.id.id_bt_control);
		if ( CURRENT_BUTTON == START ){
			id_bt_control.setText("Start");
		}else{
			id_bt_control.setText("Stop");
		}
	}
	
	/**
	 * 시작 / 정지 버튼 토글 
	 */
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
		
		Intent intent;
		TextView id_tv_min = null;
		
		switch( v.getId() ){
		case R.id.id_bt_control:
			// 시작버튼 Click 했을 경우.
			if ( CURRENT_BUTTON == START ){
				if ( receiverPhoneNumber == null ){
					Toast.makeText(this, "위치를 전송할 사람을 선택하세요.", Toast.LENGTH_SHORT).show();
					return;
				}
				mCurrentLocationServiceIntent = new Intent(this, SendCurrentLocationService.class);
				mCurrentLocationServiceIntent.putExtra("RECEIVER", receiverPhoneNumber);
				mCurrentLocationServiceIntent.putExtra("INTERVAL", currentTime);
				this.startService(mCurrentLocationServiceIntent);
				
			// 정지버튼 Click 했을 경우 
			}else if ( CURRENT_BUTTON == STOP ){
				if ( isLocationServiceRunning() ){
					this.stopService(new Intent(this, SendCurrentLocationService.class));
				}
			}
			
			toggleControlButton();
			break;
			
		case R.id.id_iv_number_up:
			id_tv_min = (TextView)findViewById(R.id.id_tv_min);
			currentTime = Integer.parseInt(id_tv_min.getText().toString());
			
			for ( int i=0; i<timeList.length; i++ ){
				if ( timeList[i] == currentTime ){
					if ( i == timeList.length -1 ){
						currentTime = timeList[0];
					}else{
						currentTime = timeList[i+1];
					}
					break;
				}
			}
			id_tv_min.setText(Integer.toString(currentTime));
    		id_tv_send_message.setText( makeLocationAlertMessage() );
			
			
			break;
			
		case R.id.id_iv_number_down:
			id_tv_min = (TextView)findViewById(R.id.id_tv_min);
			
			currentTime = Integer.parseInt(id_tv_min.getText().toString());
			
			for ( int i=0; i<timeList.length; i++ ){
				if ( timeList[i] == currentTime ){
					if ( i == 0 ){
						currentTime = timeList[timeList.length-1];
					}else{
						currentTime = timeList[i-1];
					}
					break;
				}
			}
			id_tv_min.setText(Integer.toString(currentTime));
    		id_tv_send_message.setText( makeLocationAlertMessage() );
			break;
			
		case R.id.id_menu_location_control:
			intent = new Intent(this, LocationControlActivity.class);
			startActivity(intent);
			break;
			
		case R.id.id_menu_location_list:
			intent = new Intent(this, MessageActivity.class);
			startActivity(intent);
			break;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("jiho", "parent : "+parent.getClass().getSimpleName());
		Log.d("jiho", "view : "+view.getClass().getSimpleName());
		Log.d("jiho", "position : "+position);
		Log.d("jiho", "id : "+id);
		Log.d("jiho", "position phone_name : "+Preference.CONTACTS_LIST.get(position).name);
		
		receiverPhoneNumber = null;
		receiverName = null;
		
		// 선택한 리스트 화살표 모양 보이도록 함
		contactsListAdapter.notifyDataSetChanged();
		ImageView id_iv_contacts_list_selector = (ImageView)view.findViewById(R.id.id_iv_contacts_list_selector);
		id_iv_contacts_list_selector.setVisibility(View.VISIBLE);

		// 연락처 ID로 전화번호 조회
		String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="+Preference.CONTACTS_LIST.get(position).id;
    	CursorLoader phoneLoader = new CursorLoader(this,ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, null, null);
    	Cursor phoneCursor = phoneLoader.loadInBackground();
	    //Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, null, null);
	    
	    while ( phoneCursor.moveToNext() ){
	    	
	    	String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	    	String numberType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
	    	String numberId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
	    	
	    	//01로 시작하는지 체크 해야함
	    	if ( Integer.parseInt(numberType) == Phone.TYPE_MOBILE && number.substring(0, 2).equals("01") ){
	    		receiverPhoneNumber = number.replace("-", "");
	    		Log.d("jiho", "receiverPhoneNumber : "+receiverPhoneNumber);
	    		receiverName = Preference.CONTACTS_LIST.get(position).name;
	    	}
	    }
	    phoneCursor.close();
	    
	    // 휴대폰 번호가 없는 사용자인 경우
	    if ( receiverPhoneNumber == null ){
	    	Toast.makeText(this, "보낼 수 없는 사용자 입니다.", Toast.LENGTH_SHORT).show();
	    }else{
	    	// 안내메시지 세팅
	    	id_tv_send_message.setText( makeLocationAlertMessage() );
    		
    		// 서버에 등록되어 있는지 확인
    		String url = "http://choijiho.com/gringrid/imgoing/imgoing.php";
	        List < NameValuePair > inputData = new ArrayList < NameValuePair > (4);
	        inputData.add(new BasicNameValuePair("mode","IS_REGISTERED"));
	        inputData.add(new BasicNameValuePair("phone_number",receiverPhoneNumber));
	        JSONObject resultData = Util.requestHttp(url, inputData);
	        
			// result_cd 가 0000 이 아니면 에러처리
			try {
				if ( resultData.getString("result_cd").equals(Constants.SUCCESS) == false ){
					Toast.makeText(this, resultData.getString("result_msg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	
	/**
	 * 사용자가 선택한 정보(받는사람/송신간격)을 텍스트로 세팅
	 * @return 세팅된 메시지
	 */
	public String makeLocationAlertMessage(){
		String message = "";
		String receiver = "";
		if ( receiverName == null ){
			receiver = "[           ]";
		}else{
			receiver = "[ "+receiverName+" ]";
		}
		message += currentTime+" "+getResources().getString(R.string.location_alert_first);
		message += receiver+" "+getResources().getString(R.string.location_alert_second);
		
		return message;
	}
}
