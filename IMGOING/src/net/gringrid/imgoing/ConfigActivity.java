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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 설정 화면
 * @author Evangelist
 *
 */
public class ConfigActivity extends Base implements OnClickListener{
	
	private Selector mAlarmMethod= null;
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
		mAlarmMethod = (Selector)findViewById(R.id.id_selector_config_alarm);
		if ( mAlarmMethod != null ){
			mAlarmMethod.setText(ALARM_METHOD.elementAt(Preference.CONFIG_ALARM_METHOD).mName);
		}
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
		View view = findViewById(R.id.id_selector_config_alarm);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_selector_config_max_receive);
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
	}
	
	@Override
	public void onClick(View v) {
	
		switch( v.getId() ){
		case R.id.id_selector_config_alarm:
			showSpinnerPopup(ALARM_METHOD, SPINNER_TYPE_ALARM_METHOD);
			break;
			
		case R.id.id_selector_config_max_receive:
			showSpinnerPopup(MAX_RECEIVE_COUNT, SPINNER_TYPE_MAX_RECEIVE_COUNT);
			break;
			
		case R.id.id_selector_config_max_send:
			showSpinnerPopup(MAX_SEND_COUNT, SPINNER_TYPE_MAX_SEND_COUNT);
			break;
			
		case R.id.id_selector_config_delete:
			showSpinnerPopup(DELETE, SPINNER_TYPE_DELETE);
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
				
				if(type == SPINNER_TYPE_ALARM_METHOD) {
					mAlarmMethod.setText(ALARM_METHOD.elementAt(which).mName);
					Preference.CONFIG_ALARM_METHOD = Integer.parseInt( ALARM_METHOD.elementAt(which).mCode );					
					
				} else if(type == SPINNER_TYPE_MAX_RECEIVE_COUNT) {
					mMaxReceiveCount.setText(MAX_RECEIVE_COUNT.elementAt(which).mName);
					Preference.CONFIG_MAX_RECEIVE_COUNT = Integer.parseInt( MAX_RECEIVE_COUNT.elementAt(which).mCode );					
					
					
				} else if(type == SPINNER_TYPE_MAX_SEND_COUNT) {
					mMaxSendCount.setText(MAX_SEND_COUNT.elementAt(which).mName);
					Preference.CONFIG_MAX_SEND_COUNT = Integer.parseInt( MAX_SEND_COUNT.elementAt(which).mCode );					
					
				} else if(type == SPINNER_TYPE_DELETE) {
					MessageDao messageDao = new MessageDao(getApplicationContext());
					switch ( which ) {
					// 전체삭제 
					case 1:
						messageDao.deleteAll();
						break;
					
					// 받은메시지 삭제 	
					case 2:
						messageDao.deleteReceiveMessage();
						break;
					
					// 보낸메시지 삭제	
					case 3:
						messageDao.deleteSendMessage();
						break;
						
					default:
						break;
					}
					mMaxSendCount.setText(MAX_SEND_COUNT.elementAt(which).mName);
					Preference.CONFIG_MAX_SEND_COUNT = Integer.parseInt( MAX_SEND_COUNT.elementAt(which).mCode );					
					
				}
				Util.saveConfig(context);
			}
		});
		builder.show();
	}

}
