package com.common.library.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.text.TextUtils;
import android.widget.ListView;

public class ListPositonCaches {
	/**
	 * Save mapping for key in listView and position.
	 */
	private static ConcurrentHashMap<String, Integer> listViewPositionMapping = new ConcurrentHashMap<String, Integer>();
	
	/**
	 * Save mapping for listView.toString() and keys in listView
	 */
	private static ConcurrentHashMap<String, List<String>> listViewKeyMapping = new ConcurrentHashMap<String, List<String>>();

	/**
	 * Called like onItemClicked() of List View and so on.
	 * 
	 * @param currentKey
	 *            if save position only in same listView like Cloud Disk App,
	 *            currentKey may be file path. otherwise currentKey can be
	 *            listView's name or others.
	 * @param listView
	 */
	public static void savePosition(String currentKey, ListView listView) {
		if (TextUtils.isEmpty(currentKey) || listView == null) {
			throw new RuntimeException(	"currentKey and listView cannot be null.");
		}

		listViewPositionMapping.put(currentKey, listView.getFirstVisiblePosition());

		// remember relations between listView instance and keys
		List<String> keys = listViewKeyMapping.get(listView.toString());
		if (keys == null) {
			keys = new ArrayList<String>();
		}
		keys.add(currentKey);
		listViewKeyMapping.put(listView.toString(), keys);
	}
	
	/**
	 * Called like onItemClicked() of List View and so on.
	 * @param listView
	 */
	public static void savePosition(ListView listView) {
		savePosition(listView.toString(), listView);
	}

	/**
	 * Called like in method like onResume() or others where want to refresh
	 * list view.
	 */
	public static void restorePosition(String targetKey, ListView listView) {
		if (TextUtils.isEmpty(targetKey) || listView == null) {
			return;
		}

		if (listViewPositionMapping.get(targetKey) != null	&& listViewPositionMapping.get(targetKey) != 0) {
			listView.setSelection(listViewPositionMapping.get(targetKey));
		} else {
			listView.setSelection(listView.getTop());
		}
	}
	
	/**
	 * Called like in method like onResume() or others where want to refresh
	 * list view.
	 */
	public static void restorePosition(ListView listView) {
		restorePosition(listView.toString(), listView);
	}
}
