package net.gringrid.imgoing.adapter;

import java.util.Vector;

import net.gringrid.imgoing.R;
import net.gringrid.imgoing.vo.MessageVO;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactsListAdapter extends BaseAdapter{

	Vector<MessageVO> data = new Vector<MessageVO>();
	
	/**
	 * 레이아웃 인플래터 
	 */
	private LayoutInflater inflater;
	
	
	/**
	 * 생성자
	 * @param context 액티비티 컨택스트
	 */
	public ContactsListAdapter(Context context)
	{
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
			view = inflater.inflate(R.layout.cell_message_list, null);
		}
		
		MessageVO item = data.get(position);
		
		if (item != null)
		{
			TextView id_tv_latitude = (TextView)view.findViewById(R.id.id_tv_latitude);
			TextView id_tv_longitude = (TextView)view.findViewById(R.id.id_tv_longitude);
			TextView id_tv_location_name = (TextView)view.findViewById(R.id.id_tv_location_name);
			
			id_tv_latitude.setText(item.latitude);
			id_tv_longitude.setText(item.longitude);
			id_tv_location_name.setText(item.location_name);			
		}
		
		return view;		
	}

}
