package net.gringrid.imgoing;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.UserVO;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);		
		this.setTitle(R.string.login_title);
		
		init();
		regEvent();
		
	}

	private void init() {
		
		EditText id_et_email = (EditText)findViewById(R.id.id_et_email);
		EditText id_et_password = (EditText)findViewById(R.id.id_et_password);
		
		id_et_email.setText("nisclan@hotmail.com");
		id_et_password.setText("password");
	}

	
	private void regEvent() {
		View view = findViewById(R.id.id_bt_submit);
		if ( view != null ){
			view.setOnClickListener(this);
		}
		view = findViewById(R.id.id_bt_cancel);
		if ( view != null ){
			view.setOnClickListener(this);
		}
						
		view = findViewById(R.id.id_et_phone_number);
		if ( view != null ){			
			((EditText)view).setText(Util.getMyPhoneNymber(this));
		}
	}



	@Override
	public void onClick(View v) {
		switch( v.getId() ){
		
		// 로그인버튼
		case R.id.id_bt_submit:
			SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
			
			EditText id_et_email = (EditText)findViewById(R.id.id_et_email);
			EditText id_et_password = (EditText)findViewById(R.id.id_et_password);
			
			UserVO user = new UserVO();
			user.email = id_et_email.getText().toString();
			user.password = id_et_password.getText().toString();
			user.gcm_reg_id = Preference.GCM_REGISTRATION_ID; 
			
			
			// Email 체크 
			if ( Util.isEmpty(user.email, 7) ){
				// TODO email 형식 체크 
				Toast.makeText(this, "이메일을 확인하세요.", Toast.LENGTH_LONG).show();
				// TODO 포커스 email로
				return;
			}
			// 비밀번호 체크 
			if ( Util.isEmpty(user.password, 4) ){
				Toast.makeText(this, "비밀번호를 확인하세요.", Toast.LENGTH_LONG).show();
				return;
			}
			
			Log.d("jiho", "email : "+user.email);
			Log.d("jiho", "password : "+user.password);
			
			String sharedEmail = settings.getString("EMAIL", "EMAIL EMPTY");
			Log.d("jiho", "sharedEmail : "+sharedEmail);
			
			String url = "http://choijiho.com/gringrid/imgoing/imgoing.php";
	        List < NameValuePair > inputData = new ArrayList < NameValuePair > (4);
	        inputData.add(new BasicNameValuePair("mode","LOGIN"));
	        inputData.add(new BasicNameValuePair("email",user.email));
	        inputData.add(new BasicNameValuePair("password",user.password));
	        inputData.add(new BasicNameValuePair("gcm_reg_id",user.gcm_reg_id));
			
	        JSONObject resultData = Util.requestHttp(url, inputData);
			
			try {
				// result_cd 가 0000 이면 로그인 처리
				if ( resultData.getString("result_cd").equals(Constants.SUCCESS) ){
					
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("AUTO_LOGIN", true);
					editor.putString("EMAIL", user.email);
					editor.putString("PHONE_NUMBER", user.phone_number);
					editor.putString("GCM_REG_ID", user.gcm_reg_id);
					editor.commit();
					Toast.makeText(this, resultData.getString("result_msg"), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, resultData.getString("result_msg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//String result_cd = resultData.getString("result_cd");
			
			break;
		
		// 취소버튼
		case R.id.id_bt_cancel:
			finish();
			break;
		}
	}
}
