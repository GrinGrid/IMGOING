package net.gringrid.imgoing;

import java.util.Vector;

import net.gringrid.imgoing.vo.ContactsVO;

public class Preference {
	// GCM 등록 ID : IntroActivity 에서 세팅 
	public static String GCM_REGISTRATION_ID = "";
	
	// 주소록 data : IntroActivity 에서 세팅  
	public static Vector<ContactsVO> CONTACTS_LIST;

	// 로그인 여부
	public static boolean IS_LOGIN = false;
	
	// 전화번호 : IntroActivity 에서 세팅 
	public static String PHONE_NUMBER = null;
	
	// 전송시간 
	public static String SEND_INTERVAL = "";
	
	
}
