package com.common.library.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory (will be created if does not exists)
	 * 
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	public static void unzip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = null;
		try{
			zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
			ZipEntry entry = zipIn.getNextEntry();
			// iterates over entries in the zip file
			while (entry != null) {
				String filePath = destDirectory + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					// if the entry is a file, extracts it
					extractFile(zipIn, filePath);
				} else {
					// if the entry is a directory, make the directory
					File dir = new File(filePath);
					dir.mkdir();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
		}finally{
			zipIn.close();
		}
	}
	
	public static void unzip(File zipFile, String destDirectory) throws IOException{
		unzip(zipFile.getPath(), destDirectory);
	}
	
	/**
	 * Called to unzip file stored in asset.
	 * @param fileInputStream
	 * @param destDirectory
	 * @throws IOException
	 */
	public static void unzip(InputStream fileInputStream, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = null;
		try{
			zipIn = new ZipInputStream(fileInputStream);
			ZipEntry entry = zipIn.getNextEntry();
			
			// iterates over entries in the zip file
			while (entry != null) {
				String filePath = destDirectory + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					// if the entry is a file, extracts it
					extractFile(zipIn, filePath);
				} else {
					// if the entry is a directory, make the directory
					File dir = new File(filePath);
					dir.mkdir();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
		}finally{
			zipIn.close();
		}
	}
	

	/**
	 * Extracts a zip entry (file entry)
	 * 
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	/**
	 * Compresses a list of files to a destination zip file
	 * 
	 * @param listFiles
	 *            A collection of files and directories
	 * @param destZipFilePath
	 *            The path of the destination zip file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void zip(List<File> listFiles, String destZipFilePath) throws FileNotFoundException, IOException {
		ZipOutputStream zos = null;
		try{
			zos = new ZipOutputStream(new FileOutputStream(destZipFilePath));
			for (File file : listFiles) {
				if (file.isDirectory()) {
					zipDirectory(file, file.getName(), zos);
				} else {
					zipFile(file, zos);
				}
			}
		}finally{
			zos.flush();
			zos.close();
		}
	}

	/**
	 * Compresses files represented in an array of paths
	 * 
	 * @param files
	 *            a String array containing file paths
	 * @param destZipFilePath
	 *            The path of the destination zip file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void zip(String[] files, String destZipFilePath) throws FileNotFoundException, IOException {
		List<File> listFiles = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			listFiles.add(new File(files[i]));
		}
		zip(listFiles, destZipFilePath);
	}

	/**
	 * Adds a directory to the current zip output stream
	 * 
	 * @param folder
	 *            the directory to be added
	 * @param parentFolder
	 *            the path of parent directory
	 * @param zos
	 *            the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws FileNotFoundException, IOException {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}
			zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
			BufferedInputStream bis = null;
			try{
				 bis = new BufferedInputStream(new FileInputStream(file));
				long bytesRead = 0;
				byte[] bytesIn = new byte[BUFFER_SIZE];
				int read = 0;
				while ((read = bis.read(bytesIn)) != -1) {
					zos.write(bytesIn, 0, read);
					bytesRead += read;
				}
				zos.closeEntry();
			}finally{
				bis.close();
			}
		}
	}

	/**
	 * Adds a file to the current zip output stream
	 * 
	 * @param file
	 *            the file to be added
	 * @param zos
	 *            the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void zipFile(File file, ZipOutputStream zos) throws FileNotFoundException, IOException {
		zos.putNextEntry(new ZipEntry(file.getName()));
		BufferedInputStream bis = null;
		try{
			bis = new BufferedInputStream(new FileInputStream(file));
			long bytesRead = 0;
			byte[] bytesIn = new byte[BUFFER_SIZE];
			int read = 0;
			while ((read = bis.read(bytesIn)) != -1) {
				zos.write(bytesIn, 0, read);
				bytesRead += read;
			}
			zos.closeEntry();
		}finally{
			bis.close();
		}
	}
}
