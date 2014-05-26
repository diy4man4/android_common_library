package com.common.library.download;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.common.library.utils.ThreadWork;

public class ImageDownloader {
	private static final String TAG = ImageDownloader.class.getSimpleName();
	private LruCache<String, Bitmap> mMemoryCache;
	private StorageMedium mStorageMedium;
	private ThreadWork.Tracker mTracker = new ThreadWork.Tracker();
	
	private interface ImageLoaderListener{
		void onImageLoader(Bitmap bitmap, String url);
	}
	
	/**
	 * Actions definition that can do on bitmap. 
	 * StorageMedium can be implemented by file or database. 
	 */
	public static abstract class StorageMedium {
		protected Context mContext;
		
		public StorageMedium(Context context){
			mContext = context;
		}

		public abstract void saveBitmap(String key, Bitmap bitmap);
		public abstract Bitmap getBitmap(String key);
		public abstract boolean isBitmapExist(String key);
		public abstract long getBitmapSize(String key);
		public abstract boolean deleteBitmap(String key);
	}

	public ImageDownloader(StorageMedium storageMedium){
		int maxMemory = (int) Runtime.getRuntime().maxMemory();  
        int mCacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
		mStorageMedium = storageMedium;
	}
	
	/**
	 * Add bitmap instance into cache.
	 * @param key string which can identity one bitmap
	 * @param bitmap associated with key.
	 */
	private void addBitmapToCache(String key, Bitmap bitmap) {  
	    if (getBitmapFromCache(key) == null && bitmap != null) {  
	        mMemoryCache.put(key, bitmap);  
	    }  
	}  
	 
	/**
	 * Retrieve bitmap with key from cache.
	 * @param key string which can identity one bitmap
	 * @return Bitmap associated with key.
	 */
	private Bitmap getBitmapFromCache(String key) {  
	    return mMemoryCache.get(key);  
	} 
	
	/**
	 * Retrieve bitmap from cache or storage medium, if not exist download it
	 * then and you should set default image for its imageView, after downloaded
	 * it will be replaced by downloaded bitmap in callback of {@code ImageLoaderListener}.
	 * 
	 * @param url
	 *            download url
	 * @param listener
	 *            a callback to retrieve downloaded bitmap.
	 * @return Bitmap or Null, if Null it means download task is triggered.
	 */
	public Bitmap downloadImage(final String url, final ImageLoaderListener listener){
		final String subUrl = url.replaceAll("[^\\w]", "");
		final Bitmap bitmap = getBitmapByKey(subUrl);
		if(bitmap != null){
			Log.d(TAG, "bitmap was found in cache.");
			return bitmap;
		}else{
			new ThreadWork<String, Void, Bitmap>(mTracker) {
				
				@Override
				protected Bitmap doInBackground(String... params) {
					String downloadUrl = params[0];
					return DownloadUtils.downloadBitmap(downloadUrl, null);
				}
				
				@Override
				protected void onPostExecute(Bitmap result) {
					if(result != null){
						Log.d(TAG, "bitmap download successfully");
						mStorageMedium.saveBitmap(subUrl, result);
						addBitmapToCache(subUrl, result);
						
						if(listener != null){
							listener.onImageLoader(result, url);
						}
					}
				}
			}.executeSerial(url);
		}
		return null;
	}
	
	/**
	 * Try load from memory(cache) first, if not have try load from other
	 * storage medium like file or database.
	 * 
	 * @param key
	 *            string which can identity one bitmap
	 * @return Bitmap associated with key or null.
	 */
	private Bitmap getBitmapByKey(String key){
		if(getBitmapFromCache(key) != null){
			return getBitmapFromCache(key);
		}else if(mStorageMedium.isBitmapExist(key) && mStorageMedium.getBitmapSize(key) != 0){
			Bitmap bitmap = mStorageMedium.getBitmap(key);
			// add into cache
			addBitmapToCache(key, bitmap);
			return bitmap;
		}
		return null;
	}
	
	/**
	 * Called in method getView() of adapter to load exist bitmap to imageView,
	 * if not exist set loading icon for it.
	 * 
	 * @param imageView
	 *            should retrieved from viewHolder.
	 * @param downloadUrl
	 *            URL to download bitmap
	 * @param loadingIcon
	 *            loading default icon
	 */
	public void attachCachedBitmapToImageView(ImageView imageView, String downloadUrl, Drawable loadingIcon){
		imageView.setTag(downloadUrl);
		
		Bitmap bitmap = getBitmapByKey(downloadUrl.replaceAll("[^\\w]", ""));
		if(bitmap != null){
			imageView.setImageBitmap(bitmap);
		}else{
			imageView.setImageDrawable(loadingIcon);
		}
	}
	
	/**
	 * Called when (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) 
	 * in ListView or GridView and so on.
	 * 
	 * @param imageView
	 *            should retrieved from viewHolder.
	 * @param downloadUrl
	 *            URL to download bitmap
	 * @param loadingIcon
	 *            loading default icon
	 */
	public void loadBitmapInBackground(final ImageView imageView, String downloadUrl, Drawable loadingIcon){
		Bitmap bitmap = downloadImage(downloadUrl, new ImageLoaderListener() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				if(imageView != null && bitmap != null){
					imageView.setImageBitmap(bitmap);
				}
			}
		});
		
		if(bitmap != null){
			imageView.setImageBitmap(bitmap);
		}else{
			imageView.setImageDrawable(loadingIcon);
		}
	}
	
	/**
	 * Called when user want to cancel download images.
	 */
	public synchronized void cancelPotentialTasks() {
		if(mTracker != null){
			Log.d(TAG, "ImageDownload canceled.");
			mTracker.cancellAll();
		}
	}
}
