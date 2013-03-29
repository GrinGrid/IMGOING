package net.gringrid.imgoing;

import net.gringrid.imgoing.vo.UserVO;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class JoinActivity extends Activity implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);		
		
		init();
		regEvent();
		
	}


	private void regEvent() {
		// TODO Auto-generated method stub
		
	}

	private void init() {
		View view = findViewById(R.id.id_bt_submit);
		if ( view != null ){
			view.setOnClickListener(this);
		}
						
		view = findViewById(R.id.id_et_phone_number);
		if ( view != null ){			
			TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
			((EditText)view).setText(tMgr.getLine1Number());
		}
		
		
	}


	@Override
	public void onClick(View v) {
		switch( v.getId() ){
		case R.id.id_bt_submit:
			EditText id_et_email = (EditText)findViewById(R.id.id_et_email);
			EditText id_et_phone_number = (EditText)findViewById(R.id.id_et_phone_number);
			EditText id_et_password = (EditText)findViewById(R.id.id_et_password);
			EditText id_et_repassword = (EditText)findViewById(R.id.id_et_repassword);
			
			
			
			UserVO user = new UserVO();
			user.email = id_et_email.getText().toString();
			user.phone_number = id_et_phone_number.getText().toString();
			user.password = id_et_password.getText().toString();
			user.repassword = id_et_repassword.getText().toString();
			
			Log.d("jiho", "email : "+user.email);
			Log.d("jiho", "phone_number : "+user.phone_number);
			Log.d("jiho", "password : "+user.password);
			Log.d("jiho", "repassword : "+user.repassword);
			
			
			
			break;
		}
	}
}
