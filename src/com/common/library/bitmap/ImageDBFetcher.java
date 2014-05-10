/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.common.library.bitmap;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.common.library.BuildConfig;

/**
 * A simple subclass of {@link ImageResizer} that fetches and resizes images fetched from a database.
 */
public abstract class ImageDBFetcher extends ImageResizer {
    private static final String TAG = "ImageDbFetcher";
    private static final int DB_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String DB_CACHE_DIR = "db_cache";

    private DiskLruCache mDbDiskCache;
    private File mDbCacheDir;
    private boolean mDbDiskCacheStarting = true;
    private final Object mDbDiskCacheLock = new Object();
    private static final int DISK_CACHE_INDEX = 0;

    /**
     * Initialize providing a target image width and height for the processing images.
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    public ImageDBFetcher(Context context, int imageWidth, int imageHeight) {
        super(context, imageWidth, imageHeight);
        init(context);
    }

    /**
     * Initialize providing a single target image size;
     * if isScaled == true then imageSize is used for both width and height,
     * otherwise the resulting bitmap is just a scaled one.
     *
     * @param context
     * @param imageSize
     */
    public ImageDBFetcher(Context context, int imageSize) {
        super(context, imageSize);
        init(context);
    }
    
	/**
	 * Query image from database and write to outputStream
	 * 
	 * @param key
	 *            identifier used to query image
	 * @param outputStream
	 *            outputStream from DiskCache
	 * @return true if successfully, otherwise return false
	 */
    public abstract boolean queryAndWriteToStream(String key, OutputStream outputStream);

    private void init(Context context) {
        mDbCacheDir = ImageCache.getDiskCacheDir(context, DB_CACHE_DIR);
    }

    @Override
    protected void initDiskCacheInternal() {
        super.initDiskCacheInternal();
        initDbDiskCache();
    }

    private void initDbDiskCache() {
        if (!mDbCacheDir.exists()) {
            mDbCacheDir.mkdirs();
        }
        synchronized (mDbDiskCacheLock) {
            if (ImageCache.getUsableSpace(mDbCacheDir) > DB_CACHE_SIZE) {
                try {
                    mDbDiskCache = DiskLruCache.open(mDbCacheDir, 1, 1, DB_CACHE_SIZE);
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Database cache initialized");
                    }
                } catch (IOException e) {
                    mDbDiskCache = null;
                }
            }
            mDbDiskCacheStarting = false;
            mDbDiskCacheLock.notifyAll();
        }
    }

    @Override
    protected void clearCacheInternal() {
        super.clearCacheInternal();
        synchronized (mDbDiskCacheLock) {
            if (mDbDiskCache != null && !mDbDiskCache.isClosed()) {
                try {
                    mDbDiskCache.delete();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "HTTP cache cleared");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "clearCacheInternal - " + e);
                }
                mDbDiskCache = null;
                mDbDiskCacheStarting = true;
                initDbDiskCache();
            }
        }
    }

    @Override
    protected void flushCacheInternal() {
        super.flushCacheInternal();
        synchronized (mDbDiskCacheLock) {
            if (mDbDiskCache != null) {
                try {
                    mDbDiskCache.flush();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "HTTP cache flushed");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "flush - " + e);
                }
            }
        }
    }

    @Override
    protected void closeCacheInternal() {
        super.closeCacheInternal();
        synchronized (mDbDiskCacheLock) {
            if (mDbDiskCache != null) {
                try {
                    if (!mDbDiskCache.isClosed()) {
                        mDbDiskCache.close();
                        mDbDiskCache = null;
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "HTTP cache closed");
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "closeCacheInternal - " + e);
                }
            }
        }
    }

    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background
     * thread.
     *
     * @param data The data to load the bitmap, in this case, a regular http URL
     * @return The downloaded and resized bitmap
     */
    private Bitmap processBitmap(String data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - " + data);
        }

        final String key = ImageCache.hashKeyForDisk(data);
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapshot;
        synchronized (mDbDiskCacheLock) {
            // Wait for disk cache to initialize
            while (mDbDiskCacheStarting) {
                try {
                    mDbDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }

            if (mDbDiskCache != null) {
                try {
                    snapshot = mDbDiskCache.get(key);
                    if (snapshot == null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "processBitmap, not found in http cache, downloading...");
                        }
                        DiskLruCache.Editor editor = mDbDiskCache.edit(key);
                        if (editor != null) {
                            if (queryAndWriteToStream(data, editor.newOutputStream(DISK_CACHE_INDEX))) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        }
                        snapshot = mDbDiskCache.get(key);
                    }
                    if (snapshot != null) {
                        fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                        fileDescriptor = fileInputStream.getFD();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "processBitmap - " + e);
                } catch (IllegalStateException e) {
                    Log.e(TAG, "processBitmap - " + e);
                } finally {
                    if (fileDescriptor == null && fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {}
                    }
                }
            }
        }

        Bitmap bitmap = null;
        if (fileDescriptor != null) {
            bitmap = decodeSampledBitmapFromDescriptor(fileDescriptor, mImageWidth, mImageHeight, getImageCache());
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {}
        }
        return bitmap;
    }

    @Override
    protected Bitmap processBitmap(Object data) {
        return processBitmap(String.valueOf(data));
    }
}
