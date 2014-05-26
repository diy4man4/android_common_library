package com.common.library.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.common.library.download.ProgressAwareInputStream.OnProgressListener;

public class DownloadUtils {
	private static final int BUFFER_SIZE = 4096;
	private static final int TIMEOUT_DURATION = 3000;

	/**
	 * Download bitmap of small size, if bitmap is very big you can use 
	 * {@link DownloadUtils#downloadBitmap(String, Options, OnProgressListener)}} instead.
	 */
	public static Bitmap downloadBitmap(String imageUrl, OnProgressListener progressListener)  {
		return downloadBitmap(imageUrl, null, progressListener);
	}
	
	/**
	 * Download bitmap of big size.
	 * @param imageUrl
	 * @param options
	 * @param progressListener progress update callback {@link OnProgressListener}
	 * @return bitmap downloaded from imagUrl
	 */
	public static Bitmap downloadBitmap(String imageUrl, Options options, OnProgressListener progressListener)  {
		HttpURLConnection connection = null;
		ProgressAwareInputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;

		try {
			URL url = new URL(imageUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setAllowUserInteraction(true);
			connection.setConnectTimeout(TIMEOUT_DURATION);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Android Client");
			
			// always check HTTP response code first
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String disposition = connection.getHeaderField("Content-Disposition");
				String contentType = connection.getContentType();
				int contentLength = connection.getContentLength();
				
				// retrieve file name from header field or URL
				String fileName = "";
				if (disposition != null) {
					// extracts file name from header field
					int index = disposition.indexOf("filename=");
					if (index > 0) {
						fileName = disposition.substring(index + 10, disposition.length() - 1);
					}
				} else {
					// extracts file name from URL
					String stringUrl = imageUrl.toString();
					fileName = stringUrl.substring(stringUrl.lastIndexOf("/") + 1, stringUrl.length());
				}

				System.out.println("Content-Type = " + contentType);
				System.out.println("Content-Disposition = " + disposition);
				System.out.println("Content-Length = " + contentLength);
				System.out.println("fileName = " + fileName);

				// opens input stream from the HTTP connection
				inputStream = new ProgressAwareInputStream(connection.getInputStream(), contentLength, 0l, imageUrl);
				inputStream.setOnProgressListener(progressListener);

				outputStream = new ByteArrayOutputStream();
				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				if(options != null){
					return BitmapFactory.decodeByteArray(buffer, 0, outputStream.size(), options);
				}else{
					return BitmapFactory.decodeByteArray(buffer, 0, outputStream.size());
				}
			} else{
				if(progressListener != null){
					progressListener.onError("invalidate http response code:" + responseCode, imageUrl);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			if(progressListener != null){
				progressListener.onError("invalidate url format", imageUrl);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(progressListener != null){
				progressListener.onError("IOException", imageUrl);
			}
		} finally {
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(connection != null){
				connection.disconnect();
			}
		}
		return null;
	}

	/**
	 * Download file directly, if file exist it'll delete it automatically
	 * @param fileUrl file download URL
	 * @param saveDir directory to save downloaded file 
	 * @param progressListener  progress update callback {@link OnProgressListener}
	 * @throws IOException
	 */
	public static void downloadFileDirectly(String fileUrl, String saveDir, OnProgressListener progressListener){
		HttpURLConnection connection = null;
		FileOutputStream outputStream = null;
		ProgressAwareInputStream inputStream = null;

		try {
			URL url = new URL(fileUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setAllowUserInteraction(true);
			connection.setConnectTimeout(TIMEOUT_DURATION);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Android Client");
			
			// always check HTTP response code first
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String disposition = connection.getHeaderField("Content-Disposition");
				String contentType = connection.getContentType();
				int contentLength = connection.getContentLength();

				// retrieve file name from header field or URL
				String fileName = "";
				if (disposition != null) {
					// extracts file name from header field
					int index = disposition.indexOf("filename=");
					if (index > 0) {
						fileName = disposition.substring(index + 10, disposition.length() - 1);
					}
				} else {
					// extracts file name from URL
					String stringUrl = fileUrl.toString();
					fileName = stringUrl.substring(stringUrl.lastIndexOf("/") + 1, stringUrl.length());
				}

				System.out.println("Content-Type = " + contentType);
				System.out.println("Content-Disposition = " + disposition);
				System.out.println("Content-Length = " + contentLength);
				System.out.println("fileName = " + fileName);

				// opens input stream from the HTTP connection
				inputStream = new ProgressAwareInputStream(connection.getInputStream(), contentLength, 0l, fileUrl);
				inputStream.setOnProgressListener(progressListener);

				String saveFilePath = saveDir + File.separator + fileName;
				
				// delete local file if exist
				File localFile = new File(saveFilePath);
				if(localFile.exists()){
					localFile.delete();
				}

				// opens an output stream to save into file
				outputStream = new FileOutputStream(saveFilePath);

				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} else{
				if(progressListener != null){
					progressListener.onError("invalidate http response code:" + responseCode, fileUrl);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			if(progressListener != null){
				progressListener.onError("Invalidate URL format", fileUrl);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(progressListener != null){
				progressListener.onError("IOException", fileUrl);
			}
		} finally {
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(connection != null){
				connection.disconnect();
			}
		}
	}
	
	/**
	 * Download file in breakpoint mode.
	 * @param fileURL file download URL
	 * @param localSize the size of file which not download completed
	 * @param saveDir directory to save downloaded file 
	 * @param progressListener  progress update callback {@link OnProgressListener}
	 * @throws IOException
	 */
	public static void downloadFileBreakpointly(String fileURL, File localFile, OnProgressListener progressListener) throws IOException {
		HttpURLConnection connection = null;
		ProgressAwareInputStream inputStream = null;
		RandomAccessFile outputFile = null;
		
		long localSize = localFile.length();

		try {
			URL url = new URL(fileURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setAllowUserInteraction(true);
			connection.setConnectTimeout(TIMEOUT_DURATION);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Android Client");
			connection.setRequestProperty("Range", "bytes=" + localSize + "-");
			
			// always check HTTP response code first
			int responseCode = connection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_PARTIAL) {
				long remainSize = connection.getContentLength();
				long fileTotalSize = localSize + remainSize;
				inputStream = new ProgressAwareInputStream(connection.getInputStream(), fileTotalSize, localSize, fileURL);
				inputStream.setOnProgressListener(progressListener);
				
				// seek position the be the end of file
				outputFile = new RandomAccessFile(localFile.getPath(), "rw");
				outputFile.seek(localSize);
				
				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputFile.write(buffer, 0, bytesRead);
				}
			} else{
				if(progressListener != null){
					progressListener.onError("invalidate http response code:" + responseCode, fileURL);
				}
			}
		}finally {
			if(outputFile != null){
				outputFile.close();
			}
			if(inputStream != null){
				inputStream.close();
			}
			if(connection != null){
				connection.disconnect();
			}
		}
	}
}
