package net.gringrid.imgoing.controller;

import net.gringrid.imgoing.Base;
import net.gringrid.imgoing.ConfigActivity;
import net.gringrid.imgoing.LocationControlActivity;
import net.gringrid.imgoing.MessageActivity;
import net.gringrid.imgoing.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

public class MainMenu extends LinearLayout implements OnClickListener{

	private Context mContext;
	private LayoutInflater mInflater;
	
	
	public MainMenu(Context context) {
		super(context);
		mContext = context;
		//drawMainMenu();
	}
	
	public MainMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		//drawMainMenu();
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		LinearLayout.LayoutParams params = new LayoutParams(getLayoutParams());
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;
		ImageView menuLocationControl = new ImageView(mContext);
		menuLocationControl.setLayoutParams(params); 
		menuLocationControl.setImageDrawable(mContext.getResources().getDrawable(R.drawable.location_control));
		addView(menuLocationControl);
		//net.gringrid.imgoing.controller
		super.onLayout(changed, l, t, r, b);
	}
	
	
	protected void drawMainMenu() {
		mInflater = LayoutInflater.from(mContext);
		View mainMenu = mInflater.inflate(R.layout.controller_main_menu, null);
		View view = mainMenu.findViewById(R.id.id_ll_menu_location_control);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = mainMenu.findViewById(R.id.id_ll_menu_location_list);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = mainMenu.findViewById(R.id.id_ll_menu_config);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		addView(mainMenu);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch ( v.getId() ) {
		case R.id.id_ll_menu_location_control:
			intent = new Intent(mContext, LocationControlActivity.class);
			((Base)mContext).startNewActivity(intent);
			break;
			
		case R.id.id_ll_menu_location_list:
			intent = new Intent(mContext, MessageActivity.class);
			((Base)mContext).startNewActivity(intent);
			break;
			
		case R.id.id_ll_menu_config:
			intent = new Intent(mContext, ConfigActivity.class);
			((Base)mContext).startNewActivity(intent);
			break;

		default:
			break;
		}
		// TODO Auto-generated method stub
		
	}

}
