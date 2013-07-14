package net.gringrid.imgoing;

import java.util.ArrayList;
import java.util.Iterator;
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
import android.app.Notification;
import android.app.NotificationManager;
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
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationControlActivity extends Base implements 	OnClickListener, 
																OnItemClickListener,
																OnFocusChangeListener,
																OnEditorActionListener
																{

	// 위치전송 여부
	boolean mIsStarted = false;
	// 주소록 Listview
	private ListView id_lv_contacts;
	private ContactsListAdapter contactsListAdapter;
	
	// 전송 간격을 세팅
	private int[] timeList = new int[]{1, 3, 5, 7, 10, 15, 20, 25, 30, 40, 50, 60, 120};
	private int currentTime;
	private TextView id_tv_send_message;
	private String receiverPhoneNumber;
	private String receiverNumberId;
	private String receiverName;
	
	// 초성검색 , 중간검색
	private static final char HANGUL_BEGIN_UNICODE = 44032;	// 가
	private static final char HANGUL_LAST_UNICODE = 55203;	// 힣
	private static final char HANGUL_BASE_UNIT = 588;		// 각자음 마다 가지는 글자수
	private static final char[] INITIAL_SOUND = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
	
	// Notification ID 
	private static final int NOTIFICATION_ID_MAIN = 7575;
	
	private ArrayList<View> mViewList;
	private int mCurrentFootPrintIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_control);		
		
		init();
		regEvent();
	}

	
	@Override
	protected void onResume() {
		// Preference.SEND_HISTORY_CONTACTS_LIST 갱신
		Util.setSendHistoryContactList(this);
		
		super.onResume();
	}
	
	private void init() {
		
		// 주소록을 가져와 vector에 담는다.
		setContacts();
		
		// 주소록 리스트를 리스트뷰에 출력한다.
		id_lv_contacts = (ListView)findViewById(R.id.id_lv_contacts);
		contactsListAdapter = new ContactsListAdapter(this);
		id_lv_contacts.setAdapter(contactsListAdapter);
		contactsListAdapter.setAll(Preference.SEND_HISTORY_CONTACTS_LIST);
		contactsListAdapter.addAll(Preference.CONTACTS_LIST);
		
		// 전송알림메시지 
		id_tv_send_message = (TextView)findViewById(R.id.id_tv_send_message);
		id_tv_send_message.setText( makeLocationAlertMessage() );
		
		// Start / Stop 에 따라 레이아웃 세팅 
		setStartStopMode();
		
		// 주소록 검색 자동완성 세팅
		//AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
		final EditText searchEditText = (EditText) findViewById(R.id.autocomplete_country);
		if(searchEditText != null) {
			searchEditText.setOnFocusChangeListener(this);
			searchEditText.setOnEditorActionListener(this);
			searchEditText.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String keyword = searchEditText.getText().toString().toUpperCase();
					
					if ( keyword.length() == 0 ){
						contactsListAdapter.setAll(Preference.SEND_HISTORY_CONTACTS_LIST);
						contactsListAdapter.addAll(Preference.CONTACTS_LIST);
						
					}else{
						Vector<ContactsVO> matched = new Vector<ContactsVO>();
						for ( ContactsVO contact : Preference.CONTACTS_LIST ){

							if ( contact.name.toUpperCase().indexOf(keyword) >= 0 ){
								matched.add(contact);
							}else{
								if ( matchString(contact.name, keyword) ){
									matched.add(contact);
								}
							}
						}
						contactsListAdapter.setAll(matched);
					}
						
				}
			});
		}
		
		
		
	}


	private void regEvent() {
		if ( id_lv_contacts != null ){
			id_lv_contacts.setOnItemClickListener(this);
		}
		View view = findViewById(R.id.id_bt_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_iv_stop);
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
		view = findViewById(R.id.id_tv_send_person_list);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		
	}
	
	
	private void setStartStopMode(){
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		mIsStarted = settings.getBoolean("IS_START", false);
		int interval = settings.getInt("INTERVAL", Preference.DEFAULT_INTERVAL);
		receiverName = settings.getString("RECEIVER", null);

		Button id_bt_control = (Button)findViewById(R.id.id_bt_control);
		TextView id_tv_min = (TextView)findViewById(R.id.id_tv_min);
		
		View beforeView = findViewById(R.id.id_ll_before_start);
		View afterView = findViewById(R.id.id_ll_after_start);
		
		beforeView.setVisibility(View.GONE);
		afterView.setVisibility(View.GONE);
		
		if ( mIsStarted ){
			afterView.setVisibility(View.VISIBLE);
			id_lv_contacts.setEnabled(false);
			findViewById(R.id.id_iv_number_up).setEnabled(false);
			findViewById(R.id.id_iv_number_down).setEnabled(false);
			setSendingMessage();
			playAnimation();
		}else{
			beforeView.setVisibility(View.VISIBLE);
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
	 * Animation을 실행한다.
	 */
	private void playAnimation(){
		
		ImageView foot1 = (ImageView)findViewById(R.id.foot1);
		ImageView foot2 = (ImageView)findViewById(R.id.foot2);
		ImageView foot3 = (ImageView)findViewById(R.id.foot3);
		ImageView foot4 = (ImageView)findViewById(R.id.foot4);
		ImageView foot5 = (ImageView)findViewById(R.id.foot5);
		ImageView foot6 = (ImageView)findViewById(R.id.foot6);
		ImageView foot7 = (ImageView)findViewById(R.id.foot7);
		ImageView foot8 = (ImageView)findViewById(R.id.foot8);
		ImageView foot9 = (ImageView)findViewById(R.id.foot9);
		ImageView foot10 = (ImageView)findViewById(R.id.foot10);
		ImageView foot11 = (ImageView)findViewById(R.id.foot11);
		ImageView foot12 = (ImageView)findViewById(R.id.foot12);
		
		mViewList = new ArrayList<View>();
		mViewList.add(foot1);
		mViewList.add(foot2);
		mViewList.add(foot3);
		mViewList.add(foot4);
		mViewList.add(foot5);
		mViewList.add(foot6);
		mViewList.add(foot7);
		mViewList.add(foot8);
		mViewList.add(foot9);
		mViewList.add(foot10);
		mViewList.add(foot11);
		mViewList.add(foot12);
		mCurrentFootPrintIndex = 0;
		//playFootPrintAnimation();
		playFootAnimation();
		
	}
	
	private void setFootInvisivle(){
		for ( View view : mViewList ){
			view.setVisibility(View.INVISIBLE);
		}
	}
	
	
	private void playFootAnimation(){
		int footCount = mViewList.size();
		
		for ( int i=0; i<footCount; i++ ){
			AnimationSet animationSet = new AnimationSet(true);
			
			Animation alphaFadeInAnimation = new AlphaAnimation(0, 1);
			alphaFadeInAnimation.setDuration(600);
			alphaFadeInAnimation.setStartOffset((i+1)*600);
			
			Animation alphaFadeOutAnimation = new AlphaAnimation(1, 0);
			alphaFadeOutAnimation.setDuration(3000);
			alphaFadeOutAnimation.setStartOffset((i+1)*600+600);
			
			animationSet.addAnimation(alphaFadeInAnimation);
			animationSet.addAnimation(alphaFadeOutAnimation);
			
			final View foot = mViewList.get(i);
			final int finalIndex = i;
			animationSet.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					foot.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					foot.setVisibility(View.INVISIBLE);
					if ( finalIndex == mViewList.size()-1 ){
						setFootInvisivle();
						playFootAnimation();
					}
				}
			});
			foot.startAnimation(animationSet);
		}
	}
	
	
	private void clearAllAnimation(){
		Log.d("jiho", "clearAllAnimation");
		for ( View view : mViewList ){
			view.clearAnimation();
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
		PendingIntent pIntent = null;
		NotificationManager notificationManager = null; 
		
		switch( v.getId() ){
		
		// 시작버튼 Click 했을 경우.
		case R.id.id_bt_control:
			
			int userSelectInterval = 0;
			
			userSelectInterval = currentTime;
			
			if ( receiverPhoneNumber == null ){
				Toast.makeText(this, "위치를 전송할 사람을 선택하세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// GPS체크
			String provider = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			
		    if(!provider.contains("gps")){
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
	        // TODO방어코드 삭제하고 모바일 가능한지 체크해야 할 것 같음
	        boolean isMobileAvail = false;
	        boolean isMobileConn = false;
	        if ( ni != null ){
		        isMobileAvail = ni.isAvailable();
		        isMobileConn = ni.isConnected();
	        }
	        Log.d("jiho", "isWifiAvail : "+isWifiAvail);
	        Log.d("jiho", "isMobileAvail : "+isMobileAvail);
	        
	        if (!isWifiConn && !isMobileConn) {
	        	showAlert("Wifi 혹은 3G망이 연결되지 않았거나 원활하지 않습니다.네트워크 확인후 다시 접속해 주세요!");
	        	return;
	        }
	        
	        // 알람등록을 위한 데이타 세팅
			AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			MessageVO messageVO = new MessageVO();
			messageVO.receiver = receiverPhoneNumber;			
			messageVO.interval = Integer.toString(currentTime);
			messageVO.start_time = Util.getCurrentTime();
			
			intent = new Intent(getApplicationContext(), AlarmReceiver.class);
			intent.putExtra(AlarmReceiver.ACTION_ALARM, AlarmReceiver.ACTION_ALARM);
			intent.putExtra("MESSAGEVO", messageVO);
			/*
			intent.putExtra("RECEIVER", receiverPhoneNumber);
			intent.putExtra("RECEIVER_ID", receiverNumberId);
			intent.putExtra("INTERVAL", currentTime);
			intent.putExtra("START_TIME", Util.getCurrentTime());
			*/
			pIntent = PendingIntent.getBroadcast(this, 1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			int term = currentTime * 60 * 1000;
			// 설정한 시간 간격으로 알람 호출
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), currentTime * 30 * 1000, pIntent);
			
			editor.putInt("INTERVAL", userSelectInterval);
			
			// Notification 생성
			//Notification noti = NotificationCompat.Builder.build();
			Intent resultIntent = new Intent(this, IntroActivity.class);
			resultIntent.putExtra("IS_FROM_NOTIFICATION", true);
			// 앱 실행하고 다른 메뉴로 이동후 noti 클릭하면 새로 앱을 띄움
			//resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
			PendingIntent notifyIntent =
			        PendingIntent.getActivity(
			        this,
			        0,
			        resultIntent,
			        PendingIntent.FLAG_UPDATE_CURRENT
			);
			
			
			NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this);
			notiBuilder.setSmallIcon(R.drawable.ic_launcher);
			notiBuilder.setContentTitle("I'm Going");
			notiBuilder.setContentText("I'm going is running ~");
			notiBuilder.setOngoing(true);
			notiBuilder.setContentIntent(notifyIntent);
			
			Notification notification = notiBuilder.build();
			notification.ledARGB = 1;
			
			
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(NOTIFICATION_ID_MAIN, notiBuilder.build());
				
			editor.putString("RECEIVER", receiverName);
			editor.putBoolean("IS_START", true);
			editor.commit();
			
			setStartStopMode();
			
			//toggleControlButton();
			break;
			
		case R.id.id_iv_stop:
			// 정지버튼 Click 했을 경우
			// 애니메이션 중지
			// 알람 중지
			// 서비스가 실행중일경우 서비스 중지
			clearAllAnimation();
			receiverName = null;
			receiverPhoneNumber = null;
			
			intent = new Intent(getApplicationContext(), AlarmReceiver.class);
			intent.putExtra(AlarmReceiver.ACTION_ALARM, AlarmReceiver.ACTION_ALARM);
			 
			pIntent = PendingIntent.getBroadcast(this, 1234567,intent, PendingIntent.FLAG_UPDATE_CURRENT);
			 
			AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
			alarms.cancel(pIntent);
			
			if ( isLocationServiceRunning() ){
				this.stopService(new Intent(this, SendCurrentLocationService.class));
			}
			
			// Notification dismiss
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(NOTIFICATION_ID_MAIN);
			
			editor.putString("RECEIVER", receiverName);
			editor.putBoolean("IS_START", false);
			editor.commit();
			
			mIsStarted = false;
			
			setStartStopMode();
			
		
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
	    		break;
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
	        
	        if ( resultData == null ){
	        	showAlert(getResources().getString(R.string.alert_network_disable));
	        	receiverPhoneNumber = null;
	        	receiverName = null;
	        	id_tv_send_message.setText( makeLocationAlertMessage() );
	        	return;
	        }
			// result_cd 가 0000 이 아니면 에러처리
			try {
				if ( resultData.getString("result_cd").equals(Constants.SUCCESS) == false ){
					receiverPhoneNumber = null;
					receiverName = null;
					Toast.makeText(this, resultData.getString("result_msg"), Toast.LENGTH_SHORT).show();
				}else{
					// 안내메시지 세팅
			    	//id_tv_send_message.setText( makeLocationAlertMessage() );
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    id_tv_send_message.setText( makeLocationAlertMessage() );
	}
	
	private boolean matchString(String value, String search) {
		int t = 0;
		int seof = value.length() - search.length();
		int slen = search.length();
		if (seof < 0)
			return false; // 검색어가 더 길면 false를 리턴한다.

		for (int i = 0; i <= seof; i++)
		{
			t = 0;
			while (t < slen)
			{
				if (isInitialSound(search.charAt(t)) == true && isHangul(value.charAt(i + t)))
				{
					// 만약 현재 char이 초성이고 value가 한글이면
					if (getInitialSound(value.charAt(i + t)) == search.charAt(t))
						// 각각의 초성끼리 같은지 비교한다
						t++;
					else
						break;
				}
				else
				{
					// char이 초성이 아니라면
					if (value.charAt(i + t) == search.charAt(t))
						// 그냥 같은지 비교한다.
						t++;
					else
						break;
				}
			}
			if (t == slen)
				return true; // 모두 일치한 결과를 찾으면 true를 리턴한다.
		}

		return false; // 일치하는 것을 찾지 못했으면 false를 리턴한다.
	}


	
	private boolean isInitialSound(char searchar) {
		for (char c : INITIAL_SOUND)
		{
			if (c == searchar)
			{
				return true;
			}
		}
		return false;
	}
	
	
	private char getInitialSound(char c)
	{
		int hanBegin = (c - HANGUL_BEGIN_UNICODE);
		int index = hanBegin / HANGUL_BASE_UNIT;
		return INITIAL_SOUND[index];
	}
	
	private boolean isHangul(char c)
	{
		return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
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
	
	/**
	 * 
	 */
	private void setSendingMessage(){
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		TextView id_tv_sendign_message = (TextView)findViewById(R.id.id_tv_sending_message);
		String message = "";
		String interval = Integer.toString( settings.getInt("INTERVAL", 0) );
		String receiver = settings.getString("RECEIVER", null);
		
		message += interval+" "+getResources().getString(R.string.location_sending_first);
		message += " "+receiver+" "+getResources().getString(R.string.location_sending_second);
		
		id_tv_sendign_message.setText(message);
	}


	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		
	}
}
