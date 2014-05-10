package com.common.library.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.common.library.database.TableMapping;

public abstract class BaseDBHelper extends SQLiteOpenHelper {
	private Context mContext;
	
	public abstract void configTableClassMapping(TableMapping tableMapping);
	
	public BaseDBHelper(Context context, String databaseName, int version) {
		super(context, databaseName, null, version);
		mContext = context;
		configTableClassMapping(TableMapping.getTableMapping());
	}
	
	public Context getContext(){
		return mContext;
	}

}
