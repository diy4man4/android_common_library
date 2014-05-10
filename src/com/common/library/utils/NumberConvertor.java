package com.common.library.utils;

import java.nio.ByteBuffer;

import android.util.Log;

public class NumberConvertor {
	private static final String TAG = "DataUtil";

	/**
	 * Cut Byte Array with specified start index and end index
	 */
	public static byte[] subBytes(byte[] data, int start, int end) {
		byte[] result = null;
		int cutLen = end - start;
		if (data == null) {
			Log.e(TAG, "src data is null");
			return null;
		}
		if (cutLen > data.length || cutLen < 0) {
			Log.e(TAG, "index out of bounds; start=" + start + "; end=" + end);
			return null;
		}
		result = new byte[cutLen];
		System.arraycopy(data, start, result, 0, result.length);
		return result;
	}

	public static byte[] joinBytes(byte[] ...bytes){
		int size = 0;
		for(byte[] item : bytes){
			size += item.length;
		}
		
		int copiedIndex = 0;
		byte[] totalBytes = new byte[size];
		for(byte[] item : bytes){
			System.arraycopy(item, 0, totalBytes, copiedIndex, item.length);
			copiedIndex += item.length;
		}
		return totalBytes;
	}

	public static byte[] longToBytes(long n) {
		return ByteBuffer.allocate(8).putLong(n).array();
	}

	public static long bytesToLong(byte[] array) {
		ByteBuffer buffer = ByteBuffer.wrap(array);
		return buffer.getLong();
	}

	public static byte[] intToBytes(int n) {
		return ByteBuffer.allocate(4).putInt(n).array();
	}

	public static int bytesToInt(byte b[]) {
		return ByteBuffer.wrap(b).getInt();
	}

	public static byte[] shortToBytes(short n) {
		return ByteBuffer.allocate(2).putShort(n).array();
	}

	public static short bytesToShort(byte[] b) {
		return ByteBuffer.wrap(b).getShort();
	}
}
