package net.gringrid.imgoing;

import java.util.Vector;

import net.gringrid.imgoing.controller.Selector;
import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.SpinnerVO;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 설정 화면
 * @author Evangelist
 *
 */
public class ConfigActivity extends Base implements OnClickListener, OnCheckedChangeListener{
	
	
	private Selector mMaxReceiveCount = null;
	private Selector mMaxSendCount = null;
	private Selector mDelete = null;
	
	private final int SPINNER_TYPE_ALARM_METHOD = 0;
	private final int SPINNER_TYPE_MAX_RECEIVE_COUNT = 1;
	private final int SPINNER_TYPE_MAX_SEND_COUNT = 2;
	private final int SPINNER_TYPE_DELETE = 3;
	
	
	private static final Vector<SpinnerVO> ALARM_METHOD = new Vector<SpinnerVO>();
	
	static {
		ALARM_METHOD.add(new SpinnerVO("0", "진동"));
		ALARM_METHOD.add(new SpinnerVO("1", "소리"));
		ALARM_METHOD.add(new SpinnerVO("2", "무음"));
		ALARM_METHOD.add(new SpinnerVO("3", "알리지않음"));			
	}
	
	private static final Vector<SpinnerVO> MAX_RECEIVE_COUNT = new Vector<SpinnerVO>();
	
	static {
		MAX_RECEIVE_COUNT.add(new SpinnerVO("0", "500"));
		MAX_RECEIVE_COUNT.add(new SpinnerVO("1", "1000"));
		MAX_RECEIVE_COUNT.add(new SpinnerVO("2", "2000"));					
	}
	
	private static final Vector<SpinnerVO> MAX_SEND_COUNT = new Vector<SpinnerVO>();
	
	static {
		MAX_SEND_COUNT.add(new SpinnerVO("0", "500"));
		MAX_SEND_COUNT.add(new SpinnerVO("1", "1000"));
		MAX_SEND_COUNT.add(new SpinnerVO("2", "2000"));
	}
	
	
	private static final Vector<SpinnerVO> DELETE = new Vector<SpinnerVO>();
	
	static {
		DELETE.add(new SpinnerVO("0", "선택"));
		DELETE.add(new SpinnerVO("1", "전체삭제"));
		DELETE.add(new SpinnerVO("2", "받은메시지 삭제"));
		DELETE.add(new SpinnerVO("3", "보낸메시지 삭제"));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);		
		
		init();
		regEvent();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	private void init() {
		
		SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, 0);
		CheckBox id_cb_alarm_vibrate_yn = (CheckBox)findViewById(R.id.id_cb_alarm_vibrate_yn);
		CheckBox id_cb_alarm_sound_yn = (CheckBox)findViewById(R.id.id_cb_alarm_sound_yn);
		CheckBox id_cb_alarm_light_yn = (CheckBox)findViewById(R.id.id_cb_alarm_light_yn);
		
		id_cb_alarm_vibrate_yn.setChecked( settings.getBoolean("CONFIG_ALARM_VIBRATE_YN", true));
		id_cb_alarm_sound_yn.setChecked( settings.getBoolean("CONFIG_ALARM_SOUND_YN", false));
		id_cb_alarm_light_yn.setChecked( settings.getBoolean("CONFIG_ALARM_LIGHT_YN", false));
		
		
		mMaxReceiveCount = (Selector)findViewById(R.id.id_selector_config_max_receive);
		if ( mMaxReceiveCount != null ){
			mMaxReceiveCount.setText(MAX_RECEIVE_COUNT.elementAt(Preference.CONFIG_MAX_RECEIVE_COUNT).mName);
		}
		mMaxSendCount = (Selector)findViewById(R.id.id_selector_config_max_send);	
		if ( mMaxSendCount != null ){
			mMaxSendCount.setText(MAX_SEND_COUNT.elementAt(Preference.CONFIG_MAX_SEND_COUNT).mName);
		}
		mDelete = (Selector)findViewById(R.id.id_selector_config_delete);	
		if ( mDelete != null ){
			mDelete.setText(DELETE.elementAt(0).mName);
		}
	}


	private void regEvent() {
		View view = findViewById(R.id.id_selector_config_max_receive);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_selector_config_max_send);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_selector_config_delete);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_tv_send_person_list);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_ll_guide);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		CheckBox id_cb_alarm_vibrate_yn = (CheckBox)findViewById(R.id.id_cb_alarm_vibrate_yn);
		CheckBox id_cb_alarm_sound_yn = (CheckBox)findViewById(R.id.id_cb_alarm_sound_yn);
		CheckBox id_cb_alarm_light_yn = (CheckBox)findViewById(R.id.id_cb_alarm_light_yn);
		
		if ( id_cb_alarm_vibrate_yn != null ){
			id_cb_alarm_vibrate_yn.setOnCheckedChangeListener(this);
		}
		if ( id_cb_alarm_sound_yn != null ){
			id_cb_alarm_sound_yn.setOnCheckedChangeListener(this);
		}
		if ( id_cb_alarm_light_yn != null ){
			id_cb_alarm_light_yn.setOnCheckedChangeListener(this);
		}
		
	}
	
	@Override
	public void onClick(View v) {
	
		switch( v.getId() ){
				
		case R.id.id_selector_config_max_receive:
			showSpinnerPopup(MAX_RECEIVE_COUNT, SPINNER_TYPE_MAX_RECEIVE_COUNT);
			break;
			
		case R.id.id_selector_config_max_send:
			showSpinnerPopup(MAX_SEND_COUNT, SPINNER_TYPE_MAX_SEND_COUNT);
			break;
			
		case R.id.id_selector_config_delete:
			showSpinnerPopup(DELETE, SPINNER_TYPE_DELETE);
			break;
			
		case R.id.id_ll_guide:
			Intent intent = new Intent(this, GuideActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	private void showSpinnerPopup(Vector<SpinnerVO> itemList, int spinnerType) {
		final Context context = this;
		final int type = spinnerType;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		int itemSize = itemList.size();
		
		String[] items = new String[ itemSize ];
		
		for ( int i=0; i<itemSize; i++ ){
			items[i] = itemList.get(i).mName;
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if(type == SPINNER_TYPE_MAX_RECEIVE_COUNT) {
					mMaxReceiveCount.setText(MAX_RECEIVE_COUNT.elementAt(which).mName);
					Preference.CONFIG_MAX_RECEIVE_COUNT = Integer.parseInt( MAX_RECEIVE_COUNT.elementAt(which).mCode );					
					
					
				} else if(type == SPINNER_TYPE_MAX_SEND_COUNT) {
					mMaxSendCount.setText(MAX_SEND_COUNT.elementAt(which).mName);
					Preference.CONFIG_MAX_SEND_COUNT = Integer.parseInt( MAX_SEND_COUNT.elementAt(which).mCode );					
					
				} else if(type == SPINNER_TYPE_DELETE) {
					performDelete( which );
						
				}
				Util.saveConfig(context);
			}
		});
		builder.show();
	}
	
	private void performDelete(final int mode){
		
		final MessageDao messageDao = new MessageDao(getApplicationContext());
		
		String alertMessage = null;
		
		switch (mode) {
		case 1:
			alertMessage = "모든 메시지가 삭제됩니다. 삭제 하시겠습니까?";
			break;
		case 2:
			alertMessage = "받은 메시지가 모두 삭제됩니다. 삭제 하시겠습니까?";
			break;
		case 3:
			alertMessage = "보낸 메시지가 삭제됩니다. 삭제 하시겠습니까?";
			break;

		default:
			break;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
		builder.setTitle(R.string.alert_title);
		builder.setMessage( alertMessage );
		builder.setPositiveButton(R.string.alert_confirm,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (mode) {
						case 1:
							messageDao.deleteAll();
							break;
						case 2:
							messageDao.deleteReceiveMessage();
							break;
						case 3:
							messageDao.deleteSendMessage();
							break;

						default:
							break;
						}
						showAlert("정상적으로 삭제 되었습니다.");
					}
				});
		builder.setNegativeButton("취소", null);
		builder.show();
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
				
		switch (buttonView.getId()) {
		case R.id.id_cb_alarm_vibrate_yn :
			editor.putBoolean("CONFIG_ALARM_VIBRATE_YN",isChecked);			
			break;			
		
		case R.id.id_cb_alarm_sound_yn :
			editor.putBoolean("CONFIG_ALARM_SOUND_YN",isChecked);			
			break;			
		
		case R.id.id_cb_alarm_light_yn :
			editor.putBoolean("CONFIG_ALARM_LIGHT_YN",isChecked);			
			break;			
		}
		
		editor.commit();
		
	}

}
