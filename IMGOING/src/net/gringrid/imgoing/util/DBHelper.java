package net.gringrid.imgoing.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

	private static DBHelper instance;
	
	private static SQLiteDatabase db;
	
	
	/**
	 * Database명
	 */
	private static final String DATABASE_NAME = "IMGOING.db";
	
	
	/**
	 * Database version
	 * Database 파일에 저장되며 DB에 접속(getWritable(Readable)Database)할때 버전을 비교하여
	 * 다를경우 : onCreate -> onUpgrade 실행
	 * 같을경우 : onOpen 실행
	 */
	private static final int DATABASE_VERSION = 5;
	
	
	/**
	 *  [ Table 정보 ]
	 *	사용자 테이블		: USER
	 *	메시지 테이블		: MESSAGE 
	 */
	private static final String TABLE_USER =  
			"CREATE TABLE USER ("+
				"email 			TEXT NOT NULL, "+
				"phone_number 	TEXT, "+
				"passowrd 		TEXT, "+
				"PRIMARY KEY (email)"+				
				")";
	
	private static final String TABLE_MESSAGE 		=  
			"CREATE TABLE MESSAGE ("+
				"no	    			INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, "+
				"sender 			TEXT NOT NULL, "+
				"receiver			TEXT NOT NULL, "+
				"start_time			TEXT NOT NULL, "+
				"send_time			TEXT NULL, "+
				"receive_time		TEXT NULL, "+
				"latitude	 		TEXT NULL, "+				
				"longitude	 		TEXT NULL, "+
				"interval			TEXT NULL, "+
				"provider			TEXT NULL, "+
				"location_name		TEXT NULL, "+
				"near_metro_name	TEXT NULL  "+
				")";
	
	/**
	 * SingleTon으로 구현
	 * @param ctx
	 */
	private DBHelper(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	
    public static DBHelper getInstance(Context context){
        if(instance == null)
        {
        	synchronized(DBHelper.class)
        	{
	            instance = new DBHelper(context);
	            db = instance.getWritableDatabase();
        	}
        }
        return instance;
    }

    public SQLiteDatabase getDB(){
    	if ( db == null ){
    		db = instance.getWritableDatabase();
    	}
    	return db;
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		 // 사용자 테이블
		 db.execSQL(TABLE_USER);
		 
		 // 메시지 테이블
		 db.execSQL(TABLE_MESSAGE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.d("jiho", "===================================");
		Log.d("jiho", "DB onUpgrade");
		Log.d("jiho", "TABLE_USER SCHEMA : "+TABLE_USER);
		Log.d("jiho", "TABLE_MESSAGE SCHEMA : "+TABLE_MESSAGE);
		Log.d("jiho", "===================================");
		 
		db.execSQL("DROP TABLE IF EXISTS USER");
		db.execSQL("DROP TABLE IF EXISTS MESSAGE");
		onCreate(db);	
	}

	
	/**
	 * DBHelper, SQLiteDatabase Close
	 */
    public void close(){
        if(instance != null){
            instance = null;
            db.close();
        }
    }

}
