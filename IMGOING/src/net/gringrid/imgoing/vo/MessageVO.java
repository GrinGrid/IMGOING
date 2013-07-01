package net.gringrid.imgoing.vo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MessageVO implements Parcelable{	
	
	public int no;						// 일련번호
	public String sender;				// 보낸사람 email
	public String sender_name;			// 보낸사람 이름
	public String receiver;				// 받는사람 email
	public String receiver_name;		// 받는사람 이름	
	public String start_time;			// 시작시간
	public String latitude;				// 위도
	public String longitude	;			// 경도
	public String provider;				// 제공자(GPS / NETWORK)
	public String interval;				// 전송간격(분)	
	public String wrk_time;				// 입력된 시간
	public String trans_yn;				// 전송확인
	
	public MessageVO() {
		
	}
	
	public MessageVO(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(no);
		dest.writeString(sender);		
		dest.writeString(sender_name);
		dest.writeString(receiver);
		dest.writeString(receiver_name);
		dest.writeString(start_time);		
		dest.writeString(latitude);			
		dest.writeString(longitude);
		dest.writeString(provider);
		dest.writeString(interval);
		dest.writeString(wrk_time);	
		dest.writeString(trans_yn);
	}
	
	public void readFromParcel(Parcel in) {
		no = in.readInt();
		sender = in.readString();
		sender_name = in.readString();
		receiver = in.readString();
		receiver_name = in.readString();
		start_time = in.readString();		
		latitude = in.readString();
		longitude = in.readString();		
		provider = in.readString();
		interval = in.readString();		
		wrk_time = in.readString();
		trans_yn = in.readString();	
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { 
		public MessageVO createFromParcel(Parcel in) { 
			return new MessageVO(in); 
		}   
		public MessageVO[] newArray(int size) { 
			return new MessageVO[size]; 
		} 
	};
		
	public void print(){
		Log.d("jiho", "MessageVO.no 				: "+no					);
		Log.d("jiho", "MessageVO.sender_name		: "+sender_name			);
		Log.d("jiho", "MessageVO.receiver 			: "+receiver			);
		Log.d("jiho", "MessageVO.receiver_name		: "+receiver_name		);
		Log.d("jiho", "MessageVO.start_time 		: "+start_time			);
		Log.d("jiho", "MessageVO.latitude 			: "+latitude			);
		Log.d("jiho", "MessageVO.longitude 			: "+longitude			);
		Log.d("jiho", "MessageVO.provider 			: "+provider			);
		Log.d("jiho", "MessageVO.interval 			: "+interval			);		
		Log.d("jiho", "MessageVO.wrk_time 			: "+wrk_time			);
		Log.d("jiho", "MessageVO.trans_yn 			: "+trans_yn			);
	}
}

	