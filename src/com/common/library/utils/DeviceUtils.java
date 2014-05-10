package com.common.library.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.os.Build.VERSION_CODES;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class DeviceUtils {
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
	public static String getDeviceModel(){
		return Build.MODEL;
	}
	
	public static String getDeviceVersion(Context context){
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceSoftwareVersion();
	}
	
	public static String getDeviceManufacturers(){
		return Build.MANUFACTURER;
	}
	
	public static boolean isEmulator(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (imei != null && imei.equals("000000000000000")) {
				return true;
			}
			return (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
		} catch (Exception ioe) {
		}
		return false;
	}
	
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}
	
	public static boolean isLocalCN(Context context){
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		return language.equals("zh");
	}
	
	public static int[] getDeviceMetrics(Activity activity){
		DisplayMetrics  dm = new DisplayMetrics();  
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;  
		int screenHeight = dm.heightPixels;  
		return new int[]{screenWidth, screenHeight};
	}
	
	public static boolean isCompatible(int apiLevel) {
        return android.os.Build.VERSION.SDK_INT >= apiLevel;
    }
	
	@TargetApi(VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (DeviceUtils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }
    
    public static boolean haveHighQulityForVideo() {
		if (Build.DEVICE.toLowerCase().equals("mione_plus")) {
			return false;
		}
		return true;
	}
}
