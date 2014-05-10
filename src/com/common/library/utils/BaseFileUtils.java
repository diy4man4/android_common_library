package com.common.library.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.common.library.R;

/**
 * Developer should create a FileUtils class which extends the BaseFileUtils 
 * to extend other business logic about file.
 */
public class BaseFileUtils {
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MEDIA_TYPE_AUDIO = 3;
	
	/**
	 * Generate output file to save media file.
	 * 
	 * @param mediaType
	 *            can be {@link #MEDIA_TYPE_IMAGE}, {@link #MEDIA_TYPE_VIDEO}, {@link #MEDIA_TYPE_AUDIO}
	 * 
	 */
	public static File getOutputMediaFile(int mediaType) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return null;
		}
		File mediaStorageDir = null;
		if (MEDIA_TYPE_IMAGE == mediaType) {
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), 
					"MyCameraApp");
		} else if (MEDIA_TYPE_VIDEO == mediaType) {
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), 
					"MyCameraApp");
		} else if (MEDIA_TYPE_AUDIO == mediaType) {
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
					"MyMusicApp");
		} else{
			throw new IllegalArgumentException("unsupported media type for:" + mediaType);
		}

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File mediaFile = null;
		if (mediaType == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		}else if(mediaType == MEDIA_TYPE_VIDEO){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VIDEO_" + timeStamp + ".mp4");
		}else if(mediaType == MEDIA_TYPE_AUDIO){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "AUDIO_" + timeStamp + ".mp3");
		}
		return mediaFile;
	}
	
	public static Uri getOutputMediaFileUri(int mediaType){
        return Uri.fromFile(getOutputMediaFile(mediaType)); 
	}
	
	public static String getImagePathInGallery(Context context, Uri imageUri){
		if(DeviceUtils.hasKitKat()){
			return getImagepathInGallery19(context, imageUri);
		}else{
			return getImagePathInGallery18(context, imageUri);
		}
	}
	
	private static String getImagePathInGallery18(Context context, Uri imageUri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = null;
		try{
			cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			return cursor.getString(columnIndex);
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT) 
	private static String getImagepathInGallery19(Context context, Uri imageUri) {
		// Will return like "image:43242"
		String wholeID = DocumentsContract.getDocumentId(imageUri);

		// Split at colon, use second item in the array
		String[] idArray = wholeID.split(":");
		if (idArray != null && idArray.length == 2) {
			String idLabel = idArray[0].trim();
			String realId = idArray[1];

			if (idLabel.equals("image")) {
				String[] projections = { MediaStore.Images.Media.DATA };
				String selection = MediaStore.Images.Media._ID + "=?";
				Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
						projections, selection,
						new String[] { realId }, null);

				if (cursor != null) {
					try {
						int columnIndex = cursor.getColumnIndex(projections[0]);
						if (cursor.moveToFirst()) {
							return cursor.getString(columnIndex);
						}
					} finally {
						cursor.close();
					}
				}
			}
		}
		return null;
	}
	
	public static boolean isApkInstalled(Context context, String packageName) {
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return info != null;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static void touchNoMediaFile(final File parentDir) {
		final File file = new File(parentDir, ".nomedia");
		try {
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.setLastModified(System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean canFileBeDeleted(File sourceFile) {
		File desFile = new File(sourceFile.getAbsoluteFile().getPath());
		// if file exist and can be renamed, so it is close state
		if (sourceFile.exists() && sourceFile.renameTo(desFile))
			return true;
		return false;
	}

	public static boolean deleteFiles(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		} else {
			if (file.isFile()) {
				return deleteFile(path);
			} else {
				return deleteDirectory(path);
			}
		}
	}

	private static boolean deleteFile(String path) {
		File file = new File(path);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}

	private static boolean deleteDirectory(String path) {
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	public static File getCacheDir(Context context, String namespace){
		if(!isSDCardAvailable(context)){
			return null;
		}
		String dirPath = getExternalCacheDir(context).getPath() + File.separator + namespace;
		File file = new File(dirPath);
		if(!file.exists()){
			file.mkdirs();
		}
		return file;
	}
	
	public static Intent getAllIntent(String param) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "*/*");
		return intent;
	}

	public static Intent getApkFileIntent(String param) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		return intent;
	}

	public static Intent getVideoFileIntent(String param) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	public static Intent getAudioFileIntent(String param) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	public static Intent getHtmlFileIntent(String param) {
		Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(param).build();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	public static Intent getImageFileIntent(String param) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	public static Intent getPptFileIntent(String param) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	public static Intent getExcelFileIntent(String param) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	public static Intent getWordFileIntent(String param) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	public static Intent getChmFileIntent(String param) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	public static Intent getTextFileIntent(String param, boolean paramBoolean) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	public static Intent getPdfFileIntent(String param) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	public static String getFileExternal(String fileName) {
		if ((!TextUtils.isEmpty(fileName)) && (fileName.length() > 0)) {
			int dot = fileName.lastIndexOf(".");
			if ((dot > -1) && (dot < (fileName.length() - 1))) {
				return fileName.substring(dot + 1);
			}
		}
		return fileName;
	}

	public static void deleteFile(String sdcardDownLoadPath, String accountName, String fileName) {
		File file = new File(sdcardDownLoadPath + accountName + File.separator + fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	public static String getExternalStorageDir() {
		String fileDir = null;
		boolean isSDCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		boolean isRootDirExist = Environment.getExternalStorageDirectory().exists();
		boolean isRootDirCanWrite = Environment.getExternalStorageDirectory().canWrite();
		if (isSDCardExist && isRootDirExist && isRootDirCanWrite) {
			fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return fileDir;
	}

	public static File getExternalCacheDir(Context context){
		if(isSDCardAvailable(context)){
			return context.getExternalCacheDir();
		}
		return null;
	}
	
	public static boolean isSDCardAvailable(final Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if(context.getExternalCacheDir() != null){
				return true;
			}
		}
		UiUtils.showToast(context, R.string.sdcard_not_available);
		return false;
	}
	
	public static boolean isBigImage(int size){
		return size >= 1024 * 32;
	}
	
	public static String getImagePathFromUri(Context context, Uri uri) {
		Cursor cursor = null;
		String[] projections = { MediaStore.Images.Media.DATA };
		cursor = context.getContentResolver().query(uri, projections, null, null, null);
		if (cursor != null) {
			try {
				int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				return cursor.getString(columnIndex);
			} finally {
				cursor.close();
			}
		}
		return null;
	}
	
	public static File getCrashLogDir(Context context){
		if(isSDCardAvailable(context)){
			File logDir = new File(context.getExternalCacheDir() + File.separator + "crash_logs");
			if(!logDir.exists()){
				logDir.mkdirs();
			}
			return logDir;
		}
		return null;
	}
	
	public static File getCrashLogFile(Context context, String logFileName, boolean autoCreate){
		File logDir = getCrashLogDir(context);
		if(logDir != null){
			File logFile = new File(logDir.getPath() + File.separator + logFileName);
			if(!logFile.exists() && autoCreate){
				try {
					logFile.createNewFile();
					return logFile;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return logFile;
		}
		return null;
	}
}