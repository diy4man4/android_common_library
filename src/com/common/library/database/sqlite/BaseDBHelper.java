package com.common.library.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.common.library.database.TableMapping;

public abstract class BaseDBHelper extends SQLiteOpenHelper {
	private Context mContext;
	
	/**
	 * Transaction lock flag, everytime do database transaction job should check this. 
	 */
	private boolean mTransactionLocked = false;
	
	/**
	 * You should add all mappings between class and table after extends BaseDBhelper.<br>
	 * @param tableMapping
	 */
	public abstract void configTableClassMapping(TableMapping tableMapping);
	
	public BaseDBHelper(Context context, String databaseName, int version) {
		super(context, databaseName, null, version);
		mContext = context;
		configTableClassMapping(TableMapping.getTableMapping());
	}
	
	/**
	 * <p>Before {@code SqliteUtils.beginTransaction()} must check this first and it should be unlocked,
	 *  otherwise exception should be throw out.
	 *  <p>Before {@code SqliteUtils#setTransactionSuccessful()} must check this first and should be locked,
	 *  otherwise exception should be throw out.
	 *  <p>Before {@code SqliteUtils#endTransaction()} should check this first and should be locked,
	 *  otherwise exception should be throw out..
	 */
	public boolean isTransactionLocked(){
		return mTransactionLocked;
	}
	
	/**
	 * After {@code SqliteUtils.beginTransaction()}  must lock it.
	 */
	public void lockTransaction(){
		mTransactionLocked = true;
	}
	
	/**
	 * After {@code SqliteUtils#endTransaction()} must unlock it.
	 */
	public void unlockTransaction(){
		mTransactionLocked = false;
	}
	
	public Context getContext(){
		return mContext;
	}

}
