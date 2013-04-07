package net.gringrid.imgoing;

import com.google.android.gcm.GCMRegistrar;

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
	
	private final boolean DEBUG = false;
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
            	Context mContext = getApplicationContext();
                while (mProgressStatus < 100) {
                	mProgressStatus = doWork();
                	
                	// 주소록 정보를 세팅한다.
                	if ( mProgressStatus == 33 ){
                		Looper.prepare();
	                    Util.setContacts(mContext);
	                    if ( DEBUG ){
	                    	Log.d("jiho", "Contacts count : "+Preference.CONTACTS_LIST.size());
	                    }
                	}
                	
                	// GCM서버에 단말정보를 세팅한다.
                	if ( mProgressStatus == 72 ){
                		GCMRegistrar.checkDevice(mContext);
                		GCMRegistrar.checkManifest(mContext);		
                		
                		final String regId = GCMRegistrar.getRegistrationId(mContext);
                		
                		if ( regId.equals("") ){
                			GCMRegistrar.register(mContext, Constants.PROJECT_ID);
                		}else{
                			Preference.GCM_REGISTRATION_ID = regId;
                			if ( DEBUG ){
	                			Log.d("jiho", "oncreated regId = "+regId);
	                			Log.d("jiho", "already registered.");
                			}
                		}
                	}
                	
                	// 전화번호 세팅
                	if ( mProgressStatus == 91 ){
                		Util.getMyPhoneNymber(mContext);
                		if ( DEBUG ){
                			Log.d("jiho", "MyPhoneNumber : "+Preference.PHONE_NUMBER);
                		}
                	}
                	
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                            if ( mProgressStatus < 33 ){
                            	mProgressText.setText("data loading... [ "+mProgressStatus+" / 100 ]");
                            }else if ( mProgressStatus < 72 ){
                            	mProgressText.setText("check gcm registration id ... [ "+mProgressStatus+" / 100 ]");
                            }else if ( mProgressStatus < 100 ){
                            	mProgressText.setText("set preferences data ... [ "+mProgressStatus+" / 100 ]");
                            }
                            
                        }
                    });
                }
                
                
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                
            	IntroActivity.this.startActivity(intent);
            	IntroActivity.this.finish();
            }

			private int doWork() {
				try {
					// 최소 1초는 intro화면을 보여준다.
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return ++mProgressStatus;
			}
        }).start();
	}
}
