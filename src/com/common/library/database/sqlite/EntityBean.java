package com.common.library.database.sqlite;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class EntityBean implements Serializable{
	private static final long serialVersionUID = -6833637753877258272L;

	// Newly created objects get this id
	public static final long NOT_SAVED = -1;
	
	// The id of the Content
	public long mId = NOT_SAVED;

	// All classes share this
	public static final String _ID = "_id";
	public static final String[] COUNT_COLUMNS = new String[] { "count(*)" };
	public static final String[] ID_PROJECTION = new String[] { _ID };
	public static final int ID_PROJECTION_COLUMN = 0;
	public static final String ID_SELECTION = _ID + " =?";

	// Write the Content into a ContentValues container
	public abstract ContentValues toContentValues();

	// Read the Content from a ContentCursor
	public abstract void restore(Cursor cursor);
	
	public boolean isSaved() {
		return mId != NOT_SAVED;
	}
}
