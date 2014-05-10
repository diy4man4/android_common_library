package com.common.library.preferences;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsUnity {
	public static SharedPreferences getSharedPreference(Context context, String prefFileName){
		return context.getSharedPreferences(prefFileName, Context.MODE_MULTI_PROCESS);
	}

	public static Editor getEditor(Context context, String prefsFileName){
		return getSharedPreference(context, prefsFileName).edit();
	}
	
	/**
	 * Read String type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @param defVal Value to return if this preference does not exist.
	 * @param encrypt whether encrypt or no.
	 * @return String Returns the preference value if it exists or return null.
	 */
	public static String getString(Context context, String prefsFileName, String key, String defVal){
		return getSharedPreference(context, prefsFileName).getString(key, defVal);
	}
	
	/**
	 * Read String type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @return String Returns the preference value if it exists or return null.
	 */
	public static String getString(Context context, String prefsFileName, String key) {
		return getSharedPreference(context, prefsFileName).getString(key, "");
	}
	
	/**
	 * Read Integer type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @param defVal Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue(0) .
	 */
	public static int getInt(Context context, String prefsFileName, String key, int defVal){
		return getSharedPreference(context, prefsFileName).getInt(key, defVal);
	}
	
	/**
	 * Read Integer type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @return Returns the preference value if it exists, or defValue(0) .
	 */
	public static int getInt(Context context, String prefsFileName, String key) {
		return getInt(context, prefsFileName, key, 0);
	}
	
	/**
	 * Read Float type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @param defVal Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue(0f) .
	 */
	public static float getFloat(Context context, String prefsFileName, String key, float defVal){
		return getSharedPreference(context, prefsFileName).getFloat(key, defVal);
	}
	
	/**
	 * Read Float type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @return Returns the preference value if it exists, or defValue(0f) .
	 */
	public static float getFloat(Context context, String prefsFileName, String key) {
		return getFloat(context, prefsFileName, key, 0f);
	}

	/**
	 * Read Long type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @return Returns the preference value if it exists, or defValue(0l) .
	 */
	public static long getLong(Context context, String prefsFileName, String key, long defVal){
		return getSharedPreference(context, prefsFileName).getLong(key, defVal);
	}
	
	/**
	 * Read Long type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @return Returns the preference value if it exists, or defValue(0l) .
	 */
	public static long getLong(Context context, String prefsFileName, String key) {
		return getLong(context, prefsFileName, key, 0l);
	}

	/**
	 * Read Boolean type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @param Value to return if this preference does not exist.
	 * @return Returns the preference value if it exists, or defValue(false) .
	 */
	public static boolean getBoolean(Context context, String prefsFileName, String key, boolean defVal){
		return getSharedPreference(context, prefsFileName).getBoolean(key, defVal);
	}

	/**
	 * Read Boolean type value from shared preference with its key.
	 * @param key The name of the preference to retrieve.
	 * @return Returns the preference value if it exists, or defValue(false) .
	 */
	public static boolean getBoolean(Context context, String prefsFileName, String key) {
		return getBoolean(context, prefsFileName, key, false);
	}
	
	/**
	 * Set a String value in the preferences editor default encrypt string.
	 * @param key The name of the preference to put.
	 * @param value Value to save.
	 */
	public static void putString(Context context, String prefsFileName, String key, String value) {
		getEditor(context, prefsFileName).putString(key, value).commit();
	}
	
	/**
	 * Set a Long type value in the preferences editor
	 * @param key The name of the preference to put.
	 * @param value Value to return if this preference does not exist.
	 */
	public static void putLong(Context context, String prefsFileName, String key, long value) {
		getEditor(context, prefsFileName).putLong(key, value).commit();
	}
	
	/**
	 * Set a Integer type value in the preferences editor
	 * @param key The name of the preference to put.
	 * @param value Value to return if this preference does not exist.
	 */
	public static void putInt(Context context, String prefsFileName, String key, int value) {
		getEditor(context, prefsFileName).putInt(key, value).commit();
	}

	/**
	 * Set a Float type value in the preferences editor
	 * @param key The name of the preference to put.
	 * @param value Value to return if this preference does not exist.
	 */
	public static void putFloat(Context context, String prefsFileName, String key, float value) {
		getEditor(context, prefsFileName).putFloat(key, value).commit();
	}
	
	/**
	 * Set a Boolean type value in the preferences editor
	 * @param key The name of the preference to put.
	 * @param value Value to return if this preference does not exist.
	 */
	public static void putBoolean(Context context, String prefsFileName, String key, boolean value) {
		getEditor(context, prefsFileName).putBoolean(key, value).commit();
	}
	
	/**
	 * Remove one value saved in shared preference with its key
	 * @param key
	 */
	public static void remove(Context context, String prefsFileName, String key) {
		getEditor(context, prefsFileName).remove(key).commit();
	}

	/**
	 * Remove all value saved in shared preference.
	 * @return boolean Return true if all value are removed successfully.
	 */
	public static void removeAll(Context context, String prefsFileName) {
		getEditor(context, prefsFileName).clear().commit();
	}
	
	/**
	 * Get all key-value saved in shared preference.
	 * @param context
	 * @param prefsFileName
	 * @return
	 */
	public static Map<String, ?> getAll(Context context, String prefsFileName){
		return getSharedPreference(context, prefsFileName).getAll();
	}
}
