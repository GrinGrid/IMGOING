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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Base implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);		
		this.setTitle(R.string.login_title);
		Log.d("jiho", "LoginActivity Oncreage : "+Preference.GCM_REGISTRATION_ID);
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
			user.phone_number = Preference.PHONE_NUMBER;
			
			
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
			Log.d("jiho", "GCM id : "+Preference.GCM_REGISTRATION_ID);
			
			String sharedEmail = settings.getString("EMAIL", "EMAIL EMPTY");
			Log.d("jiho", "sharedEmail : "+sharedEmail);
			
			String url = "http://choijiho.com/gringrid/imgoing/imgoing.php";
	        List < NameValuePair > inputData = new ArrayList < NameValuePair > (4);
	        inputData.add(new BasicNameValuePair("mode","LOGIN"));
	        inputData.add(new BasicNameValuePair("email",user.email));
	        inputData.add(new BasicNameValuePair("password",user.password));
	        inputData.add(new BasicNameValuePair("gcm_reg_id",user.gcm_reg_id));
			
	        JSONObject resultData = Util.requestHttp(url, inputData);
			

			if ( Util.isPossibleNetwork(this) == false || resultData == null ){
				showAlert("Wifi 혹은 3G망이 연결되지 않았거나 원활하지 않습니다.네트워크 확인후 다시 접속해 주세요!");
	        	return;
			}
        	
			
			try {
				// result_cd 가 0000 이면 로그인 처리
				if ( resultData.getString("result_cd").equals(Constants.SUCCESS) ){
					boolean isNeedUpdate = false;
					final String loginEmail = user.email;
					String serverPhoneNumber = resultData.getString("result_phone_number");
					String serverGcmRegId = resultData.getString("result_gcm_reg_id");
					
					// 스마트폰 전화번호와 서버 전화번호가 다른경우
					if ( Util.isEmpty(user.phone_number) == false && user.phone_number.equals(serverPhoneNumber) == false ){
						// 전화번호 서버로 전송하여 update
						isNeedUpdate = true;
					}else if ( Util.isEmpty(user.phone_number) ){
						user.phone_number = serverPhoneNumber;
						Preference.PHONE_NUMBER = serverPhoneNumber;
					}
					
					Log.d("jiho", "LOCAL GCM ID : "+user.gcm_reg_id);
					Log.d("jiho", "serverGcmRegId : "+serverGcmRegId);
					Log.d("jiho", "user.phone_number : "+user.phone_number);
					
					// 스마트폰 GCM ID 와 서버 GCM ID 가 다른경우
					if ( user.gcm_reg_id != null && user.gcm_reg_id.equals(serverGcmRegId) == false ){
						// GCM ID 서버로 전송하여 update
						isNeedUpdate = true;
					}else if ( user.gcm_reg_id == null ){
						showAlert("GCM ID가 존재하지 않습니다. \n앱을 다시 실행하여 주시기 바랍니다.");
					}
					
					if ( isNeedUpdate ){
						url = "http://choijiho.com/gringrid/imgoing/imgoing.php";
				        inputData = new ArrayList < NameValuePair > (4);
				        inputData.add(new BasicNameValuePair("mode","UPDATE"));
				        inputData.add(new BasicNameValuePair("email",user.email));
				        inputData.add(new BasicNameValuePair("phone_number",user.phone_number));
				        inputData.add(new BasicNameValuePair("gcm_reg_id",user.gcm_reg_id));
						
				        resultData = Util.requestHttp(url, inputData);
					}
					
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("AUTO_LOGIN", true);
					editor.putString("EMAIL", user.email);
					editor.putString("PHONE_NUMBER", user.phone_number);
					editor.putString("GCM_REG_ID", user.gcm_reg_id);
					editor.commit();
					
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(R.string.alert_title);
					builder.setMessage("로그인 되었습니다.");
					builder.setPositiveButton(R.string.alert_confirm,
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent();
									//bundle = new Bundle();
									
									//bundle.putString("EMAIL", loginEmail);
									intent.putExtra("EMAIL", loginEmail);
									setResult(RESULT_OK, intent);
									finish();
								}
							});
					builder.show();
					
					
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
