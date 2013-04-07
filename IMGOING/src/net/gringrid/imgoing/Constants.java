package net.gringrid.imgoing;

public final class Constants {
		
	public static final String BROADCAST_ACTION = "TEST_BROADCAST_ACTION";
	
	// 서비스가 수행중인지 여부를 파악하기 위한 서비스 경로 
	public static final String LOCATION_SERVICE = "net.gringrid.imgoing.location.SendCurrentLocationService";
	
	// 서버 수신시 정상 코드
	public static final String SUCCESS = "0000";
	
	// SharedPreferences 이름
	public static final String PREFS_NAME = "IMGOING";
	
	// Registration ID를 얻기 위해 GCM 서버로 보낼 Project id
	public static final String PROJECT_ID = "877042154251";
	public static final int COLUMN = 0;
	public static final int VALUE = 1;
		
}
