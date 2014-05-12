package com.common.library.database.sqlite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.common.library.database.BatchSelection;
import com.common.library.database.TableMapping;

/**
 * A convenient utils class to help do CRUD on sqlite database, 
 * every module in project should have a subclass of this class.
 * 
 * <p>You can do like below:</p>
 * 
 * <pre>
 *public class ReportDbUtils extends SqliteUtils{
 *	public static final String DATABASE_NAME = "breakdown_report.db";
 *	public static final int DATABASE_VERSION = 1;
*	private static ReportDbHelper mDbHelper;
*
*	public ReportDbUtils(Context context) {
*		super(mDbHelper);
*	}
*
*	public static SqliteUtils getDbUtils(Context context){
*		 return getDbUtils(mDbHelper == null ? mDbHelper = new ReportDbHelper(context) : mDbHelper);
*	}
*}</pre>
*/
public class SqliteUtils {
	private static SqliteUtils mSingleton;
	protected SQLiteDatabase mDatabase;
	protected BaseDBHelper mDbHelper;
	private volatile boolean mTransactionLocked = false;
	
	/**
	 * Construct which should be override by subclass.
	 * @param dbHelper
	 */
	protected SqliteUtils(BaseDBHelper dbHelper){
		mDbHelper = dbHelper;
		if(mDbHelper == null){
			throw new RuntimeException("dbHelper cannot be null");
		}
		mDatabase = mDbHelper.getWritableDatabase();
	}
	
	private synchronized static void initDbUtils(BaseDBHelper dbHelper){
		mSingleton = new SqliteUtils(dbHelper);
	}
	
	/**
	 * Subclass should have a method like below:<br>
	 * protected static SqliteUtils getDbUtils(Context context);
	 * @param dbHelper
	 * @return
	 */
	protected static SqliteUtils getDbUtils(BaseDBHelper dbHelper){
		if(mSingleton == null){
			initDbUtils(dbHelper);
		}
		return mSingleton;
	}
	
	public Context getContext(){
		return mDbHelper.getContext();
	}
	
	public SQLiteDatabase getDatabase(){
		return mDatabase;
	}
	
	// The Content sub class must have a no-arg constructor
	protected <T extends EntityBean> T getContent(Cursor cursor, Class<T> klass) {
		try {
			T content = klass.newInstance();
			content.mId = cursor.getLong(0);
			content.restore(cursor);
			return content;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <T extends EntityBean> int count(Class<T> klass){
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		Cursor c = mDatabase.query(tableName, EntityBean.COUNT_COLUMNS, null, null, null, null, null);
		if(c == null){
			throw new RuntimeException();
		}
		try{
			if (c.moveToFirst()) {
				return c.getInt(0);
			}else{
				return 0;
			}
		}finally{
			c.close();
		}
	}
	
	public <T extends EntityBean> int count(Class<T> klass, String selection, String[] selectionArgs){
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		Cursor c = mDatabase.query(tableName, EntityBean.COUNT_COLUMNS, null, null, null, null, null);
		if(c == null){
			throw new RuntimeException();
		}
		try{
			if (c.moveToFirst()) {
				return c.getInt(0);
			}else{
				return 0;
			}
		}finally{
			c.close();
		}
	}
	
	public <T extends EntityBean> T findById(Class<T> klass, long id){
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		Cursor c = mDatabase.query(
				tableName, null,	
				EntityBean._ID + "=?", 
				new String[]{String.valueOf(id)}, null, null, null);
		if(c == null){
			throw new RuntimeException();
		}
		try {
			if (c.moveToFirst()) {
				return getContent(c, klass);
			} else {
				return null;
			}
		} finally {
			c.close();
		}
	}
	
	public <T extends EntityBean> List<T> findWithLimit(Class<T> klass, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		Cursor c = mDatabase.query(tableName, null, selection, selectionArgs, groupBy, having, orderBy, limit);
		if (c == null) {
			throw new RuntimeException();
		}
		List<T> entities = new ArrayList<T>();
		try {
			while (c.moveToNext()) {
				entities.add(getContent(c, klass));
			}
		} finally {
			c.close();
		}
		return entities;
	}
	
	public <T extends EntityBean> List<T> find(Class<T> klass, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy) {
		return findWithLimit(klass, selection, selectionArgs, groupBy, having, orderBy, null);
	}
	
	public <T extends EntityBean> T findFirst(Class<T> klass, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy) {
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		Cursor c = mDatabase.query(
				tableName,
				null, 
				selection, 
				selectionArgs,
				groupBy,
				having,
				orderBy);
		
		if(c == null){
			throw new RuntimeException();
		}
		
		try {
			if (c.moveToFirst()) {
				return getContent(c, klass);
			} else {
				return null;
			}
		} finally {
			c.close();
		}
	}
	
	/**
	 * Insert table with module's properties.
	 */
	public <T extends EntityBean> long save(Class<T> klass, T bean) {
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		return mDatabase.insert(tableName, null, bean.toContentValues());
	}

	/**
	 * Save more than one records in batch mode.
	 */
	public <T extends EntityBean> Long[] batchSave(Class<T> klass, List<T> beans){
		if(beans == null || beans.size() == 0){
			return null;
		}
		
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		List<Long> ids = new ArrayList<Long>();
		try{
			mDatabase.beginTransaction();
			for(T bean : beans){
				long id = mDatabase.insert(tableName, null, bean.toContentValues());
				ids.add(id);
			}
			mDatabase.setTransactionSuccessful();
			Long[] idArray = new Long[ids.size()];
			return ids.toArray(idArray);
		}finally{
			mDatabase.endTransaction();
		}
	}
	
	/**
	 * Update recored with content values.
	 */
	public <T extends EntityBean> int update(Class<T> klass, long id, ContentValues values) {
		if (id == EntityBean.NOT_SAVED) {
			throw new UnsupportedOperationException();
		}
		
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		return mDatabase.update(
				tableName,
				values,
				EntityBean._ID + "=?", 
				new String[]{String.valueOf(id)});
	}
	
	/**
	 * Update records with selection.
	 */
	public <T extends EntityBean>int update(Class<T> klass, String where,
			String[] selectionArgs, ContentValues values){
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		return mDatabase.update(
				tableName,
				values, 
				where,
				selectionArgs);
	}
	
	/**
	 * Update records in batch mode and update records with explicit primary key.
	 * @param klass
	 * @param beans
	 * @return updated records' count
	 */
	public <T extends EntityBean> int batchUpdateWithId(Class<T> klass, Map<Long, ContentValues> beans){
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		int count = 0;
		try{
			mDatabase.beginTransaction();
			for(long id : beans.keySet()){
				int c = mDatabase.update(
						tableName,
						beans.get(id), 
						EntityBean._ID + "=?",
						new String[]{String.valueOf(id)});
				count += c;
			}
			mDatabase.setTransactionSuccessful();
			return count;
		}finally{
			mDatabase.endTransaction();
		}
	}
	
	/**
	 * Update records in batch mode and update records with selection clause.
	 * @param klass
	 * @param selections
	 * @return updated records' count
	 */
	public <T extends EntityBean> int batchUpdateWithSelections(Class<T> klass, List<BatchSelection> selections){
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		int count = 0;
		try{
			mDatabase.beginTransaction();
			for(BatchSelection selection : selections){
				int c = mDatabase.update(
						tableName,
						selection.getContentValues(),
						selection.getWhereClause(),
						selection.getWhereArgs());
				count += c;
			}
			mDatabase.setTransactionSuccessful();
			return count;
		}finally{
			mDatabase.endTransaction();
		}
	}
	
	/**
	 * Delete record with id in this subclass.
	 */
	public <T extends EntityBean> int delete(Class<T>klass,  long id) {
		if(id == EntityBean.NOT_SAVED){
			return 0;
		}
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		return mDatabase.delete(
				tableName, 
				EntityBean._ID + "=?",
				new String[]{String.valueOf(id)});
	}
	
	/**
	 * Delete records with selections.
	 */
	public <T extends EntityBean> int delete(Class<T> klass, String selection, String[] selectionArgs){
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		return mDatabase.delete(
				tableName,
				selection, 
				selectionArgs);
	}
	
	/**
	 * Delete records in batch mode.
	 */
	public <T extends EntityBean> int batchDeleteWithId(Class<T> klass, List<Long> ids){
		if(ids != null && ids.size() == 0){
			return 0;
		}
		
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		int count = 0;
		try{
			mDatabase.beginTransaction();
			for(long id : ids){
				int c = mDatabase.delete(
						tableName, 
						EntityBean._ID + "=?", 
						new String[]{String.valueOf(id)});
				count += c;
			}
			mDatabase.setTransactionSuccessful();
			return count;
		}finally{
			mDatabase.endTransaction();
		}
	}
	
	public <T extends EntityBean> int batchDeleteWithSelections(Class<T> klass, List<BatchSelection> selections){
		if(selections == null || selections.size() == 0){
			return 0;
		}
		
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		int count = 0;
		try{
			for(BatchSelection selection : selections){
				int c = mDatabase.delete(
						tableName, 
						selection.getWhereClause(), 
						selection.getWhereArgs());
				count += c;
			}
			mDatabase.setTransactionSuccessful();
			return count;
		}finally{
			mDatabase.endTransaction();
		}
	}
	
	public void beginTransaction() {
		if(mTransactionLocked){
			throw new IllegalStateException("Transaction was started and locked currently.");
		}else{
			mDatabase.beginTransaction();
		}
	}

	public void setTransactionSuccessful() {
		if(mTransactionLocked){
			mDatabase.setTransactionSuccessful();
		}else{
			throw new IllegalStateException("Currently no transaction was started.");
		}
	}

	public void endTransaction() {
		if(mTransactionLocked){
			mDatabase.endTransaction();
			mTransactionLocked = false;
		}else{
			throw new IllegalStateException("Currently no transaction was started.");
		}
	}
}
