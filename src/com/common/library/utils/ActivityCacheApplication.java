package com.common.library.utils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ActivityCacheApplication extends Application {
	private List<SoftReference<Activity>> mCachedActivities = new ArrayList<SoftReference<Activity>>();

	public void addNewActivity(Activity activity) {
		if (!mCachedActivities.contains(activity)) {
			mCachedActivities.add(new SoftReference<Activity>(activity));
		}
	}

	public void clearCachedActivities() {
		for (SoftReference<Activity> reference : mCachedActivities) {
			Activity activity = reference.get();
			if (activity != null) {
				activity.finish();
				activity = null;
			}
		}
	}
}
