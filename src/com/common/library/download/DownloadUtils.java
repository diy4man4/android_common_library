package com.common.library.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.accounts.NetworkErrorException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class DownloadUtils {
	private static final String TAG = DownloadUtils.class.getSimpleName();
	private static final int BUFFER_SIZE = 4096;

	public static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpConnectionParams.setSoTimeout(httpParams, 3000);
        
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or IllegalStateException
			getRequest.abort();
			Log.w(TAG, "Error while retrieving bitmap from " + url +"; error msg:" + e.toString());
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}
	
	/**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
	 * @throws NetworkErrorException 
     */
	public static void downloadFile(String fileURL, String saveDir) throws NetworkErrorException, IOException {
		HttpURLConnection httpConn = null;
		try {
			URL url = new URL(fileURL);
			httpConn = (HttpURLConnection) url.openConnection();
			int responseCode = httpConn.getResponseCode();

			// always check HTTP response code first
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String fileName = "";
				String disposition = httpConn.getHeaderField("Content-Disposition");
				String contentType = httpConn.getContentType();
				int contentLength = httpConn.getContentLength();

				if (disposition != null) {
					// extracts file name from header field
					int index = disposition.indexOf("filename=");
					if (index > 0) {
						fileName = disposition.substring(index + 10, disposition.length() - 1);
					}
				} else {
					// extracts file name from URL
					fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
				}

				Log.i(TAG, "Content-Type = " + contentType);
				Log.i(TAG, "Content-Disposition = " + disposition);
				Log.i(TAG, "Content-Length = " + contentLength);
				Log.i(TAG, "fileName = " + fileName);

				// opens input stream from the HTTP connection
				InputStream inputStream = httpConn.getInputStream();
				String saveFilePath = saveDir + File.separator + fileName;

				// opens an output stream to save into file
				FileOutputStream outputStream = new FileOutputStream(saveFilePath);

				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.close();
				inputStream.close();

				System.out.println("File downloaded");
			} else {
				throw new NetworkErrorException("No file to download. Server replied HTTP code: " + responseCode);
			}
		} finally {
			if (httpConn != null) {
				httpConn.disconnect();
			}
		}

	}
}
