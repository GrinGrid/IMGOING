package net.gringrid.imgoing;


import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;

import net.gringrid.imgoing.util.MyActivityManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Base extends Activity implements OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//regEvent();
	}

	
	@Override
	protected void onStart() {
		EasyTracker.getInstance().activityStart(this); // Add this method.
		//GoogleAnalytics mGaInstance = GoogleAnalytics.getInstance(this);
		//Tracker mGaTracker1 = mGaInstance.getTracker("UA-XXXX-Y");
		//mGaTracker1.sendView(this.getClass().getSimpleName());

		super.onStart();
	}
	
	@Override
	protected void onStop() {
		EasyTracker.getInstance().activityStop(this); // Add this method.
		super.onStop();
	}
	
	
	private void init() {
		
	}

	
	private void regEvent() {
		
	}

	
	
	public void startNewActivity(Intent intent) {
		
		String currentActivityName = this.getClass().getName();
		String nextActivityName = intent.getComponent().getClassName();
		
		if ( currentActivityName.equals(nextActivityName) ){
			return;
		}
		
		MyActivityManager.addActivityAtFirst(this);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	
	
	public void killProcess(){
		
		
		
		List<ApplicationInfo> packages;
		PackageManager pm;
		pm = getPackageManager();
		packages = pm.getInstalledApplications(0);

		ActivityManager mActivityManager = (ActivityManager) this.getSystemService(getApplicationContext().ACTIVITY_SERVICE);
		
		List<RunningAppProcessInfo> runningProcesses = mActivityManager.getRunningAppProcesses();
		
		for ( RunningAppProcessInfo runningProcess : runningProcesses ){
			
			Log.d("jiho", "runningProcess : ["+runningProcess.pid+"] "+runningProcess.processName);
		}
		/*
		for (ApplicationInfo packageInfo : packages)
		{
			
			if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
			Log.d("jiho", "packageInfo name : "+packageInfo.packageName);
			
			if (packageInfo.packageName.equals("net.gringrid.imgoing"))
			{
				
				int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
				Log.d("jiho", "sdkVersion : "+sdkVersion);
				if(sdkVersion < 8)
				{
					mActivityManager.restartPackage(packageInfo.packageName);
				}
				else
				{
					mActivityManager.killBackgroundProcesses(packageInfo.packageName);
				}
			}
		}
		*/
		

		
		
		
		MyActivityManager.clearHistory();
		//android.os.Process.killProcess(android.os.Process.myPid());
		Log.d("jiho", "PID : "+android.os.Process.myPid());
		
		finish();
	}
	
	
	public void showTerminateAlert() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.alert_title);
			builder.setMessage("종료하시겠습니까?");
			builder.setPositiveButton(R.string.alert_confirm,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							killProcess();
						}
					});
			builder.setNegativeButton("취소", null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onBackPressed() {
		
		
		
		if ( MyActivityManager.historyIsEmpty() ){
			showTerminateAlert();
			
		}else{
			MyActivityManager.remove(this);
			Intent intent;
			Activity lastHistoryActivity = MyActivityManager.getLastHistory();
			
			intent = new Intent(this, lastHistoryActivity.getClass());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			MyActivityManager.removeLastHistory();
			super.onBackPressed();
		}
	}
	

	@Override
	public void onClick(View v) {
		Intent intent;
		switch ( v.getId() ) {
		/*
		case R.id.id_menu_location_control:
			if ( Preference.IS_LOGIN == false ){
				showAlert(R.string.alert_need_login);
				return;
			}
			Log.d("jiho", "LocationControl Menu");
			intent = new Intent(this, LocationControlActivity.class);
			startNewActivity(intent);
			break;
			
		case R.id.id_menu_location_list:
			if ( Preference.IS_LOGIN == false ){
				showAlert(R.string.alert_need_login);
				return;
			}
			Log.d("jiho", "id_menu_location_list");
			intent = new Intent(this, MessageActivity.class);
			startNewActivity(intent);
			break;
		*/	
		default:
			break;
		}// TODO Auto-generated method stub
		
	}
	
	public void showAlert(String message) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.alert_title);
			builder.setMessage(message);
			builder.setPositiveButton(R.string.alert_confirm, null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public void showAlert(int messageResourceID) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.alert_title);
			builder.setMessage(messageResourceID);
			builder.setPositiveButton(R.string.alert_confirm, null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
