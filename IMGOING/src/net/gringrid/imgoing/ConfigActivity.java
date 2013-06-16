package net.gringrid.imgoing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 설정 화면
 * @author Evangelist
 *
 */
public class ConfigActivity extends Base implements OnClickListener{
	
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
	}


	private void regEvent() {
		View view = findViewById(R.id.id_bt_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_iv_number_up);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_iv_number_down);
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
		view = findViewById(R.id.id_tv_send_person_list);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		
		
	}
	
	@Override
	public void onClick(View v) {
		
		Intent intent;
		TextView id_tv_min = null;
		
		switch( v.getId() ){
		case R.id.id_bt_control:
			break;
			
		case R.id.id_iv_number_up:
			break;
			
		case R.id.id_iv_number_down:
			break;
			
		}
	}

}
