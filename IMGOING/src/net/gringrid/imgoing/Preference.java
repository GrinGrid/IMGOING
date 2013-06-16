package net.gringrid.imgoing;

import java.util.Vector;

import android.location.Location;

import net.gringrid.imgoing.vo.ContactsVO;

public class Preference {
	// GCM 등록 ID : IntroActivity 에서 세팅 
	public static String GCM_REGISTRATION_ID = "";
	
	// 주소록 data : IntroActivity 에서 세팅  
	public static Vector<ContactsVO> CONTACTS_LIST;

	// 최근 보냈던 HISTORY 주소록 data : IntroActivity 에서 세팅  
	public static Vector<ContactsVO> SEND_HISTORY_CONTACTS_LIST;
		
	// 로그인 여부
	public static boolean IS_LOGIN = false;
	
	// 전화번호 : IntroActivity 에서 세팅 
	public static String PHONE_NUMBER = null;
	
	// 전송시간 
	public static String SEND_INTERVAL = "";
	
	// 기본 전송간격
	public final static int DEFAULT_INTERVAL = 5;
	
	// 마지막 장소
	public static Location LAST_LOCATION = null;
	
	// 현재위치 검색 옵션
	public static int SETTING_LOCATION_SEARCH;
	public static final int SETTING_LOCATION_SEARCH_FINE = 0;
	public static final int SETTING_LOCATION_SEARCH_COARSE = 1;
	public static final int SETTING_LOCATION_SEARCH_DEFAULT = SETTING_LOCATION_SEARCH_FINE;
	
}
