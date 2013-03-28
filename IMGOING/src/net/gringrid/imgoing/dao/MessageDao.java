package net.gringrid.imgoing.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import net.gringrid.imgoing.location.LocationUtil;
import net.gringrid.imgoing.util.DBHelper;
import net.gringrid.imgoing.vo.MessageVO;
import net.gringrid.imgoing.vo.UserVO;

public class MessageDao {

	private DBHelper dbHelper = null;
	private SQLiteDatabase mDB = null;
	
	
	// 단건 입력
	private static final String SQL_INSERT =
			String.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES(?,?,?,?,?,?,?,?,?,?)",
					"MESSAGE",
					"sender",
					"receiver",
					"send_time",
					"receive_time",
					"latitude",
					"longitude",					
					"interval",
					"provider",
					"location_name",
					"near_metro_name");
	
	// 한건조회
	private static final String SQL_SELECT_ONE =
			String.format("SELECT email, phone_number, password FROM USER");
	
	
	
	// 전체조회
	private static final String SQL_SELECT_ALL =
			String.format("SELECT "+
					"no"+
					",sender"+
					",receiver"+
					",send_time"+
					",receive_time"+
					",latitude"+
					",longitude"+
					",interval"+
					",provider"+
					",location_name"+
					",near_metro_name "+
					"FROM MESSAGE");
	
	// 전체삭제
	private static final String SQL_DELETE_ALL = 
			"DELETE FROM MESSAGE";
	
	/**
	 * Constructor
	 * @param context
	 */
	public MessageDao(Context context) {
		dbHelper = DBHelper.getInstance(context);		
	}
	
	/**
	 * 장소 송/수신 정보 입력
	 * @param vo
	 */
	public int insert(MessageVO vo){
		SQLiteStatement stmt = null;
				
		mDB = dbHelper.getDB();	
		stmt = mDB.compileStatement(SQL_INSERT);
				
		stmt.bindString(1, vo.sender);
		stmt.bindString(2, vo.receiver);
		stmt.bindString(3, vo.send_time);
		stmt.bindString(4, vo.receive_time);
		stmt.bindString(5, vo.latitude);
		stmt.bindString(6, vo.longitude);
		stmt.bindString(7, vo.interval);
		stmt.bindString(8, vo.provider);
		stmt.bindString(9, vo.location_name);
		stmt.bindString(10, vo.near_metro_name);
		
		
		
		try {
			stmt.execute();	
		} catch (Exception e) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * 송/수신 모든 메시지 조회
	 * @return	cursor 모든 메시지
	 */
	public Cursor queryMessageAll(){
		Cursor cursor = null;

		mDB = dbHelper.getDB();

		cursor = mDB.rawQuery(SQL_SELECT_ALL, null);

		if(cursor != null){
			cursor.moveToFirst();
		}
		
		return cursor;
	}
	

	public void deleteAll() { 
		mDB = dbHelper.getDB();

		mDB.execSQL(SQL_DELETE_ALL);		
	}
	
	
	/**
	 * 사용자 정보 단건 조회
	 * @param vo
	 
	public UserVO getOne(){
		
		UserVO leguVO = new UserVO();
		
		mDB = dbHelper.getDB();
		Cursor cursor = null;
				
		cursor = mDB.rawQuery(SQL_SELECT_ONE);
		
		if ( cursor.moveToFirst() ){
			leguVO.email = cursor.getString(0);
			leguVO.phone_number = cursor.getString(1);
			leguVO.password = cursor.getString(2);
			}
		
		return leguVO;
				
	}
	*/
}
