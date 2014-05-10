package com.common.library.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Base64;

public class Base64Utils {
	private static final int CACHE_SIZE = 1024;

	public static byte[] decode(String base64) throws Exception {
		return Base64.decode(base64.getBytes(), Base64.DEFAULT);
	}

	public static String encode(byte[] bytes) throws Exception {
		return new String(Base64.encode(bytes, Base64.DEFAULT));
	}

	public static String encodeFile(String filePath) throws Exception {
		byte[] bytes = fileToBytes(filePath);
		return encode(bytes);
	}

	public static void decodeToFile(String filePath, String base64) throws Exception {
		byte[] bytes = decode(base64);
		bytesToFile(bytes, filePath);
	}

	public static byte[] fileToBytes(String filePath) throws Exception {
		byte[] data = new byte[0];
		File file = new File(filePath);
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = in.read(cache)) != -1) {
				out.write(cache, 0, nRead);
				out.flush();
			}
			out.close();
			in.close();
			data = out.toByteArray();
		}
		return data;
	}

	public static void bytesToFile(byte[] bytes, String filePath) throws Exception {
		InputStream in = new ByteArrayInputStream(bytes);
		File destFile = new File(filePath);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		destFile.createNewFile();
		OutputStream out = new FileOutputStream(destFile);
		byte[] cache = new byte[CACHE_SIZE];
		int nRead = 0;
		while ((nRead = in.read(cache)) != -1) {
			out.write(cache, 0, nRead);
			out.flush();
		}
		out.close();
		in.close();
	}
}
