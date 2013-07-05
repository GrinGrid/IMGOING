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
			String.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES(?,?,?,?,?,?,?,?,?)",
					"MESSAGE",
					"sender",
					"receiver",
					"start_time",
					"latitude",
					"longitude",	
					"provider",
					"interval",					
					"wrk_time",
					"trans_yn");
	
	// 한건조회
	private static final String SQL_SELECT_ONE =
			String.format("SELECT email, phone_number, password FROM USER");
	
	
	
	// 전체조회
	private static final String SQL_SELECT_ALL =
			String.format("SELECT "+
					"no"+
					",sender"+
					",receiver"+
					",start_time"+
					",latitude"+
					",longitude"+
					",provider"+
					",interval"+					
					",wrk_time"+
					",trans_yn"+
					"FROM MESSAGE");
	
	// 보낸사람 목록
	private static final String SQL_SEND_PERSON_LIST = 
	String.format("SELECT "+
			"DISTINCT receiver "+
			"FROM MESSAGE "+
			"WHERE sender = ? "+
			"ORDER BY start_time DESC");

	
	
	// 보낸목록 
	private static final String SQL_SEND_LIST = 
			String.format("SELECT "+
					"receiver "+
					",start_time "+
					",max(wrk_time) last_send_time "+
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
					",start_time"+
					",latitude"+
					",longitude"+
					",provider"+
					",interval"+					
					",wrk_time"+
					",trans_yn "+
					"FROM MESSAGE "+
					"WHERE sender = ? "+
					"AND start_time = ?"+
					"ORDER BY start_time DESC");
	
	
	// 한번 받은 경로 목록  수
	private static final String SQL_RECEIVE_ONE_ROUTE_COUNT = 
			String.format("SELECT "+
					"count(*) cnt "+
					"FROM MESSAGE "+
					"WHERE sender = ? "+
					"AND start_time = ?"
					);
	
	
	// 받은목록
	private static final String SQL_RECEIVE_LIST = 
			String.format("SELECT "+
					"sender "+
					",start_time "+
					",max(wrk_time) last_send_time "+
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
	
		
	// 한사람에 대한 메시지 수신목록
	private static final String SQL_RECEOVE_LIST_FOR_ONE = 
			String.format("SELECT "+
					"* "+
					"FROM MESSAGE "+
					"WHERE sender = ? "+
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
		
	// 보낸메시지 삭제
	private static final String SQL_DELETE_SEND_MESSAGE = 
			String.format("DELETE FROM MESSAGE WHERE sender = ? ");
	
	// 받은메시지 삭제
	private static final String SQL_DELETE_RECEIVE_MESSAGE = 
			String.format("DELETE FROM MESSAGE WHERE receiver = ? ");
	
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
		stmt.bindString(3, vo.start_time);
		stmt.bindString(4, vo.latitude);
		stmt.bindString(5, vo.longitude);
		stmt.bindString(6, vo.provider);
		stmt.bindString(7, vo.interval);		
		stmt.bindString(8, vo.wrk_time);
		stmt.bindString(9, vo.trans_yn);
		
		
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
	 * 한사람이 보낸 경로의 받은 메시지 수
	 */
	public long queryReceiveOneRouteCount(String sender, String start_time){
		
		mDB = dbHelper.getDB();
		
		SQLiteStatement stmt = mDB.compileStatement(SQL_RECEIVE_ONE_ROUTE_COUNT);
		
		stmt.bindString(1, sender);
		stmt.bindString(2, start_time);
		
    	long count = stmt.simpleQueryForLong();
		return count;

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
	 * 특정인에게 받은 메시지 목록 조회 
	 */
	public Cursor queryReceiveListForOne(String sender, String start_time){
		Cursor cursor = null;
		mDB = dbHelper.getDB();
		cursor = mDB.rawQuery(SQL_RECEOVE_LIST_FOR_ONE, new String[]{ sender, start_time });
		
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
	
	public void deleteSendMessage() {
		SQLiteStatement stmt = null;
		
		mDB = dbHelper.getDB();	
		stmt = mDB.compileStatement(SQL_DELETE_SEND_MESSAGE);
				
		stmt.bindString(1, Util.getMyPhoneNymber(mContext));
		
		stmt.execute();
	}
	
	public void deleteReceiveMessage() { 
		SQLiteStatement stmt = null;
		
		mDB = dbHelper.getDB();	
		stmt = mDB.compileStatement(SQL_DELETE_RECEIVE_MESSAGE);
				
		stmt.bindString(1, Util.getMyPhoneNymber(mContext));
		
		stmt.execute();		
	}
	
}

