package net.gringrid.imgoing.util;

import java.util.Vector;

import android.app.Activity;
import android.util.Log;

public class MyActivityManager {
	
	
	private static Vector<Activity> ACTIVITY_HISTORY = new Vector<Activity>();
	
	
	public static void addActivityAtFirst(Activity activity){
		
		int historySize = ACTIVITY_HISTORY.size();
		
		String newActivityName = activity.getClass().getName();
		
		for ( int i=0; i<historySize; i++ ){
			if ( ACTIVITY_HISTORY.get(i).getClass().getName().equals( newActivityName ) ){
				ACTIVITY_HISTORY.remove(i);
				break;
			}
		}
		ACTIVITY_HISTORY.add(0, activity);
		printCurrentActivityHistory();
	}
	
	private static void printCurrentActivityHistory(){
		int historySize = ACTIVITY_HISTORY.size();
		
		for ( int i=0; i<historySize; i++ ){
			Log.d("jiho", String.format("[ %d] %s", i, ACTIVITY_HISTORY.get(i).getClass().getName()));
		}
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
		
		if(ACTIVITY_HISTORY.isEmpty()) {
			return null;
		}
		
		Activity history = ACTIVITY_HISTORY.firstElement();
		
		return history;
	
		
	}
	
	
	public static void removeLastHistory(){
		if ( ACTIVITY_HISTORY.size() > 0 ){
			ACTIVITY_HISTORY.removeElementAt(0);
		}
		printCurrentActivityHistory();
	}


	public static void clearHistory() {
		ACTIVITY_HISTORY.clear();
	}

	public static void remove(Activity activity) {
		String activityNameForRemove = activity.getClass().getName();
		int activitySize = ACTIVITY_HISTORY.size();
		
		for ( int i=0; i<activitySize; i++ ){
			if ( ACTIVITY_HISTORY.get(i).getClass().getName().equals( activityNameForRemove ) ){
				ACTIVITY_HISTORY.remove(i);
				break;
			}
		}
	}
	
}
