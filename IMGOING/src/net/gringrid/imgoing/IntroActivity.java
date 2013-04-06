package net.gringrid.imgoing;

import net.gringrid.imgoing.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;


public class IntroActivity extends Activity{
	private ProgressBar mProgress;
	private TextView mProgressText;
    private int mProgressStatus = 0;
    
    private Handler mHandler = new Handler();
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);		

		mProgress = (ProgressBar) findViewById(R.id.id_pb_loading);
		mProgressText = (TextView) findViewById(R.id.id_tv_pb_text);
		
        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
        	
            public void run() {
            	
                while (mProgressStatus < 100) {
                	mProgressStatus = doWork();
                	if ( mProgressStatus == 33 ){
                		Looper.prepare();
	                    Util.setContacts(getApplicationContext());
                	}
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                            mProgressText.setText("data loading... [ "+mProgressStatus+" / 100 ]");
                        }
                    });
                }
                
                
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                
            	IntroActivity.this.startActivity(intent);
            	IntroActivity.this.finish();
            }

			private int doWork() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				return ++mProgressStatus;
			}
        }).start();
        /*
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                
            	IntroActivity.this.startActivity(intent);
            	IntroActivity.this.finish();
            }
        }, 1000);
        */
	}
}
