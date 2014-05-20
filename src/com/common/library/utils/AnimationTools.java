package com.common.library.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;

import com.common.library.R;

public class AnimationTools {

	public static final String EXTRA_OPEN_ANIMATION_TYPE = "open_animation_type";
	
	public static interface AnimationTypes {
		public static final int NORMAL = 0;
		public static final int SLIDE_OPEN_FROM_LEFT = 1;
		public static final int SLIDE_OPEN_FROM_LEFT2 = 2;
		public static final int SLIDE_OPEN_FROM_RIGHT = 3;
		public static final int SLIDE_OPEN_FROM_RIGHT2 = 4;
	}

	public static void openWithoutAnimation(Activity activity, Intent intent) {
		activity.startActivity(intent);
		activity.overridePendingTransition(0, 0);
	}

	private static void slideOpenWithAnimation(Activity activity, Intent intent, int inAnimation, int outAnimation) {
		if (DeviceUtils.isCompatible(16)) {
			ActivityOptions opt = ActivityOptions.makeCustomAnimation(activity, inAnimation, outAnimation);
			activity.startActivity(intent, opt.toBundle());
		} else {
			activity.startActivity(intent);
			activity.overridePendingTransition(inAnimation, outAnimation);
		}
	}

	/**
	 * Open new activity with animation slide from left to right and current
	 * activity also slide with new activity to be opened.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void slideOpenFromLeft(Activity activity, Intent intent) {
		slideOpenWithAnimation(activity, intent, R.anim.activity_slide_in_from_left, R.anim.activity_slide_out_to_right);
	}

	/**
	 * Open new activity with animation slide from left to right, but current
	 * activity remain unchanged state.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void slideOpenFromLeft2(Activity activity, Intent intent) {
		slideOpenWithAnimation(activity, intent, R.anim.activity_slide_in_from_left, R.anim.activity_no_animation);
	}

	/**
	 * Open new activity with animation slide from right to left and current
	 * activity also slide with new activity to be opened.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void slideOpenFromRight(Activity activity, Intent intent) {
		slideOpenWithAnimation(activity, intent, R.anim.activity_slide_in_from_right, R.anim.activity_slide_out_to_left);
	}

	/**
	 * Open new activity with animation slide from right to left but current
	 * activity remain unchanged state.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void slideOpenFromRight2(Activity activity, Intent intent) {
		slideOpenWithAnimation(activity, intent, R.anim.activity_slide_in_from_right, R.anim.activity_no_animation);
	}

	/**
	 * Close current activity with animation slide from right to left and
	 * overrided activity slide with activity to be closed.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void slideCloseToLeft(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.activity_slide_in_from_right, R.anim.activity_slide_out_to_left);
	}

	/**
	 * Close current activity with animation slide from right to left and
	 * overrided activity remain unchanged state.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void slideCloseToLeft2(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_slide_out_to_left);
	}

	/**
	 * Close current activity with animation slide from left to right and
	 * overrided activity slide with activity to be closed.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void slideCloseToRight(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.activity_slide_in_from_left, R.anim.activity_slide_out_to_right);
	}

	/**
	 * Close current activity with animation slide from left to right and only
	 * activity to be closed have animation.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void slideCloseToRight2(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_slide_out_to_right);
	}

	/**
	 * Open new activity with slide in animation.
	 * 
	 * @param activity
	 *            current activity.
	 * @param intent
	 *            contain activity you want to open.
	 */
	public static void fadeOpen(Activity activity, Intent intent) {
		if (DeviceUtils.isCompatible(16)) {
			ActivityOptions opt = ActivityOptions.makeCustomAnimation(activity, android.R.anim.fade_in, android.R.anim.fade_out);
			activity.startActivity(intent, opt.toBundle());
		} else {
			activity.startActivity(intent);
			activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	}

	/**
	 * Close current activity with fade out animation.
	 * 
	 * @param activity
	 *            current activity
	 */
	public static void fadeClose(Activity activity) {
		activity.finish();
		// activity.overridePendingTransition(android.R.anim.fade_out,
		// android.R.anim.fade_in);
	}

	private static void slideOpenForResultWithAnimation(Activity activity, Intent intent, int requestCode, int inAnimation, int outAnimation) {
		if (DeviceUtils.isCompatible(16)) {
			ActivityOptions opt = ActivityOptions.makeCustomAnimation(activity, inAnimation, outAnimation);
			activity.startActivityForResult(intent, requestCode, opt.toBundle());
		} else {
			activity.startActivityForResult(intent, requestCode);
			activity.overridePendingTransition(inAnimation, outAnimation);
		}
	}

	/**
	 * Open new activity for result with animation of sliding from left to
	 * right, but current activity remain unchanged state.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 * @param requestCode
	 *            original request code
	 */
	public static void slideOpenForResultFromLeft(Activity activity, Intent intent, int requestCode) {
		slideOpenForResultWithAnimation(activity, intent, requestCode, R.anim.activity_slide_in_from_left, R.anim.activity_slide_out_to_right);
	}

	/**
	 * Open new activity for result with animation of sliding from right to left
	 * and current activity close also slide with new activity.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 * @param requestCode
	 *            original request code
	 */
	public static void slideOpenForResultFromRight(Activity activity, Intent intent, int requestCode) {
		slideOpenForResultWithAnimation(activity, intent, requestCode, R.anim.activity_slide_in_from_right, R.anim.activity_slide_out_to_left);
	}

	/**
	 * Open new activity for result with animation of sliding from left to
	 * right, but current activity remain unchanged state.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 * @param requestCode
	 *            original request code
	 */
	public static void slideOpenForResultFromLeft2(Activity activity, Intent intent, int requestCode) {
		slideOpenForResultWithAnimation(activity, intent, requestCode, R.anim.activity_slide_in_from_left, R.anim.activity_no_animation);
	}

	/**
	 * Open new activity for result with animation of sliding from left to
	 * right, but current activity remain unchanged state.
	 * 
	 * @param activity
	 *            from activity or current activity.
	 * @param intent
	 *            contain activity you want to open.
	 * @param requestCode
	 *            original request code
	 */
	public static void slideOpenForResultFromRight2(Activity activity, Intent intent, int requestCode) {
		slideOpenForResultWithAnimation(activity, intent, requestCode, R.anim.activity_slide_in_from_right, R.anim.activity_no_animation);
	}

	/**
	 * Execute animation when back key pressed.
	 * @param animationType
	 * @param activity
	 */
	public static void onBackpressedAnimation(int animationType, Activity activity) {
		switch (animationType) {
		case AnimationTypes.SLIDE_OPEN_FROM_LEFT:
			slideCloseToLeft(activity);
			break;
		case AnimationTypes.SLIDE_OPEN_FROM_LEFT2:
			slideCloseToLeft2(activity);
			break;
		case AnimationTypes.SLIDE_OPEN_FROM_RIGHT:
			slideCloseToRight(activity);
			break;
		case AnimationTypes.SLIDE_OPEN_FROM_RIGHT2:
			slideCloseToRight2(activity);
			break;
		default:
			activity.finish();
		}
	}
}
