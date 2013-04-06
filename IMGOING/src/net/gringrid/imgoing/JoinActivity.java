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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.gringrid.imgoing.util.Util;
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
			((EditText)view).setText(Util.getMyPhoneNymber(this));
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
			user.gcm_reg_id = Preference.GCM_REGISTRATION_ID; 
			
			Log.d("jiho", "email : "+user.email);
			Log.d("jiho", "phone_number : "+user.phone_number);
			Log.d("jiho", "password : "+user.password);
			Log.d("jiho", "repassword : "+user.repassword);
			
			requestHttp(user);
			
			
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
            Log.d("jiho", "stringBuilder : "+stringBuilder);
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

}
