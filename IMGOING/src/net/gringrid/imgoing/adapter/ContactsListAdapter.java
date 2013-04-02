package net.gringrid.imgoing.adapter;

import java.util.Vector;

import net.gringrid.imgoing.R;
import net.gringrid.imgoing.vo.ContactsVO;
import net.gringrid.imgoing.vo.MessageVO;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactsListAdapter extends BaseAdapter{

	Vector<ContactsVO> data = new Vector<ContactsVO>();
	
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

	public void setAll(Vector<ContactsVO> messageList){
		this.data.clear();
		this.data.addAll(messageList);		
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null)
		{
			view = inflater.inflate(R.layout.cell_contacts_list, null);
		}
		
		ContactsVO item = data.get(position);
		
		if (item != null)
		{
			TextView id_tv_name = (TextView)view.findViewById(R.id.id_tv_name);
			id_tv_name.setText(item.name);
		}
		
		return view;		
	}

}
