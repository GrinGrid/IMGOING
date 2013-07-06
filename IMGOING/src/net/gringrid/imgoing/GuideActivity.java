package net.gringrid.imgoing;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;



/**
 * 이용안내 </br>
 * @author choijiho
 * @date 2013/07/04
 * @version 1.0.0
 * @since 1.0.0
 */
public class GuideActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_guide);
		
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		
	
	}
	
	@Override
	public void onStart()	{
		super.onStart();	
	}
	
}