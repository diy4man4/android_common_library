package com.common.library.utils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.widget.ListView;

public class ListPositonCaches {
	private static Map<String, Integer> positionCache = new HashMap<String, Integer>();
	private static Map<ListView, List<String>> listViewKeyCache = new HashMap<ListView, List<String>>();
	
	private static SoftReference<Map<String, Integer>>  positionCachePrefs = new SoftReference<Map<String,Integer>>(positionCache);
	private static SoftReference<Map<ListView, List<String>>> listViewKeyCachePrefs = new SoftReference<Map<ListView,List<String>>>(listViewKeyCache);

	/**
	 * Called like onItemClicked() of List View and so on.
	 */
	public static void savePosition(String currentKey, ListView listView) {
		if (TextUtils.isEmpty(currentKey) || listView == null) {
			throw new RuntimeException("currentKey and listView cannot be null.");
		}
		
		Map<String, Integer> cachedListPosition = positionCachePrefs.get();
		if(cachedListPosition != null){
			cachedListPosition.put(currentKey, listView.getFirstVisiblePosition());
		}
		
		// remember relations between listView instance and keys
		Map<ListView, List<String>> listViewKeyCaches = listViewKeyCachePrefs.get();
		if(listViewKeyCaches != null){
			List<String> keys = listViewKeyCaches.get(listView);
			if(keys == null){
				keys = new ArrayList<String>();
			}
			keys.add(currentKey);
			listViewKeyCaches.put(listView, keys);
		}
	}

	/**
	 * Called like in method like onResume() or others where want to refresh list view.
	 */
	public static void restorePositon(String targetKey, ListView listView) {
		if (TextUtils.isEmpty(targetKey) || listView == null) {
			return;
		}
		
		Map<String, Integer> cachedListPosition = positionCachePrefs.get();
		if(cachedListPosition != null){
			if (cachedListPosition.get(targetKey) != null && cachedListPosition.get(targetKey) != 0) {
				listView.setSelection(cachedListPosition.get(targetKey));
			} else {
				listView.setSelection(listView.getTop());
			}
		}
	}
}
