
package net.gringrid.imgoing;

import java.util.ArrayList;
import java.util.Vector;

import com.google.android.gcm.GCMRegistrar;

import net.gringrid.imgoing.adapter.MessageListAdapter;
import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.util.DBHelper;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.ContactsVO;
import net.gringrid.imgoing.vo.MessageVO;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MessageActivity extends Base implements OnClickListener, OnItemClickListener{

	private int MESSAGE_MODE;
	private final int MESSAGE_MODE_RECEIVE = 0;	// 받은메시지
	private final int MESSAGE_MODE_SEND = 1;		// 보낸메시지 
	
	private ListView messageList;
	MessageListAdapter messageListAdapter;
	Vector<MessageVO> message_data = new Vector<MessageVO>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		
		init();
		regEvent();
		
	}
	
	@Override
	protected void onResume() {
		if ( MESSAGE_MODE == MESSAGE_MODE_RECEIVE ){
			viewReceiveMessage();
		}else if ( MESSAGE_MODE == MESSAGE_MODE_SEND ){
			viewSendMessage();
		}
		setButton();
		super.onResume();
	}
	
	// 초기화
	public void init(){
		messageList = (ListView)findViewById(R.id.id_lv_message);
		
		if ( messageList != null ){
			messageList.setOnItemClickListener(this);
			messageListAdapter = new MessageListAdapter(this);
			messageList.setAdapter(messageListAdapter);
		}
	}
	
	
	private void setButton(){
		
		View id_bt_receive = findViewById(R.id.id_bt_receive);
		View id_bt_send = findViewById(R.id.id_bt_send);
		
		id_bt_receive.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_message_tap_off));
		id_bt_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_message_tap_off));
		
		if ( MESSAGE_MODE == MESSAGE_MODE_RECEIVE ){
			id_bt_receive.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_message_tap_on));
		}else if ( MESSAGE_MODE == MESSAGE_MODE_SEND ){
			id_bt_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_message_tap_on));
		}
	}
	
	// 이벤트 등록
	private void regEvent() {
		
		// 서비스 시작 버튼
		View view = findViewById(R.id.id_bt_send);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_bt_receive);
		if ( view != null ){
			view.setOnClickListener(this);
		}
	}
	
	/**
	 * 보낸 메시지 보이기
	 */
	private void viewSendMessage(){
		MESSAGE_MODE = MESSAGE_MODE_SEND;
		MessageDao messageDao = new MessageDao(this);
		Cursor cursor = messageDao.querySendList();
		int index_receiver = cursor.getColumnIndex("receiver");
		int index_start_time = cursor.getColumnIndex("start_time");
		int index_last_send_time = cursor.getColumnIndex("last_send_time");
		
		message_data.clear();
		if ( cursor.moveToFirst() ) {
			do{
				ContactsVO contactsVO = Util.getContactsVOByPhoneNumber(getApplication(), cursor.getString(index_receiver));
				String receiver_name = Util.isEmpty(contactsVO.name)?cursor.getString(index_receiver):contactsVO.name;
				MessageVO messageVO = new MessageVO();
				messageVO.receiver_name = receiver_name;
				messageVO.receiver = cursor.getString(index_receiver);
				messageVO.start_time = cursor.getString(index_start_time);
				messageVO.wrk_time = cursor.getString(index_last_send_time);
				
				Log.d("jiho", "last_send_time : "+messageVO.wrk_time);
				
				message_data.add(messageVO);
			}while(cursor.moveToNext());
		}
		if ( cursor != null ) cursor.close();
		messageListAdapter.setAll(message_data);
		messageListAdapter.setMode(MESSAGE_MODE);
		setButton();
	}
	
	
	/**
	 * 받은 메시지 보이기
	 */
	private void viewReceiveMessage(){
		MESSAGE_MODE = MESSAGE_MODE_RECEIVE;
		MessageDao messageDao = new MessageDao(this);
		Cursor cursor = messageDao.queryReceiveList();
		
		int index_sender = cursor.getColumnIndex("sender");
		int index_start_time = cursor.getColumnIndex("start_time");
		int index_last_send_time = cursor.getColumnIndex("last_send_time");
		message_data.clear();
		if ( cursor.moveToFirst() ) {
			do{
				Log.d("jiho", "cursor.getString(index_sender) : "+cursor.getString(index_sender));
				ContactsVO contactsVO = Util.getContactsVOByPhoneNumber(getApplication(), cursor.getString(index_sender));
				String sender_name = contactsVO==null?cursor.getString(index_sender):contactsVO.name;
								
				MessageVO messageVO = new MessageVO();
				messageVO.sender_name = sender_name;
				messageVO.sender = cursor.getString(index_sender);
				messageVO.start_time = cursor.getString(index_start_time);
				messageVO.wrk_time = cursor.getString(index_last_send_time);
				
				message_data.add(messageVO);
			}while(cursor.moveToNext());
		}
		if ( cursor != null ) cursor.close();
		messageListAdapter.setAll(message_data);
		messageListAdapter.setMode(MESSAGE_MODE);
		setButton();
	}

	
	
	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		
		switch(v.getId()){
		case R.id.id_bt_send:
			viewSendMessage();
			break;
		case R.id.id_bt_receive:
			viewReceiveMessage();
			break;
		}
		
	}

	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("jiho", "onlcick.");
		String receiver = message_data.get(position).receiver;
		String start_time = message_data.get(position).start_time;
		
		message_data.clear();
		message_data = new Vector<MessageVO>();
		MessageDao messageDao = new MessageDao(this);
		Cursor cursor = messageDao.querySendListForOne(receiver, start_time);
		
		int index_no = cursor.getColumnIndex("no");
		int index_sender = cursor.getColumnIndex("sender"); 
		int index_receiver = cursor.getColumnIndex("receiver"); 
		int index_start_time = cursor.getColumnIndex("start_time"); 
		int index_latitude = cursor.getColumnIndex("latitude");
		int index_longitude = cursor.getColumnIndex("longitude");
		int index_provider = cursor.getColumnIndex("provider");
		int index_wrk_time = cursor.getColumnIndex("wrk_time");
		int index_trans_yn = cursor.getColumnIndex("trans_yn");
		
		do{
			MessageVO messageVO = new MessageVO();
			messageVO.no = cursor.getInt(index_no);
			messageVO.sender = cursor.getString(index_sender);
			messageVO.receiver = cursor.getString(index_receiver);
			messageVO.start_time = cursor.getString(index_start_time);			
			messageVO.latitude = cursor.getString(index_latitude);
			messageVO.longitude = cursor.getString(index_longitude);
			messageVO.provider = cursor.getString(index_provider);
			messageVO.wrk_time = cursor.getString(index_wrk_time);
			messageVO.trans_yn = cursor.getString(index_trans_yn);
			
			message_data.add(messageVO);	
		
		}while(cursor.moveToNext());
		if ( cursor != null ) cursor.close();
		messageListAdapter.setAll(message_data);
		
		
	}
}