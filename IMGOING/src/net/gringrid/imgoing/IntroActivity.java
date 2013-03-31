package net.gringrid.imgoing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class IntroActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);		
		
		
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                
            	IntroActivity.this.startActivity(intent);
            	IntroActivity.this.finish();
            }
        }, 1000);
	}
}
