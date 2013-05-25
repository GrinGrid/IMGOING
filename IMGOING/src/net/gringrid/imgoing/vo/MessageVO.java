package net.gringrid.imgoing.vo;

import android.util.Log;

public class MessageVO {	
	
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

	