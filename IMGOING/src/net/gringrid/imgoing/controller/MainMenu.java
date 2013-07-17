package net.gringrid.imgoing.controller;

import net.gringrid.imgoing.Base;
import net.gringrid.imgoing.ConfigActivity;
import net.gringrid.imgoing.LocationControlActivity;
import net.gringrid.imgoing.MessageActivity;
import net.gringrid.imgoing.R;
import net.gringrid.imgoing.util.Util;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;

public class MainMenu extends LinearLayout  implements OnClickListener, AnimationListener{

	private Context mContext;
	private LayoutInflater mInflater;

	TranslateAnimation mAnimation;
	Animation mAnimationRotateRight;
	
	private ImageView mCurrentMenu;
	private ImageView mHideMenu1;
	private ImageView mHideMenu2;
	private ImageView mBtMenu;
	private RelativeLayout.LayoutParams mBtMenuParams;
	
	boolean mIsOpen;
	int mCurrentMenuIndex;
	
	public MainMenu(Context context) {
		super(context);
		mContext = context;
		//drawMainMenu();
	}
	
	public MainMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		//initAnimation();
		TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.MainMenu);
		
		mCurrentMenuIndex = ta.getInteger(R.styleable.MainMenu_current_menu, 0);
		
		
		
		drawMainMenu();
	}

	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		super.onLayout(changed, l, t, r, b);
	}
	
	
	protected void drawMainMenu() {
		mInflater = LayoutInflater.from(getContext());
		View mainMenu = mInflater.inflate(R.layout.controller_main_menu, null);
		mainMenu.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		addView(mainMenu);
		
		View view = findViewById(R.id.id_ll_location_control);
		if ( view != null ){
			//view.setVisibility(View.GONE);
			view.setOnClickListener(this);
			//mHideMenu1 = (ImageView) view;
		}
		view = findViewById(R.id.id_ll_location_list);
		if ( view != null ){
			//view.setVisibility(View.GONE);
			view.setOnClickListener(this);
			//mHideMenu2 = (ImageView) view;
		}
		
		view = findViewById(R.id.id_ll_config);
		if ( view != null ){
			view.setOnClickListener(this);
			//mBtMenu = (ImageView) view;
			//mBtMenuParams = (RelativeLayout.LayoutParams) mBtMenu.getLayoutParams();
		}
		
		
		switch (mCurrentMenuIndex) {
		case 0:
			findViewById(R.id.id_ll_location_control_arrow).setVisibility(View.VISIBLE);
			break;
			
		case 1:
			findViewById(R.id.id_ll_location_list_arrow).setVisibility(View.VISIBLE);
			break;
			
		case 2:
			findViewById(R.id.id_ll_config_arrow).setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
		
	}
	
	private void initAnimation(){
		mAnimationRotateRight = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_right);
		
	}

	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch ( v.getId() ) {
		/*
		case R.id.id_iv_current_menu:
			findViewById(R.id.id_iv_hide_menu1).setVisibility(View.VISIBLE);
			findViewById(R.id.id_iv_hide_menu2).setVisibility(View.VISIBLE);
			
			intent = new Intent(mContext, LocationControlActivity.class);
			((Base)mContext).startNewActivity(intent);
			
			break;
		*/
		case R.id.id_ll_location_control:
			intent = new Intent(mContext, LocationControlActivity.class);
			((Base)mContext).startNewActivity(intent);
			break;
			
			
		case R.id.id_ll_location_list:
			intent = new Intent(mContext, MessageActivity.class);
			((Base)mContext).startNewActivity(intent);
			break;
			
		case R.id.id_ll_config:
			intent = new Intent(mContext, ConfigActivity.class);
			((Base)mContext).startNewActivity(intent);
			break;
		/*	
		case R.id.id_iv_menu:
			mAnimationRotateRight.setAnimationListener(this);
			
			//mBtMenu.setAnimation(mAnimation);
			//mBtMenu.startAnimation(mAnimationRotateRight);
			mIsOpen = !mIsOpen;
			startMenuAnimation(mIsOpen);
			
			intent = new Intent(mContext, MessageActivity.class);
			((Base)mContext).startNewActivity(intent);
			
			break;
		*/
			
		default:
			break;
		}
		// TODO Auto-generated method stub
		
	}
	
	private void startMenuAnimation(boolean open){
		
		AnimationSet animation = new AnimationSet(false);
		
		Animation rotationAnimation;
		int degree = open ? 90 : -90;
		rotationAnimation = new RotateAnimation(0,degree,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		//animation.setInterpolator(AnimationUtils.loadInterpolator(getContext(),android.R.anim.anticipate_overshoot_interpolator));
		rotationAnimation.setDuration(400);
		
		
		Animation translateAnimation;
		translateAnimation = new TranslateAnimation(0.0f, -Util.getPxFromDp(getContext(), 90), 0.0f, 0);
		//animation.setInterpolator(AnimationUtils.loadInterpolator(getContext(),android.R.anim.anticipate_overshoot_interpolator));
		translateAnimation.setDuration(400);
		translateAnimation.setStartOffset(400);
		translateAnimation.setFillAfter(true);
		translateAnimation.setFillEnabled(true);
		
		animation.setFillAfter(true);
		animation.setFillEnabled(true);
		animation.addAnimation(rotationAnimation);
		animation.addAnimation(translateAnimation);
		animation.setAnimationListener(this);
		mBtMenu.startAnimation(animation);
		
		
		mCurrentMenu.startAnimation(translateAnimation);
		
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
		
	}

	
}