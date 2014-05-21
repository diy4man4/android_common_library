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
 * A convenient utils class to help do CRUD on sqlite database, every module in project should have a subclass of this class.
 * 
 * <p>
 * You must create a module DbUtils like below:
 * </p>
 * 
 * <pre>
 * public class ReportDbUtils extends SqliteUtils {
 * 	public static final String DATABASE_NAME = &quot;breakdown_report.db&quot;;
 * 	public static final int DATABASE_VERSION = 1;
 * 	private static ReportDbUtils singleton;
 * 
 * 	private synchronized static void initDbUtils() {
 * 		singleton = new ReportDbUtils();
 * 	}
 * 
 * 	public synchronized static SqliteUtils getDbUtils(Context context) {
 * 		if (singleton == null) {
 * 			initDbUtils();
 * 		}
 * 		return singleton;
 * 	}
 * 
 * 	&#064;Override
 * 	protected BaseDBHelper getDbHelper(Context context) {
 * 		return new ReportDbHelper(context);
 * 	}
 * }
 * 
 * </pre>
 */
public abstract class SqliteUtils {
	protected SQLiteDatabase mDatabase;
	protected BaseDBHelper mDbHelper;
	
	protected abstract BaseDBHelper getDbHelper(Context context);
	
	protected SqliteUtils(Context context){
		mDbHelper = getDbHelper(context);
		mDatabase = mDbHelper.getWritableDatabase();
	}
	
	public SQLiteDatabase getDatabase(){
		return mDatabase;
	}
	
	private void checkTransactionLocked(){
		if(mDbHelper.isTransactionLocked()){
			throw new IllegalStateException("transaction is locked by others");
		}
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
	
	/**
	 * Get all record count of table.
	 * 
	 * @param klass subclass of {@link EntityBean}
	 * @return count result
	 */
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
	
	/**
	 * Get record count of table with selections
	 * @param klass subclass of {@link EntityBean}
	 * @param selection
	 * @param selectionArgs
	 * @return count result
	 */
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
	
	/**
	 * Query and return subclass of {@link EntityBean} instance by table primary key id's value.
	 * @param klass subclass of {@link EntityBean}
	 * @param id primary key id's value
	 * @return subclass of {@link EntityBean}'s instance
	 */
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
	
	/**
	 * Query and return subclass of {@link EntityBean} instances as a list with size limit for pagination
	 * @param klass subclass of {@link EntityBean}
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return subclass of {@link EntityBean}'s instances list
	 */
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
	
	/**
	 * Query and return subclass of {@link EntityBean} instance with selections
	 * @param klass subclass of {@link EntityBean}
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return subclass of {@link EntityBean}'s instances list
	 */
	public <T extends EntityBean> List<T> find(Class<T> klass, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy) {
		return findWithLimit(klass, selection, selectionArgs, groupBy, having, orderBy, null);
	}
	
	/**
	 * Query and return the first record of subclass of {@link EntityBean} instance with selections.
	 * @param klass subclass of {@link EntityBean}
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return subclass of {@link EntityBean}'s instance
	 */
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
	 * 
	 * @param klass subclass of {@link EntityBean}
	 * @param beansubclass of {@link EntityBean}'s instance
	 */
	public <T extends EntityBean> long save(Class<T> klass, T bean) {
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		return mDatabase.insert(tableName, null, bean.toContentValues());
	}

	/**
	 * Save more than one records in batch mode.
	 * @param klass subclass of {@link EntityBean}
	 * @param beans subclass of {@link EntityBean}'s instance list
	 */
	public <T extends EntityBean> Long[] batchSave(Class<T> klass, List<T> beans){
		if(beans == null || beans.size() == 0){
			return null;
		}
		
		checkTransactionLocked();
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		List<Long> ids = new ArrayList<Long>();
		try{
			beginTransaction();
			for(T bean : beans){
				long id = mDatabase.insert(tableName, null, bean.toContentValues());
				ids.add(id);
			}
			setTransactionSuccessful();
			Long[] idArray = new Long[ids.size()];
			return ids.toArray(idArray);
		}finally{
			endTransaction();
		}
	}
	
	/**
	 * Update recored with content values.
	 * 
	 * @param klass subclass of {@link EntityBean}
	 * @param id the primary key id's value of table you want to update.
	 * @param values table columns to be updated were defined here.
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
	 * Update records with selection and values.
	 * 
	 * @param klass subclass of {@link EntityBean}
	 * @param where
	 * @param selectionArgs
	 * @param values
	 * @return updated records' count
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
	 * Update records in batch mode and update records with primary key's value.
	 * 
	 * <pre>
	 * Map<Long, ContentValues> beans = new HashMap<Long, ContentValues>();
	 * ContentValues values = new ContentValues();
	 * values.put(Record.Columns.USERNAME, "zhangsan");
	 * values.put(Record.Columns.PASSWORD, "123456");
	 * beans.put(101l, values);
	 * 
	 * values = new ContentValues();
	 * values.put(Record.Columns.USERNAME, "lisi");
	 * values.put(Record.Columns.PASSWORD, "654321");
	 * beans.put(102l, values);
	 * 
	 * DefaultDbUtils.get(context).batchUpdateWithId(Record.class, beans);
	 * </pre>
	 * 
	 * @param klass subclass of {@link EntityBean}
	 * @param beans update records definitions
	 * @return updated records' count
	 */
	public <T extends EntityBean> int batchUpdateWithId(Class<T> klass, Map<Long, ContentValues> beans){
		checkTransactionLocked();
		
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		int count = 0;
		try{
			beginTransaction();
			for(long id : beans.keySet()){
				int c = mDatabase.update(
						tableName,
						beans.get(id), 
						EntityBean._ID + "=?",
						new String[]{String.valueOf(id)});
				count += c;
			}
			setTransactionSuccessful();
			return count;
		}finally{
			endTransaction();
		}
	}
	
	/**
	 * Update records in batch mode and update records with selection clause.
	 * 
	 * <pre>
	 * List<BatchSelection> selections = new ArrayList<BatchSelection>();
	 * 
	 * ContentValues values = new ContentValues();
	 * values.put(Record.Columns.USERNAME, "zhangsan");
	 * values.put(Record.Columns.SCORE, 100);
	 * BatchSelection selection = new BatchSelection(values, Record.Columns.SEX + "=?", new String[]{"femal"});
	 * selections.add(selection);
	 * 
	 * values = new ContentValues();
	 * values.put(Record.Columns.SCORE, 100);
	 * selection = new BatchSelection(values, Record.Columns.SEX + "=?", new String[]{"male"});
	 * selections.add(selection);
	 * 
	 * DefaultDbUtils.getDbUtils(context).batchUpdateWithSelections(Record.class, selections);
	 * </>
	 * @param klass subclass of {@link EntityBean}
	 * @param selections
	 * @return updated records' count
	 */
	public <T extends EntityBean> int batchUpdateWithSelections(Class<T> klass, List<BatchSelection> selections){
		checkTransactionLocked();
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		int count = 0;
		try{
			beginTransaction();
			for(BatchSelection selection : selections){
				int c = mDatabase.update(
						tableName,
						selection.getContentValues(),
						selection.getWhereClause(),
						selection.getWhereArgs());
				count += c;
			}
			setTransactionSuccessful();
			return count;
		}finally{
			endTransaction();
		}
	}
	
	/**
	 * Delete record with its primary key id.
	 * 
	 * @param klass subclass of {@link EntityBean}
	 * @param id value of primary key id
	 * @return deleted record count
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
	 * 
	 *  @param klass subclass of {@link EntityBean}
	 *  @param selection
	 *  @param selectionArgs
	 *  @return deleted records' count
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
	 * 
	 * <pre>
	 * List&lt;Long&gt; ids = new ArrayList&lt;Long&gt;();
	 * ids.add(111l);
	 * ids.add(112l);
	 * ids.add(113l);
	 * 
	 * DefaultDbUtils.getDbUtils(context).batchDeleteWithId(Record.class, ids);
	 * </pre>
	 * 
	 * @param klass
	 *            subclass of {@link EntityBean}
	 * @param ids
	 *            primary key's values of records to be deleted.
	 * @return deleted record count
	 */
	public <T extends EntityBean> int batchDeleteWithId(Class<T> klass, List<Long> ids){
		if(ids != null && ids.size() == 0){
			return 0;
		}
		
		checkTransactionLocked();
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		int count = 0;
		try{
			beginTransaction();
			for(long id : ids){
				int c = mDatabase.delete(
						tableName, 
						EntityBean._ID + "=?", 
						new String[]{String.valueOf(id)});
				count += c;
			}
			setTransactionSuccessful();
			return count;
		}finally{
			endTransaction();
		}
	}
	
	/**
	 * Delete records with selections
	 * 
	 * <pre>
	 * List&lt;BatchSelection&gt; selections = new ArrayList&lt;BatchSelection&gt;();
	 * BatchSelection selection = new BatchSelection(Record.Columns.USERNAME + &quot;=?&quot;, new String[] { &quot;xiaoli&quot; });
	 * selections.add(selection);
	 * 
	 * selection = new BatchSelection(Record.Columns.HEIGHT + &quot;&gt;?&quot;, new String[] { &quot;190&quot; });
	 * selections.add(selection);
	 * 
	 * DefaultDbUtils.getDbUtils(context).batchDeleteWithSelections(Record.class, selections);
	 * </pre>
	 * 
	 * @param klass
	 *            subclass of {@link EntityBean}
	 * @param selections
	 * @return deleted records count
	 */
	public <T extends EntityBean> int batchDeleteWithSelections(Class<T> klass, List<BatchSelection> selections){
		if(selections == null || selections.size() == 0){
			return 0;
		}
		
		checkTransactionLocked();
		String tableName = TableMapping.getTableMapping().getTableName(klass);
		int count = 0;
		try{
			beginTransaction();
			for(BatchSelection selection : selections){
				int c = mDatabase.delete(
						tableName, 
						selection.getWhereClause(), 
						selection.getWhereArgs());
				count += c;
			}
			setTransactionSuccessful();
			return count;
		}finally{
			endTransaction();
		}
	}
	
	public void beginTransaction() {
		if(mDbHelper.isTransactionLocked()){
			throw new IllegalStateException("Transaction was started and locked currently.");
		}else{
			mDatabase.beginTransaction();
			mDbHelper.lockTransaction();
		}
	}

	public void setTransactionSuccessful() {
		if(mDbHelper.isTransactionLocked()){
			mDatabase.setTransactionSuccessful();
		}else{
			throw new IllegalStateException("Currently no transaction was started.");
		}
	}

	public void endTransaction() {
		if(mDbHelper.isTransactionLocked()){
			mDatabase.endTransaction();
			mDbHelper.unlockTransaction();
		}else{
			throw new IllegalStateException("Currently no transaction was started.");
		}
	}
}
