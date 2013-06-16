package net.gringrid.imgoing.adapter;

import java.util.Vector;

import net.gringrid.imgoing.R;
import net.gringrid.imgoing.vo.ContactsVO;
import net.gringrid.imgoing.vo.MessageVO;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsListAdapter extends BaseAdapter{

	Vector<ContactsVO> data = new Vector<ContactsVO>();
	
	/**
	 * 레이아웃 인플래터 
	 */
	private LayoutInflater inflater;
	private int selectedIndex;
	
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
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setAll(Vector<ContactsVO> messageList){
		if ( messageList == null ){
			return;
		}
		this.data.clear();
		this.data.addAll(messageList);		
		notifyDataSetChanged();
	}
	
	public void addAll(Vector<ContactsVO> messageList){
		this.data.addAll(messageList);		
		notifyDataSetChanged();
	}
	
	public void add(ContactsVO contact){
		this.data.add(0, contact);		
		notifyDataSetChanged();
	}
	
	public void setSelected(int position){
		selectedIndex = position;
	}
	
	public Vector<ContactsVO> getAll(){
		return data;
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
			ImageView id_iv_contacts_list_selector = (ImageView)view.findViewById(R.id.id_iv_contacts_list_selector);
			if ( selectedIndex == position ){
				id_iv_contacts_list_selector.setVisibility(View.VISIBLE);
			}else{
				id_iv_contacts_list_selector.setVisibility(View.GONE);
			}
			id_tv_name.setText(item.name);
			if ( item.isHistory ){
				id_tv_name.setTextColor(Color.BLUE);
			}else{
				id_tv_name.setTextColor(Color.BLACK);
			}
		}
		
		return view;		
	}

}
