package net.gringrid.imgoing.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import net.gringrid.imgoing.util.DBHelper;
import net.gringrid.imgoing.vo.UserVO;

public class UserDao {

	private DBHelper dbHelper = null;
	private SQLiteDatabase mDB = null;
	
	
	// 단건 입력
	private static final String SQL_INSERT =
			String.format("INSERT INTO %s(%s,%s,%s,%s,%s) VALUES(?,?,?,?,?)",
					"USER",
					"email",
					"phone_number",
					"password");	
	// 한건조회
	private static final String SQL_SELECT_ONE =
			String.format("SELECT email, phone_number, password FROM USER");
	
	/**
	 * Constructor
	 * @param ctx
	 */
	public UserDao(Context ctx) {
		dbHelper = DBHelper.getInstance(ctx);		
	}
	
	/**
	 * 사용자 정보 입력
	 * @param vo
	 */
	public int insert(UserVO vo){
		SQLiteStatement stmt = null;
				
		mDB = dbHelper.getDB();	
		stmt = mDB.compileStatement(SQL_INSERT);
				
		stmt.bindString(1, vo.email);
		stmt.bindString(2, vo.phone_number);
		stmt.bindString(3, vo.password);
		
		
		try {
			stmt.execute();	
		} catch (Exception e) {
			return -1;
		}
		return 0;
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

