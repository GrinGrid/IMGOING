
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class MessageActivity extends Activity implements OnClickListener {

	private ListView messageList;
	MessageListAdapter messageListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		
		
		regEvent();
		
		
		Vector<MessageVO> message_data = new Vector<MessageVO>();
		MessageDao messageDao = new MessageDao(this);
		Cursor cursor = messageDao.queryMessageAll();
		
		int index_no = cursor.getColumnIndex("no");
		int index_sender = cursor.getColumnIndex("sender"); 
		int index_send_time = cursor.getColumnIndex("send_time"); 
		int index_latitude = cursor.getColumnIndex("latitude");
		int index_longitude = cursor.getColumnIndex("longitude");
		int index_provider = cursor.getColumnIndex("provider");
		int index_location_name = cursor.getColumnIndex("location_name");
		
		while (cursor.moveToNext()){
			MessageVO messageVO = new MessageVO();
			messageVO.no = cursor.getInt(index_no);
			messageVO.sender = cursor.getString(index_sender);
			messageVO.send_time = cursor.getString(index_send_time);
			messageVO.latitude = cursor.getString(index_latitude);
			messageVO.longitude = cursor.getString(index_longitude);
			messageVO.provider = cursor.getString(index_provider);
			messageVO.location_name = cursor.getString(index_location_name);
			
			message_data.add(messageVO);
		}
				
		messageList = (ListView)findViewById(R.id.id_lv_message);
		
		if ( messageList != null ){
			messageListAdapter = new MessageListAdapter(this);
			messageList.setAdapter(messageListAdapter);
			messageListAdapter.setAll(message_data);
		}

	}
	
	// 초기화
	public void init(){
		
	}
	
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
		
		
	}
	
	
//id_bt_delete
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
			startActivity(intent);
			break;
		case R.id.id_menu_location_list:
			intent = new Intent(this, MessageActivity.class);
			startActivity(intent);
			break;
		}
		
	}
}