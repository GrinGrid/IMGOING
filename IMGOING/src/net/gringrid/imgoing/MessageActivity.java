
package net.gringrid.imgoing;

import java.util.ArrayList;
import java.util.Vector;

import com.google.android.gcm.GCMRegistrar;

import net.gringrid.imgoing.adapter.MessageListAdapter;
import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.util.DBHelper;
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
		viewReceiveMessage();
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
	
	
	// 이벤트 등록
	private void regEvent() {
		
		// 서비스 시작 버튼
		View view = findViewById(R.id.id_bt_delete);	
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
		view = findViewById(R.id.id_bt_send);
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
		MessageDao messageDao = new MessageDao(this);
		Cursor cursor = messageDao.querySendList();
		int index_receiver = cursor.getColumnIndex("receiver");
		int index_start_time = cursor.getColumnIndex("start_time");
		message_data.clear();
		if ( cursor.moveToFirst() ) {
			do{
				MessageVO messageVO = new MessageVO();
				messageVO.receiver = cursor.getString(index_receiver);
				messageVO.start_time = cursor.getString(index_start_time);
				
				message_data.add(messageVO);
			}while(cursor.moveToNext());
		}
		messageListAdapter.setAll(message_data);
	}
	
	
	/**
	 * 받은 메시지 보이기
	 */
	private void viewReceiveMessage(){
		MessageDao messageDao = new MessageDao(this);
		//Cursor cursor = messageDao.queryMessageAll();
		Cursor cursor = messageDao.queryReceiveList();
		
		int index_sender = cursor.getColumnIndex("sender");
		int index_start_time = cursor.getColumnIndex("start_time");
		message_data.clear();
		if ( cursor.moveToFirst() ) {
			do{
				MessageVO messageVO = new MessageVO();
				messageVO.sender = cursor.getString(index_sender);
				messageVO.start_time = cursor.getString(index_start_time);
				
				message_data.add(messageVO);
			}while(cursor.moveToNext());
		}
		messageListAdapter.setAll(message_data);
	}

	
	
	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		
		switch(v.getId()){
		case R.id.id_bt_delete:
			MessageDao messageDao = new MessageDao(this);
			messageDao.deleteAll();
			messageListAdapter.notifyDataSetChanged();
			break;
		case R.id.id_menu_location_control:
			intent = new Intent(this, LocationControlActivity.class);
			startNewActivity(intent);
			break;
		case R.id.id_menu_location_list:
			intent = new Intent(this, MessageActivity.class);
			startNewActivity(intent);
			break;
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
		int index_send_time = cursor.getColumnIndex("send_time"); 
		int index_latitude = cursor.getColumnIndex("latitude");
		int index_longitude = cursor.getColumnIndex("longitude");
		int index_provider = cursor.getColumnIndex("provider");
		int index_location_name = cursor.getColumnIndex("location_name");
		
		do{
			MessageVO messageVO = new MessageVO();
			messageVO.no = cursor.getInt(index_no);
			messageVO.sender = cursor.getString(index_sender);
			messageVO.receiver = cursor.getString(index_receiver);
			messageVO.start_time = cursor.getString(index_start_time);
			messageVO.send_time = cursor.getString(index_send_time);
			messageVO.latitude = cursor.getString(index_latitude);
			messageVO.longitude = cursor.getString(index_longitude);
			messageVO.provider = cursor.getString(index_provider);
			messageVO.location_name = cursor.getString(index_location_name);
			
			message_data.add(messageVO);	
		
		}while(cursor.moveToNext());
		messageListAdapter.setAll(message_data);
		
		
	}
}