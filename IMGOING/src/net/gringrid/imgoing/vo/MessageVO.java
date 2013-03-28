package net.gringrid.imgoing.vo;

public class MessageVO {	
	
	public int no;						// 일련번호
	public String sender;				// 보낸사람 email
	public String receiver;				// 받는사람 email
	public String send_time;			// 보낸시간
	public String receive_time;			// 받은시간
	public String latitude;				// 위도
	public String longitude	;			// 경도
	public String interval;				// 전송간격(분)
	public String provider;				// 제공자(GPS / NETWORK)
	public String location_name;		// 장소명
	public String near_metro_name;		// 주변지하철역
	
}

	