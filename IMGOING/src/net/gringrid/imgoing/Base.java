package net.gringrid.imgoing;

import net.gringrid.imgoing.util.MyActivityManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class Base extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	private void init() {
		
	}

	
	private void regEvent() {
	}


	
	public void startNewActivity(Intent intent) {
		MyActivityManager.addActivityAtFirst(this);
	}
	
	public void killProcess(){
		MyActivityManager.clearHistory();
		System.exit(0);
	}
	
	public void showTerminateAlert() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("알림");
			builder.setMessage("종료하시겠습니까?");
			builder.setPositiveButton("확인",
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
			MyActivityManager.getLastHistory();
			super.onBackPressed();
		}
		super.onBackPressed();
	}

}
