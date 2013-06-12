package net.gringrid.imgoing;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.gringrid.imgoing.adapter.ContactsListAdapter;
import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.location.AlarmReceiver;
import net.gringrid.imgoing.location.SendCurrentLocationService;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.ContactsVO;
import net.gringrid.imgoing.vo.MessageVO;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationControlActivity extends Base implements OnClickListener, OnItemClickListener{
	
	// 주소록 Listview
	private ListView id_lv_contacts;
	private ContactsListAdapter contactsListAdapter;
	
	// 전송 간격을 세팅
	private int[] timeList = new int[]{1, 3, 5, 10, 15, 20, 25, 30, 40, 50, 60, 120};
	private int currentTime = 5;
	private TextView id_tv_send_message;
	private String receiverPhoneNumber;
	private String receiverNumberId;
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
		
		// Start / Stop 에 따라 레이아웃 세팅 
		setStartStopMode();
		
		// 주소록 검색 자동완성 세팅
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
		String[] names = new String[Preference.CONTACTS_LIST.size()];
		int i = 0;
		for ( ContactsVO vo : Preference.CONTACTS_LIST ){
			names[i++] = vo.name;
		}
		for ( String name : names ){
			//Log.d("jiho", "name : "+name);
		}
		ArrayAdapter<String> adapter = 
		        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
		textView.setAdapter(adapter);		
		
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
		view = findViewById(R.id.id_menu_config);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_tv_send_person_list);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		
	}
	
	
	private void setStartStopMode(){
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		boolean isStarted = settings.getBoolean("IS_START", false);
		int interval = settings.getInt("INTERVAL", Preference.DEFAULT_INTERVAL);
		receiverName = settings.getString("RECEIVER", null);

		Button id_bt_control = (Button)findViewById(R.id.id_bt_control);
		TextView id_tv_min = (TextView)findViewById(R.id.id_tv_min);
		
		
		if ( isStarted ){
			id_bt_control.setText("Stop");
			id_lv_contacts.setEnabled(false);
			findViewById(R.id.id_iv_number_up).setEnabled(false);
			findViewById(R.id.id_iv_number_down).setEnabled(false);
		}else{
			id_bt_control.setText("Start");
			id_lv_contacts.setEnabled(true);
			findViewById(R.id.id_iv_number_up).setEnabled(true);
			findViewById(R.id.id_iv_number_down).setEnabled(true);
		}
		
		currentTime = interval;
		id_tv_send_message.setText( makeLocationAlertMessage() );
		id_tv_min.setText(Integer.toString(currentTime));
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
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		switch( v.getId() ){
		case R.id.id_bt_control:
			
			boolean isStarted = settings.getBoolean("IS_START", false);
			int userSelectInterval = 0;
			
			// 시작버튼 Click 했을 경우.
			if ( isStarted == false ){
				userSelectInterval = currentTime;
				
				if ( receiverPhoneNumber == null ){
					Toast.makeText(this, "위치를 전송할 사람을 선택하세요.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				// GPS체크
				LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				
				if( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false ) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(R.string.alert_title);
					builder.setMessage("GPS 기능을 활성화 시키면 보다 정확한 위치정보를 얻을 수 있습니다. GPS기능을 설정 하시겠습니까?");
					builder.setPositiveButton(R.string.alert_confirm,
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							        startActivity(gpsOptionsIntent);
								}
							});
					builder.setNegativeButton("취소", null);
					builder.show();
				}
				
				// 네트워크 체크
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		        boolean isWifiAvail = ni.isAvailable();
		        boolean isWifiConn = ni.isConnected();
		        ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		        boolean isMobileAvail = ni.isAvailable();
		        boolean isMobileConn = ni.isConnected();
				
		        if (!isWifiConn && !isMobileConn) {
		        	showAlert("Wifi 혹은 3G망이 연결되지 않았거나 원활하지 않습니다.네트워크 확인후 다시 접속해 주세요!");
		        	return;
		        }
		        
				AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
				intent = new Intent(getApplicationContext(), AlarmReceiver.class);
				intent.putExtra(AlarmReceiver.ACTION_ALARM, AlarmReceiver.ACTION_ALARM);
				intent.putExtra("RECEIVER", receiverPhoneNumber);
				intent.putExtra("RECEIVER_ID", receiverNumberId);
				intent.putExtra("INTERVAL", currentTime);
				intent.putExtra("START_TIME", Util.getCurrentTime());
				
				
				PendingIntent pIntent = PendingIntent.getBroadcast(this, 1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					 
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 25000, pIntent);
					    
				/*
				mCurrentLocationServiceIntent = new Intent(this, SendCurrentLocationService.class);
				mCurrentLocationServiceIntent.putExtra("RECEIVER", receiverPhoneNumber);
				mCurrentLocationServiceIntent.putExtra("RECEIVER_ID", receiverNumberId);
				mCurrentLocationServiceIntent.putExtra("INTERVAL", currentTime);
				this.startService(mCurrentLocationServiceIntent);
				*/
			// 정지버튼 Click 했을 경우 
			}else if ( isStarted == true ){
				userSelectInterval = Preference.DEFAULT_INTERVAL;
				receiverName = null;
				receiverPhoneNumber = null;
				
				intent = new Intent(getApplicationContext(), AlarmReceiver.class);
				intent.putExtra(AlarmReceiver.ACTION_ALARM, AlarmReceiver.ACTION_ALARM);
				 
				final PendingIntent pIntent = PendingIntent.getBroadcast(this, 1234567,intent, PendingIntent.FLAG_UPDATE_CURRENT);
				 
				AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
				alarms.cancel(pIntent);
				/*
				if ( isLocationServiceRunning() ){
					this.stopService(new Intent(this, SendCurrentLocationService.class));
				}
				*/
			}
			editor.putBoolean("IS_START", !isStarted);
			editor.putInt("INTERVAL", userSelectInterval);
			editor.putString("RECEIVER", receiverName);
			editor.commit();
			
			setStartStopMode();
			
			//toggleControlButton();
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
			startNewActivity(intent);
			break;
			
		case R.id.id_menu_location_list:
			intent = new Intent(this, MessageActivity.class);
			startNewActivity(intent);
			break;
		
		case R.id.id_menu_config:
			intent = new Intent(this, ConfigActivity.class);
			startNewActivity(intent);
			break;
			
		case R.id.id_tv_send_person_list:
			// 보낸사람 목록을 리스트뷰에 출력한다.
			MessageDao messageDao = new MessageDao(this);
			Cursor cursor = messageDao.querySendPersonList();
			
			int index_receiver = cursor.getColumnIndex("receiver");
			int index_receiver_id = cursor.getColumnIndex("receiver_id");
			
			Vector<ContactsVO> contactList = new Vector<ContactsVO>();
			if ( cursor.moveToFirst() ) {
				do{
					Log.d("jiho", "cursor.getString(index_receiver) : "+cursor.getString(index_receiver));
					for ( ContactsVO contact : Preference.CONTACTS_LIST ){
						if ( contact.id.equals(cursor.getString(index_receiver_id)) ){
							contactList.add(contact);
						}
					}
					/*
					// 받은사람 전화번호로 연락처 정보 조회 
					String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="+ cursor.getString(index_receiver_id);
			    	CursorLoader phoneLoader = new CursorLoader(this,ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, null, null);
			    	Cursor phoneCursor = phoneLoader.loadInBackground();
				    
			    	while ( phoneCursor.moveToNext() ){
				    	
				    	String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				    	String numberType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				    	String numberId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				    	String numberName = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				    	
				    	//01로 시작하는지 체크 해야함
				    	if ( Integer.parseInt(numberType) == Phone.TYPE_MOBILE && number.substring(0, 2).equals("01") ){
				    		receiverPhoneNumber = number.replace("-", "");
				    		
				    		Log.d("jiho", "receiverPhoneNumber : "+receiverPhoneNumber);
				    		
				    		if ( receiverPhoneNumber.equals(cursor.getString(index_receiver)) ){
				    			Log.d("jiho", "receiverPhoneNumber : "+receiverPhoneNumber);
				    			ContactsVO contactsVO = new ContactsVO();
								contactsVO.id = numberId;
								contactsVO.phoneNumber = receiverPhoneNumber;
								contactsVO.name = numberName; 
								contactList.add(contactsVO);
				    		}
				    	}
				    	
				    
				    }
				    */
			    	
					
				}while(cursor.moveToNext());
			}
			contactsListAdapter.setAll( contactList );
			cursor.close();
			break;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ContactsVO contact = (ContactsVO)contactsListAdapter.getItem(position);
		Log.d("jiho", "parent : "+parent.getClass().getSimpleName());
		Log.d("jiho", "view : "+view.getClass().getSimpleName());
		Log.d("jiho", "position : "+position);
		Log.d("jiho", "id : "+id);
		Log.d("jiho", "position phone_name : "+contact.name);
		
		receiverPhoneNumber = null;
		receiverName = null;
		
		// 선택한 리스트 화살표 모양 보이도록 함
		contactsListAdapter.setSelected(position);
		contactsListAdapter.notifyDataSetChanged();
		
		// 연락처 ID로 전화번호 조회
		String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="+contact.id;
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
	    		receiverNumberId = numberId;
	    		Log.d("jiho", "receiverPhoneNumber : "+receiverPhoneNumber);
	    		receiverName = contact.name;
	    	}
	    }
	    phoneCursor.close();
	    
	    // 휴대폰 번호가 없는 사용자인 경우
	    if ( receiverPhoneNumber == null ){
	    	Toast.makeText(this, "보낼 수 없는 사용자 입니다.", Toast.LENGTH_SHORT).show();
	    }else{
	    	
    		// 서버에 등록되어 있는지 확인
    		String url = "http://choijiho.com/gringrid/imgoing/imgoing.php";
	        List < NameValuePair > inputData = new ArrayList < NameValuePair > (4);
	        inputData.add(new BasicNameValuePair("mode","IS_REGISTERED"));
	        inputData.add(new BasicNameValuePair("phone_number",receiverPhoneNumber));
	        JSONObject resultData = Util.requestHttp(url, inputData);
	        
			// result_cd 가 0000 이 아니면 에러처리
			try {
				if ( resultData.getString("result_cd").equals(Constants.SUCCESS) == false ){
					receiverPhoneNumber = null;
					Toast.makeText(this, resultData.getString("result_msg"), Toast.LENGTH_SHORT).show();
				}else{
					// 안내메시지 세팅
			    	id_tv_send_message.setText( makeLocationAlertMessage() );
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
