package com.common.library.utils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageConvertor {

	/**
	 * Convert picture format from drawable to bitmap.
	 * 
	 * @param drawable
	 * @return Bitmap
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	public static BitmapDrawable bitmap2Drawable(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	/**
	 * Convert picture format from bitmap to byte array.
	 * 
	 * @param bitmap
	 * @return byte[]
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * Convert picture from drawable to byte array.
	 * 
	 * @param drawable
	 * @return byte[]
	 */
	public static byte[] drawable2Bytes(Drawable drawable) {
		return bitmap2Bytes(drawable2Bitmap(drawable));
	}
}
