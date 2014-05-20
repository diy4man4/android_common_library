package com.common.library.database;

import android.content.ContentValues;

public class BatchSelection {
	private ContentValues contentValues;
	private String whereClause;
	private String[] whereArgs;

	/**
	 * Construct used for batch delete.
	 * @param whereClause
	 * @param whereArgs
	 */
	public BatchSelection(String whereClause, String[] whereArgs){
		this.whereClause = whereClause;
		this.whereArgs = whereArgs;
	}
	
	/**
	 * Construct used for batch update.
	 * @param contentValues
	 * @param whereClause
	 * @param whereArgs
	 */
	public BatchSelection(ContentValues contentValues, String whereClause, String[] whereArgs){
		this.contentValues = contentValues;
		this.whereClause = whereClause;
		this.whereArgs = whereArgs;
	}
	
	public ContentValues getContentValues() {
		return contentValues;
	}

	public void setContentValues(ContentValues contentValues) {
		this.contentValues = contentValues;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String[] getWhereArgs() {
		return whereArgs;
	}

	public void setWhereArgs(String[] whereArgs) {
		this.whereArgs = whereArgs;
	}

}
