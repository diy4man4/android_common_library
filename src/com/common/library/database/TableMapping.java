package com.common.library.database;

import java.util.HashMap;
import java.util.Map;

import com.common.library.database.sqlite.EntityBean;

import android.text.TextUtils;

public class TableMapping {
	private final Map<Class<? extends EntityBean>, String> modelToTableMap 
		= new HashMap<Class<? extends EntityBean>, String>();
	private static TableMapping singleton = new TableMapping(); 
	
	private TableMapping() {}
	
	public static TableMapping getTableMapping() {
		return singleton;
	}
	
	public void addTableClassMapping(Class<? extends EntityBean> entityClass, String tableName){
		modelToTableMap.put(entityClass, tableName);
	}
	
	public String getTableName(Class<? extends EntityBean> entityClass){
		String tableName = modelToTableMap.get(entityClass);
		if(TextUtils.isEmpty(tableName)){
			throw new RuntimeException("The Table mapping of entity: " + entityClass.getName() 
					+ " not exists. Please add mapping in subclass of BaseDBHelper");
		}
		return tableName;
	}
}
