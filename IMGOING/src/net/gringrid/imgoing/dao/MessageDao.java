package net.gringrid.imgoing.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import net.gringrid.imgoing.location.LocationUtil;
import net.gringrid.imgoing.util.DBHelper;
import net.gringrid.imgoing.util.Util;
import net.gringrid.imgoing.vo.MessageVO;
import net.gringrid.imgoing.vo.UserVO;

public class MessageDao {

	private DBHelper dbHelper = null;
	private SQLiteDatabase mDB = null;
	private Context mContext;
	
	// 단건 입력
	private static final String SQL_INSERT =
			String.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
					"MESSAGE",
					"sender",
					"receiver",
					"receiver_id",
					"start_time",
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
					",receiver_id"+
					",start_time"+
					",send_time"+
					",receive_time"+
					",latitude"+
					",longitude"+
					",interval"+
					",provider"+
					",location_name"+
					",near_metro_name "+
					"FROM MESSAGE");
	
	// 보낸사람 목록
	private static final String SQL_SEND_PERSON_LIST = 
	String.format("SELECT "+
			"DISTINCT receiver "+
			",receiver_id "+
			"FROM MESSAGE "+
			"WHERE sender = ? "+
			"ORDER BY start_time DESC");

	
	
	// 보낸목록 
	private static final String SQL_SEND_LIST = 
			String.format("SELECT "+
					"receiver "+
					",start_time "+
					",max(send_time) last_send_time "+
					"FROM MESSAGE "+
					"WHERE sender = ? "+
					"GROUP BY receiver, start_time "+
					"ORDER BY start_time DESC");
	
	// 한번 보낸 경로 목록 
	private static final String SQL_SEND_ONE_ROUTE_LIST = 
			String.format("SELECT "+
					"no"+
					",sender"+
					",receiver"+
					",receiver_id"+
					",start_time"+
					",send_time"+
					",receive_time"+
					",latitude"+
					",longitude"+
					",interval"+
					",provider"+
					",location_name"+
					",near_metro_name "+
					"FROM MESSAGE "+
					"WHERE sender = ? "+
					"AND start_time = ?"+
					"ORDER BY start_time DESC");
	
	
	// 받은목록
	private static final String SQL_RECEIVE_LIST = 
			String.format("SELECT "+
					"sender "+
					",start_time "+
					"FROM MESSAGE "+
					"WHERE receiver = ? "+
					"GROUP BY sender, start_time "+
					"ORDER BY start_time DESC");
	
	
	// 한사람에 대한 메시지 송신목록
	private static final String SQL_SEND_LIST_FOR_ONE = 
			String.format("SELECT "+
					"* "+
					"FROM MESSAGE "+
					"WHERE receiver = ? "+
					"AND start_time = ? "
					);
	
	// 보낸 메시지 힌건삭제
	private static final String SQL_DELETE_SEND_ONE =
			String.format(
				"DELETE FROM MESSAGE "+
				"WHERE sender = ? " +
				"AND start_time = ? ");
	
	// 받은 메시지 힌건삭제
	private static final String SQL_DELETE_RECEIVE_ONE =
			String.format(
				"DELETE FROM MESSAGE "+
				"WHERE receiver = ? " +
				"AND start_time = ? ");
	
	// 전체삭제
	private static final String SQL_DELETE_ALL = 
			"DELETE FROM MESSAGE";
	
	/**
	 * Constructor
	 * @param context
	 */
	public MessageDao(Context context) {
		mContext = context;
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
		stmt.bindString(3, vo.receiver_id);
		stmt.bindString(4, vo.start_time);
		stmt.bindString(5, vo.send_time);
		stmt.bindString(6, vo.receive_time);
		stmt.bindString(7, vo.latitude);
		stmt.bindString(8, vo.longitude);
		stmt.bindString(9, vo.interval);
		stmt.bindString(10, vo.provider);
		stmt.bindString(11, vo.location_name);
		stmt.bindString(12, vo.near_metro_name);
		
		
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
		try{
			if(cursor != null){
				cursor.moveToFirst();
			}
			return cursor;
		}finally{
			cursor.close();
		}
		
	}
	

	/**
	 * 보낸 사람 목록 조회 
	 */
	public ArrayList<String> querySendPersonList(){
		ArrayList<String> result = new ArrayList<String>();
		
		Cursor cursor = null;
		mDB = dbHelper.getDB();
		cursor = mDB.rawQuery(SQL_SEND_PERSON_LIST, new String[]{ Util.getMyPhoneNymber(mContext) });
		
		int index_receiver = cursor.getColumnIndex("receiver");
		
		try{
			if ( cursor.moveToFirst() ) {
				do{
					result.add( cursor.getString(index_receiver) );
				}while( cursor.moveToNext() );
			}
			return result;
		}finally{
			cursor.close();
		}
		
	}
	
	
	/**
	 * 보낸 메시지 목록 조회 
	 */
	public Cursor querySendList(){
		Cursor cursor = null;
		mDB = dbHelper.getDB();
		cursor = mDB.rawQuery(SQL_SEND_LIST, new String[]{ Util.getMyPhoneNymber(mContext) });
		
		if(cursor != null){
			cursor.moveToFirst();
		}
		return cursor;
	
	}
	
	
	/**
	 * 보낸 메시지 목록 조회 
	 */
	public Cursor querySendOneRouteList(String start_time){
		Cursor cursor = null;
		mDB = dbHelper.getDB();
		cursor = mDB.rawQuery(SQL_SEND_ONE_ROUTE_LIST, new String[]{ Util.getMyPhoneNymber(mContext), start_time });

		if(cursor != null){
			cursor.moveToFirst();
		}
		return cursor;

	}
	
	
	
	
	/**
	 * 받은 메시지 목록 조회
	 */
	public Cursor queryReceiveList(){
		Cursor cursor = null;
		mDB = dbHelper.getDB();
		cursor = mDB.rawQuery(SQL_RECEIVE_LIST, new String[]{ Util.getMyPhoneNymber(mContext) });

		if(cursor != null){
			cursor.moveToFirst();
		}
		return cursor;

	}
	
	
	
	/**
	 * 특정인에게 보낸 메시지 목록 조회 
	 */
	public Cursor querySendListForOne(String receiver, String start_time){
		Cursor cursor = null;
		mDB = dbHelper.getDB();
		cursor = mDB.rawQuery(SQL_SEND_LIST_FOR_ONE, new String[]{ receiver, start_time });
		
		if(cursor != null){
			cursor.moveToFirst();
		}
		return cursor;

	}

	/**
	 * 보낸 메시지 삭제
	 */
	public int deleteSendOne(String sender, String start_time){
		SQLiteStatement stmt = null;
		
		int resultCnt = 0;

		mDB = dbHelper.getDB();
		stmt = mDB.compileStatement(SQL_DELETE_SEND_ONE);
		
		stmt.bindString(1, sender);
		stmt.bindString(2, start_time);
		
		stmt.execute();
		
		return resultCnt;
	}

	/**
	 * 보낸 메시지 삭제
	 */
	public int deleteReceiveOne(String receiver, String start_time){
		SQLiteStatement stmt = null;
		
		int resultCnt = 0;

		mDB = dbHelper.getDB();
		stmt = mDB.compileStatement(SQL_DELETE_RECEIVE_ONE);
		
		stmt.bindString(1, receiver);
		stmt.bindString(2, start_time);
		
		stmt.execute();
		
		return resultCnt;

	}

	
	public void deleteAll() { 
		mDB = dbHelper.getDB();

		mDB.execSQL(SQL_DELETE_ALL);		
	}
	
}

