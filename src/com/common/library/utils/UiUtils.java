package com.common.library.utils;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class UiUtils {
	private static Handler sMainThreadHandler;
	private static long sLastClickTime;

	/**
	 * @return a {@link Handler} tied to the main thread.
	 */
	public static Handler getMainThreadHandler() {
		if (sMainThreadHandler == null) {
			// No need to synchronize -- it's okay to create an extra Handler,
			// which will be used
			// only once and then thrown away.
			sMainThreadHandler = new Handler(Looper.getMainLooper());
		}
		return sMainThreadHandler;
	}

	/**
	 * A thread safe way to show a Toast. Can be called from any thread.
	 * 
	 * @param context
	 *            context
	 * @param resId
	 *            Resource ID of the message string.
	 */
	public static void showToast(Context context, int resId) {
		showToast(context, context.getResources().getString(resId));
	}

	/**
	 * A thread safe way to show a Toast. Can be called from any thread.
	 * 
	 * @param context
	 *            context
	 * @param message
	 *            Message to show.
	 */
	public static void showToast(final Context context, final String message) {
		getMainThreadHandler().post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	/** Generics version of {@link Activity#findViewById} */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getViewOrNull(Activity parent, int viewId) {
        return (T) parent.findViewById(viewId);
    }

    /** Generics version of {@link View#findViewById} */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getViewOrNull(View parent, int viewId) {
        return (T) parent.findViewById(viewId);
    }

    /**
     * Same as {@link Activity#findViewById}, but crashes if there's no view.
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getView(Activity parent, int viewId) {
        return (T) checkView(parent.findViewById(viewId));
    }

    /**
     * Same as {@link View#findViewById}, but crashes if there's no view.
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getView(View parent, int viewId) {
        return (T) checkView(parent.findViewById(viewId));
    }

    private static View checkView(View v) {
        if (v == null) {
            throw new IllegalArgumentException("View doesn't exist");
        }
        return v;
    }

	/**
	 * check whether device is tablet or phone
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout 
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * At phone platform, set the phone screen is portrait; otherwise landscape.
	 */
	public static void setRequestOrizentation(Activity activity) {
		if (isTablet(activity)) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR | ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR | ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	/**
	 * Check App is running and can be seen.
	 */
	public static boolean isAppInForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
			}
		}
		return false;
	}

	/**
	 * Check android service component is running in background.
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * Check activity is opened even it was onPause() status.
	 */
	public static boolean isActivityOpened(Context context, String componentName) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
		if (taskInfos != null && taskInfos.size() > 0) {
			RunningTaskInfo taskInfo = taskInfos.get(0);
			if (taskInfo.topActivity.getClassName().equals(componentName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check screen is on or off.
	 */
	public static boolean isScreenOn(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();
	}

	/**
	 * Check user double clicked button, if true should do nothing,
	 * otherwise do things you want to do.
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - sLastClickTime;
		if (0 < timeD && timeD < 1000) {
			return true;
		}
		sLastClickTime = time;
		return false;
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
	public static void setSystemBarCanDim(Activity activity, boolean canDim){
		if(DeviceUtils.hasIceCreamSandwich()){
			View decorView = activity.getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
			if(canDim){
				decorView.setSystemUiVisibility(uiOptions);
			}else{
				// Calling setSystemUiVisibility() with a value of 0 to clears all flags.
				decorView.setSystemUiVisibility(0);
			}
		}
	}
	
	/**
	 * Hidden system status bar, you can also do by set style in Manifest for activity like below:<br>
	 * If you hide the system bars in your activity's onCreate() method and the user presses Home, 
	 * the system bars will reappear. When the user reopens the activity, onCreate() won't get called, 
	 * so the system bars will remain visible. If you want system UI changes to persist as the user
	 * navigates in and out of your activity, set UI flags in onResume() or onWindowFocusChanged().
	 * <pre>
	 * android:theme="@android:style/Theme.Holo.NoActionBar.Fullscreen"
	 * </pre>
	 * @param activity
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
	public static void hiddenStatusBar(Activity activity){
		 if (DeviceUtils.hasJellyBean()) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
	    }else{
	    	 activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    }
	}
}
