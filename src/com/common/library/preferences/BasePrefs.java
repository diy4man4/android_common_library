package com.common.library.preferences;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.text.TextUtils;

import com.common.library.preferences.PrefsUnity;

/**
 * Preferences in every model should extends BasePrefs, you can do like below:<br>
 * <pre>
 * public class DefaultPrefs extends BasePrefs{
 *	private static DefaultPrefs singleton;
 *
 *	protected DefaultPrefs(Context context) {
 *		super(context);
 *	}
 *
 *	@Override
 *	protected String getModuleName() {
 *		return "default_prefs";
 *	}
 *	
 *	private static synchronized void initPrefs(Context context){
 *		singleton = new DefaultPrefs(context);
 *	}
 *	
 *	public  synchronized  static DefaultPrefs getPrefs(Context context){
 *		if(singleton == null){
 *			initPrefs(context);
 *		}
 *		return singleton;
 *	}
 *}
* </pre>
*/
public abstract class BasePrefs {
    private static final String KEY_NAMESPACE = "namespace";
	protected Context mContext;

	protected final ConcurrentHashMap<String, String> STRING_PREFS = new ConcurrentHashMap<String, String>();
	protected final ConcurrentHashMap<String, Integer> INTEGER_PREFS = new ConcurrentHashMap<String, Integer>();
	protected final ConcurrentHashMap<String, Boolean> BOOLEAN_PREFS = new ConcurrentHashMap<String, Boolean>();
	protected final ConcurrentHashMap<String, Long> LONG_PREFS = new ConcurrentHashMap<String, Long>();
	protected final ConcurrentHashMap<String, Float> FLOAT_PREFS = new ConcurrentHashMap<String, Float>();

	protected abstract String getModuleName();
	
	protected BasePrefs(Context context) {
		mContext = context;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	protected String getPrefsFileName() {
		String namespace = getNamespace();
		if(TextUtils.isEmpty(namespace)){
			throw new RuntimeException("No namespace is available.");
		}else{
			return namespace + "_" + getModuleName();
		}
	}
	
	public String getNamespace(){
		return getGlobalString(KEY_NAMESPACE);
	}
	
	public void setNamespace(String namespace){
		putGlobalString(KEY_NAMESPACE, namespace);
	}

	// preferences Api part
	// base api for getBoolean()
	private Boolean getBoolean(String prefsFileName, String key, boolean defVal, boolean isGlobal) {
		if (BOOLEAN_PREFS.get(key) == null) {
			BOOLEAN_PREFS.put(key, PrefsUnity.getBoolean(mContext, prefsFileName, key, defVal));
		}
		return BOOLEAN_PREFS.get(key);
	}

	public Boolean getBoolean(String key, boolean defVal) {
		return getBoolean(getPrefsFileName(), key, defVal, false);
	}

	public Boolean getBoolean(String key) {
		return getBoolean(getPrefsFileName(), key, false, false);
	}

	public Boolean getGlobalBoolean(String key, boolean defVal) {
		return getBoolean(mContext.getPackageName(), key, defVal, true);
	}

	public Boolean getGlobalBoolean(String key) {
		return getBoolean(mContext.getPackageName(), key, false, true);
	}

	// base api for getString()
	private String getString(String prefsFileName, String key, String defVal, boolean isGlobal) {
		// search in prefs file when does not exist in cache
		if (TextUtils.isEmpty(STRING_PREFS.get(key))) {
			STRING_PREFS.put(key, PrefsUnity.getString(mContext, prefsFileName, key, defVal));
		}
		
		String strVal = STRING_PREFS.get(key);
		// no need to decode for global string
		if (isGlobal) {
			return strVal;
		} else {
			/*String encryptKey = GlobalPrefs.getPreferences(mContext).getEncryptKey();
			if(TextUtils.isEmpty(encryptKey)){
				throw new RuntimeException("No encrypt key is available.");
			}
			return AESEncryptor.decrypt(encryptKey, strVal);*/
			return strVal;
		}
	}

	public String getString(String key, String defVal) {
		return getString(getPrefsFileName(), key, defVal, false);
	}

	public String getString(String key) {
		return getString(getPrefsFileName(), key, "", false);
	}

	public String getGlobalString(String key, String defVal) {
		return getString(mContext.getPackageName(), key, defVal, true);
	}

	public String getGlobalString(String key) {
		return getString(mContext.getPackageName(), key, "", true);
	}

	// base api for getInt()
	private Integer getInt(String prefsFileName, String key, int defVal, boolean isGloabl) {
		if (INTEGER_PREFS.get(key) == null) {
			INTEGER_PREFS.put(key, PrefsUnity.getInt(mContext, prefsFileName, key, defVal));
		}
		return INTEGER_PREFS.get(key);
	}

	public Integer getInt(String key, int defVal) {
		return getInt(getPrefsFileName(), key, defVal, false);
	}

	public Integer getInt(String key) {
		return getInt(getPrefsFileName(), key, 0, false);
	}

	public Integer getGlobalInt(String key, int defVal) {
		return getInt(mContext.getPackageName(), key, defVal, true);
	}

	public Integer getGlobalInt(String key) {
		return getInt(mContext.getPackageName(), key, 0, true);
	}

	// base api for getLong()
	private Long getLong(String prefsFileName, String key, long defVal, boolean isGlobal) {
		if (LONG_PREFS.get(key) == null) {
			LONG_PREFS.put(key, PrefsUnity.getLong(mContext, prefsFileName, key, defVal));
		}
		return LONG_PREFS.get(key);
	}

	public Long getLong(String key, long defVal) {
		return getLong(getPrefsFileName(), key, defVal, false);
	}

	public Long getLong(String key) {
		return getLong(getPrefsFileName(), key, 0l, false);
	}

	public Long getGlobalLong(String key, long defVal) {
		return getLong(getPrefsFileName(), key, defVal, true);
	}

	public Long getGlobalLong(String key) {
		return getLong(getPrefsFileName(), key, 0l, true);
	}

	// base api for getFloat()
	private Float getFloat(String prefsFileName, String key, float defVal, boolean isGloabl) {
		if (FLOAT_PREFS.get(key) == null) {
			FLOAT_PREFS.put(key, PrefsUnity.getFloat(mContext, prefsFileName, key, defVal));
		}
		return FLOAT_PREFS.get(key);
	}

	public Float getFloat(String key, float defVal) {
		return getFloat(getPrefsFileName(), key, defVal, false);
	}

	public Float getFloat(String key) {
		return getFloat(getPrefsFileName(), key, 0f, false);
	}

	public Float getGlobalFloat(String key, float defVal) {
		return getFloat(mContext.getPackageName(), key, defVal, true);
	}

	public Float getGlobalFloat(String key) {
		return getFloat(mContext.getPackageName(), key, 0f, true);
	}

	// base api for putString()
	private void putString(String prefsFileName, String key, String value, boolean isGloabl) {
		// no need encode for global string
		if(isGloabl){
			STRING_PREFS.put(key, value);
		}else{
//			String encryptKey = GlobalPrefs.getPreferences(mContext).getEncryptKey();
//			if(TextUtils.isEmpty(encryptKey)){
//				throw new RuntimeException("No encrypt key is available.");
//			}
//			value = AESEncryptor.encrypt(encryptKey, value);
			STRING_PREFS.put(key, value);
		}
		PrefsUnity.putString(mContext, prefsFileName, key, value);
	}

	public void putString(String key, String value) {
		putString(getPrefsFileName(), key, value, false);
	}

	public void putGlobalString(String key, String value) {
		putString(mContext.getPackageName(), key, value, true);
	}

	// base api for putBoolean()
	private void putBoolean(String prefsFileName, String key, boolean value, boolean isGloabl) {
		BOOLEAN_PREFS.put(key, value);
		PrefsUnity.putBoolean(mContext, prefsFileName, key, value);
	}

	public void putBoolean(String key, boolean value) {
		putBoolean(getPrefsFileName(), key, value, false);
	}

	public void putGlobalBoolean(String key, boolean value) {
		putBoolean(mContext.getPackageName(), key, value, true);
	}

	// base api for putInt()
	private void putInt(String prefsFileName, String key, int value, boolean isGloabl) {
		INTEGER_PREFS.put(key, value);
		PrefsUnity.putInt(mContext, prefsFileName, key, value);
	}

	public void putInt(String key, int value) {
		putInt(getPrefsFileName(), key, value, false);
	}

	public void putGlobalInt(String key, int value) {
		putInt(mContext.getPackageName(), key, value, true);
	}

	// base api for putFloat()
	private void putFloat(String prefsFileName, String key, float value, boolean isGloabl) {
		FLOAT_PREFS.put(key, value);
		PrefsUnity.putFloat(mContext, prefsFileName, key, value);
	}

	public void putFloat(String key, float value) {
		putFloat(getPrefsFileName(), key, value, false);
	}

	public void putGlobalFloat(String key, float value) {
		putFloat(mContext.getPackageName(), key, value, true);
	}

	// base api for putLong()
	private void putLong(String prefsFileName, String key, long value, boolean isGloabl) {
		LONG_PREFS.put(key, value);
		PrefsUnity.putLong(mContext, prefsFileName, key, value);
	}

	public void putLong(String key, long value) {
		putLong(getPrefsFileName(), key, value, false);
	}

	public void putGlobalLong(String key, long value) {
		putLong(mContext.getPackageName(), key, value, true);
	}

	// base api for removeString()
	private void removeString(String prefsFileName, String key, boolean isGloabl) {
		STRING_PREFS.remove(key);
		PrefsUnity.remove(mContext, prefsFileName, key);
	}

	public void removeString(String key) {
		removeString(getPrefsFileName(), key, false);
	}

	public void removeGlobalString(String key) {
		removeString(mContext.getPackageName(), key, true);
	}

	// base api for removeBoolean()
	private void removeBoolean(String prefsFileName, String key, boolean isGloabl) {
		BOOLEAN_PREFS.remove(key);
		PrefsUnity.remove(mContext, prefsFileName, key);
	}

	public void removeBoolean(String key) {
		removeBoolean(getPrefsFileName(), key, false);
	}

	public void removeGlobalBoolean(String key) {
		removeBoolean(mContext.getPackageName(), key, true);
	}

	// base api for removeInteger()
	private void removeInteger(String prefsFileName, String key, boolean isGloabl) {
		INTEGER_PREFS.remove(key);
		PrefsUnity.remove(mContext, prefsFileName, key);
	}

	public void removeInt(String key) {
		removeInteger(getPrefsFileName(), key, false);
	}

	public void removeGlobalInt(String key) {
		removeInteger(mContext.getPackageName(), key, true);
	}

	// base api for removeFloat()
	private void removeFloat(String prefsFileName, String key, boolean isGloabl) {
		FLOAT_PREFS.remove(key);
		PrefsUnity.remove(mContext, prefsFileName, key);
	}

	public void removeFloat(String key) {
		removeFloat(getPrefsFileName(), key, false);
	}

	public void removeGlobalFloat(String key) {
		removeFloat(mContext.getPackageName(), key, true);
	}

	// base api for removeLong()
	private void removeLong(String prefsFileName, String key, boolean isGloabl) {
		LONG_PREFS.remove(key);
		PrefsUnity.remove(mContext, prefsFileName, key);
	}

	public void removeLong(String key) {
		removeLong(getPrefsFileName(), key, false);
	}

	public void removeGlobalLong(String key) {
		removeLong(mContext.getPackageName(), key, true);
	}

	/**
	 * Called when account name and sn are available
	 */
	public void removeAll() {
		PrefsUnity.removeAll(mContext, getPrefsFileName());
	}

	public void removeGlobalAll() {
		PrefsUnity.removeAll(mContext, mContext.getPackageName());
	}
	
	/**
	 * Should be called by sub-app since ServiceConnection may not connected then cannot get namespace and platform account name
	 * @param namespace
	 * @param accountName
	 */
	public void clearAll(String namespace, String accountName){
		PrefsUnity.removeAll(mContext, namespace + "_" + getModuleName());
		removeGlobalAll();
	}

	/**
	 * Only remove cached shared preferences value.
	 */
	public void removeCachedPrefs() {
		STRING_PREFS.clear();
		BOOLEAN_PREFS.clear();
		INTEGER_PREFS.clear();
		FLOAT_PREFS.clear();
		LONG_PREFS.clear();
	}

	public Map<String, ?> getAll() {
		return PrefsUnity.getAll(mContext, getPrefsFileName());
	}
}