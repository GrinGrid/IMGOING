package net.gringrid.imgoing.adapter;

import java.util.Vector;

import net.gringrid.imgoing.MapActivity;
import net.gringrid.imgoing.R;
import net.gringrid.imgoing.dao.MessageDao;
import net.gringrid.imgoing.vo.MessageVO;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter{

	private static int TRANS_MODE;
	private final static int TRANS_MODE_SENDER = 0;
	private final static int TRANS_MODE_RECEIVER = 1;
	
	Vector<MessageVO> data = new Vector<MessageVO>();
	Context mContext;
	/**
	 * 레이아웃 인플래터 
	 */
	private LayoutInflater inflater;
	
	
	/**
	 * 생성자
	 * @param context 액티비티 컨택스트
	 */
	public MessageListAdapter(Context context)
	{
		mContext = context;
		inflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setAll(Vector<MessageVO> messageList){
		this.data.clear();
		this.data.addAll(messageList);		
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		
		if (view == null)
		{
			view = inflater.inflate(R.layout.cell_message, null);
		}
		
		final MessageVO item = data.get(position);
		
		if (item != null)
		{
			LinearLayout id_rl_cell = (LinearLayout)view.findViewById(R.id.id_ll_cell);
			TextView id_tv_sender = (TextView)view.findViewById(R.id.id_tv_sender);
			//TextView id_tv_receiver = (TextView)view.findViewById(R.id.id_tv_receiver);
			//TextView id_tv_start_time = (TextView)view.findViewById(R.id.id_tv_start_time);
			TextView id_tv_send_time = (TextView)view.findViewById(R.id.id_tv_send_time);
			//TextView id_tv_provider = (TextView)view.findViewById(R.id.id_tv_provider);
			//TextView id_tv_latitude = (TextView)view.findViewById(R.id.id_tv_latitude);
			//TextView id_tv_longitude = (TextView)view.findViewById(R.id.id_tv_longitude);
			//TextView id_tv_location_name = (TextView)view.findViewById(R.id.id_tv_location_name);
			ImageView id_iv_map = (ImageView)view.findViewById(R.id.id_iv_map);
			ImageView id_iv_del = (ImageView)view.findViewById(R.id.id_iv_del);
			//Button id_bt_del = (Button)view.findViewById(R.id.id_bt_del);
			//Button id_bt_list = (Button)view.findViewById(R.id.id_bt_list);
			
			/*
			if ( position % 2 == 0 ){
				id_rl_cell.setBackgroundColor(Color.CYAN);
			}else{
				id_rl_cell.setBackgroundColor(Color.WHITE);
			}
			*/
			
			id_tv_sender.setText(item.receiver_name);
			//id_tv_receiver.setText(item.receiver);
			//id_tv_start_time.setText(item.start_time);
			id_tv_send_time.setText(item.send_time);
			//id_tv_provider.setText(item.provider);
			//id_tv_latitude.setText(item.latitude);
			//id_tv_longitude.setText(item.longitude);
			//id_tv_location_name.setText(item.location_name);
			
			final String receiver = item.receiver;
			final String start_time = item.start_time;
			
			id_iv_map.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if ( v.getId() == R.id.id_iv_map ){
						Intent intent = new Intent(mContext, MapActivity.class);
						intent.putExtra("RECEIVER", receiver);
						intent.putExtra("START_TIME", start_time);
						mContext.startActivity(intent);
					}
				}
			});
			
			id_iv_del.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if ( v.getId() == R.id.id_iv_del ){
						if ( item.sender == null ){
							MessageDao dao = new MessageDao(mContext);
							dao.deleteReceiveOne(item.receiver, item.start_time);
							notifyDataSetChanged();
						}
						if ( item.receiver == null ){
							MessageDao dao = new MessageDao(mContext);
							dao.deleteSendOne(item.sender, item.start_time);
							notifyDataSetChanged();
							
						}
					}
				}
			});
			/*
			id_bt_list.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if ( v.getId() == R.id.id_bt_list ){
						MessageDao messageDao = new MessageDao(mContext);
						Cursor cursor = messageDao.querySendOneRouteList(start_time);
						Vector<MessageVO> listdata = new Vector<MessageVO>();
						
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
							
							listdata.add(messageVO);	
						
						}while(cursor.moveToNext());
						setAll(listdata);
					}
				}
			});
			*/
			
		}
		
		return view;		
	} // end of getView

}
