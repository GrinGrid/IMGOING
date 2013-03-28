
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
		while (cursor.moveToNext()){
			MessageVO messageVO = new MessageVO();
			messageVO.no = cursor.getInt(0);
			messageVO.latitude = cursor.getString(5);
			messageVO.longitude = cursor.getString(6);
			messageVO.location_name = cursor.getString(9);
			
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
		
	}
	
	
//id_bt_delete
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_bt_delete:
			MessageDao messageDao = new MessageDao(this);
			messageDao.deleteAll();
			messageListAdapter.notifyDataSetChanged();
			break;
		}
		
	}
}