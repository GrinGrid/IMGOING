package net.gringrid.imgoing.vo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MessageVO implements Parcelable{	
	
	public int no;						// 일련번호
	public String sender;				// 보낸사람 email
	public String receiver;				// 받는사람 email
	public String receiver_id;			// 받는사람 id
	public String start_time;			// 시작시간
	public String send_time;			// 보낸시간
	public String receive_time;			// 받은시간
	public String latitude;				// 위도
	public String longitude	;			// 경도
	public String interval;				// 전송간격(분)
	public String provider;				// 제공자(GPS / NETWORK)
	public String location_name;		// 장소명
	public String near_metro_name;		// 주변지하철역
	
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
		dest.writeString(receiver);			
		dest.writeString(receiver_id);		
		dest.writeString(start_time);		
		dest.writeString(send_time);		
		dest.writeString(receive_time);		
		dest.writeString(latitude);			
		dest.writeString(longitude);		
		dest.writeString(interval);			
		dest.writeString(provider);			
		dest.writeString(location_name);	
		dest.writeString(near_metro_name);
	}
	
	public void readFromParcel(Parcel in) {
		no = in.readInt();
		sender = in.readString();			
		receiver = in.readString();
		receiver_id = in.readString();		
		start_time = in.readString();		
		send_time = in.readString();		
		receive_time = in.readString();
		latitude = in.readString();
		longitude = in.readString();
		interval = in.readString();
		provider = in.readString();
		location_name = in.readString();
		near_metro_name = in.readString();	
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
		Log.d("jiho", "MessageVO.sender 			: "+sender				);
		Log.d("jiho", "MessageVO.receiver 			: "+receiver			);
		Log.d("jiho", "MessageVO.receiver_id 		: "+receiver_id			);
		Log.d("jiho", "MessageVO.start_time 		: "+start_time			);
		Log.d("jiho", "MessageVO.send_time 			: "+send_time			);
		Log.d("jiho", "MessageVO.receive_time 		: "+receive_time		);		
		Log.d("jiho", "MessageVO.latitude 			: "+latitude			);
		Log.d("jiho", "MessageVO.longitude 			: "+longitude			);
		Log.d("jiho", "MessageVO.interval 			: "+interval			);
		Log.d("jiho", "MessageVO.provider 			: "+provider			);
		Log.d("jiho", "MessageVO.location_name 		: "+location_name		);
		Log.d("jiho", "MessageVO.near_metro_name 	: "+near_metro_name		);
	}
}

	