package net.gringrid.imgoing.util;

import java.util.Vector;

import android.app.Activity;

public class MyActivityManager {
	
	
	private static Vector<Activity> ACTIVITY_HISTORY = new Vector<Activity>();
	
	
	public static void addActivityAtFirst(Activity activity){
		
		int historySize = ACTIVITY_HISTORY.size();
		
		for ( int i=0; i<historySize; i++ ){
			if ( ACTIVITY_HISTORY.get(i).getClass().getName().equals(activity.getClass().getName() ) ){
				ACTIVITY_HISTORY.remove(i);
			}
		}
		ACTIVITY_HISTORY.add(0, activity);
	}
	
	
	public static void removeFirstActivity(){
		if ( ACTIVITY_HISTORY.size() > 0 ){
			ACTIVITY_HISTORY.remove( 0 );
		}
	}
	
	
	public static boolean historyIsEmpty(){
		boolean result = false;
		if ( ACTIVITY_HISTORY.size() == 0 ){
			result = true;
		}
		return result;
	}
	
	
	public static Activity getLastHistory(){
		return ACTIVITY_HISTORY.get(0);
	}


	public static void clearHistory() {
		ACTIVITY_HISTORY.clear();
	}
	
}
