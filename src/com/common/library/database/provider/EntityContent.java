package com.common.library.database.provider;

import java.io.Serializable;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract class EntityContent implements Serializable{
	private static final long serialVersionUID = 2465462005204648975L;
	protected static final String PARAMETER_LIMIT = "limit";
	public static final String UNKNOWN_URI_LOG = "Unknown URI ";
	protected Uri mContentUri;
	public Uri uri;

	// All classes share this
	public static final String _ID = "_id";
	
	public static final String[] COUNT_COLUMNS = new String[] { "count(*)" };
	public static final String[] ID_PROJECTION = new String[] { _ID };
	public static final int ID_PROJECTION_COLUMN = 0;
	public static final String ID_SELECTION = _ID + " =?";

	// Newly created objects get this id
	public static final long NOT_SAVED = -1;
	
	// The id of the Content
	public long mId = NOT_SAVED;

	// Write the Content into a ContentValues container
	public abstract ContentValues toContentValues();

	// Read the Content from a ContentCursor
	public abstract void restore(Cursor cursor);
	
	public Uri getUri() {
		if (uri == null) {
			uri = ContentUris.withAppendedId(mContentUri, mId);
		}
		return uri;
	}

	public boolean isSaved() {
		return mId != NOT_SAVED;
	}
}
