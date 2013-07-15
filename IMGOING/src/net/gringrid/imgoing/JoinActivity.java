package net.gringrid.imgoing;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.UserVO;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoinActivity extends Base implements OnClickListener, OnFocusChangeListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_join);		
		
		
		LayoutParams params = getWindow().getAttributes(); 
		params.height = LayoutParams.WRAP_CONTENT;
		params.width  = LayoutParams.FILL_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		init();
		regEvent();
		
	}

	private void init() {
		
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
		
		view = findViewById(R.id.id_et_email);
		if ( view != null ){
			view.setOnFocusChangeListener(this);
		}
		view = findViewById(R.id.id_et_phone_number);
		if ( view != null ){
			view.setOnFocusChangeListener(this);
		}
		view = findViewById(R.id.id_et_password);
		if ( view != null ){
			view.setOnFocusChangeListener(this);
		}
		view = findViewById(R.id.id_et_repassword);
		if ( view != null ){
			view.setOnFocusChangeListener(this);
		}
		
		
	}



	@Override
	public void onClick(View v) {
		switch( v.getId() ){
		
		// 가입버튼
		case R.id.id_bt_submit:
			SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
			
			EditText id_et_email = (EditText)findViewById(R.id.id_et_email);
			EditText id_et_phone_number = (EditText)findViewById(R.id.id_et_phone_number);
			EditText id_et_password = (EditText)findViewById(R.id.id_et_password);
			EditText id_et_repassword = (EditText)findViewById(R.id.id_et_repassword);
		
			UserVO user = new UserVO();
			user.email = id_et_email.getText().toString();
			user.phone_number = id_et_phone_number.getText().toString();
			user.password = id_et_password.getText().toString();
			user.repassword = id_et_repassword.getText().toString();
			user.gcm_reg_id = Preference.GCM_REGISTRATION_ID; 
			
			
			// Email 체크 
			if ( Util.isEmpty(user.email, 7) ){
				// TODO email 형식 체크 
				Toast.makeText(this, "이메일을 확인하세요.", Toast.LENGTH_LONG).show();
				// TODO 포커스 email로
				return;
			}
			// phone number 체크 
			if ( Util.isEmpty(user.phone_number, 10) ){
				Toast.makeText(this, "가입할 수 없는 기기입니다.", Toast.LENGTH_LONG).show();
				return;
			}
			// 비밀번호 체크 
			if ( Util.isEmpty(user.password, 4) ){
				Toast.makeText(this, "비밀번호를 확인하세요.", Toast.LENGTH_LONG).show();
				return;
			}
			// 비밀번호 확인 체크 
			if ( Util.isEmpty(user.repassword, 4) ){
				Toast.makeText(this, "비밀번호 확인 항목을 확인하세요.", Toast.LENGTH_LONG).show();
				return;
			}
			// 비밀번호와 비밀번호확인 같은지 체크 
			if ( user.repassword.equals(user.password) == false ){
				Toast.makeText(this, "비밀번호와 비밀번호 확인 내용이 다릅니다.", Toast.LENGTH_LONG).show();
				return;
			}
			
			
			String url = "http://choijiho.com/gringrid/imgoing/imgoing.php";
	        List < NameValuePair > inputData = new ArrayList < NameValuePair > (4);
	        inputData.add(new BasicNameValuePair("mode","JOIN"));
	        inputData.add(new BasicNameValuePair("email",user.email));
	        inputData.add(new BasicNameValuePair("phone_number",user.phone_number));
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
					
					showAlert("정상적으로 가입되었습니다");
					finish();
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
	
	/**
	 * Geocoder 클래스의 getFromLocation이 null일경우 HTTP를 통해 얻어온다.
	 * @param lat
	 * @param lng
	 * @return
	 */
	public JSONObject requestHttp(UserVO userVO) {

		HttpPost httpPost = new HttpPost("http://choijiho.com/gringrid/imgoing/join.php");
        HttpClient client = new DefaultHttpClient();
        List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > (5);
        nameValuePairs.add(new BasicNameValuePair("email", userVO.email));
        nameValuePairs.add(new BasicNameValuePair("phone_number", userVO.phone_number));
        nameValuePairs.add(new BasicNameValuePair("password", userVO.password));
        nameValuePairs.add(new BasicNameValuePair("gcm_reg_id", userVO.gcm_reg_id));
        
        HttpResponse response;
        
        StringBuilder stringBuilder = new StringBuilder();

        try {        	
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = client.execute(httpPost);
 
            HttpEntity entity = response.getEntity();            
            InputStream stream = entity.getContent();
            
            // 한글을 위해
            Reader reader=new InputStreamReader(stream);
         
            int b;
            while ((b = reader.read()) != -1) {
                stringBuilder.append((char) b);
            }
            
        } catch (ClientProtocolException e) {
            } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        
        try {
        	jsonObject = new JSONObject(stringBuilder.toString());
        	
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

	
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		
		TextView id_tv_input_info = (TextView)findViewById(R.id.id_tv_input_info);
		
		switch ( v.getId() ) {
		case R.id.id_et_email:
			id_tv_input_info.setText(R.string.email_input_info);
			break;
		case R.id.id_et_phone_number:
			id_tv_input_info.setText(R.string.phone_number_input_info);
			break;
		case R.id.id_et_password:
			id_tv_input_info.setText(R.string.password_input_info);
			break;
		case R.id.id_et_repassword:
			id_tv_input_info.setText(R.string.repassword_input_info);
			break;

		default:
			break;
		}
	}
}
