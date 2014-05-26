package com.common.library.download;

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream that notifies listeners of its progress.
 */
public class ProgressAwareInputStream extends InputStream {
	private InputStream inputStream;
	private long fileSize;
	private long localSize;
	
	/**
	 * Identify which download is on progress, can be file URL and so on.
	 */
	private Object tag;
	private long lastPercent;
	private OnProgressListener listener;

	public ProgressAwareInputStream(InputStream in, long fileSize, long localSize, Object tag) {
		this.inputStream = in;
		this.fileSize = fileSize;
		this.localSize = localSize;
		this.tag = tag;
		
		// init progress
		this.lastPercent = (int) (this.localSize * 100 / this.fileSize);
	}

	public void setOnProgressListener(OnProgressListener listener) {
		this.listener = listener;
	}

	public Object getTag() {
		return tag;
	}

	@Override
	public int read() throws IOException {
		localSize += 1;
		checkProgress();
		return inputStream.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		int readCount = inputStream.read(b);
		localSize += readCount;
		checkProgress();
		return readCount;
	}

	@Override
	public int read(byte[] b, int offset, int length) throws IOException {
		int readCount = inputStream.read(b, offset, length);
		localSize += readCount;
		checkProgress();
		return readCount;
	}

	private void checkProgress() {
		int percent = (int) (localSize * 100 / fileSize);
		
		// check whether progress is updated
		if (percent - lastPercent >= 1) {
			lastPercent = percent;
			if (listener != null){
				listener.onProgress(percent, tag);
			}
		}
		
		// check whether download is completed
		if(percent == 100 && listener != null){
			listener.onCompleted(tag);
		}
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}

	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public void mark(int readlimit) {
		inputStream.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		inputStream.reset();
	}

	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	@Override
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}

	/**
	 * Interface for classes that want to monitor this input stream
	 */
	public interface OnProgressListener {
		/**
		 * This callback should only be used to alert user download failed.
		 * @param tag indicate which file download failed among downloading files 
		 */
		void onError(String errorMsg, Object tag);
		
		/**
		 * This callback should only be used to update download progress UI
		 * @param percentage download progress
		 * @param tag indicate progress among downloading files 
		 */
		void onProgress(int percentage, Object tag);
		
		/**
		 * This callback should be used to do like open downloaded file.
		 * @param tag indicate which of downloading file is finished.
		 */
		void onCompleted(Object tag);
	}
}