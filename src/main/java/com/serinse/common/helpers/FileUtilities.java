package com.serinse.common.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.serinse.pers.exception.FailProcessException;

public class FileUtilities {
	public static Logger LOG = Logger.getLogger(FileUtilities.class);

	
	public static byte[] fileToByteArray(File file) throws IOException {
		FileInputStream fileInputStream = null;

		byte[] bFile = new byte[(int) file.length()];

		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();

			return bFile;

		} catch (Exception e) {
			LOG.error("..FileUtilities " + e);
			throw new IOException(e);
		}

	}

	public static void createFile(byte[] bArray, String path) throws IOException {

		try {
			FileOutputStream fileOuputStream = new FileOutputStream(path);
			fileOuputStream.write(bArray);
			fileOuputStream.close();
		} catch (IOException e) {
			LOG.error("..FileUtilities " + e);
			throw new IOException(e);
		}

	}

	public static void createDirectory(String pathName) {
		File directory = new File(pathName);
		if (!directory.exists()) {
			try {
				directory.mkdir();
			} catch (SecurityException se) {
				LOG.error("****SOME ERROR DETECTED :" + se.getLocalizedMessage());
			}
		}
		setPermissions(directory);
	}
	
	public static void createDirectoryAndSaveFile(String pathFolder, String fileNameWithExtension, byte[] bArray)
			throws IOException {
		createDirectory(pathFolder);
		createFile(bArray, pathFolder + fileNameWithExtension);
	}

	private static void setPermissions(File directory) {
		directory.setExecutable(true);
		directory.setReadable(true);
		directory.setWritable(true);

	}

	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		Files.copy(sourceFile.toPath(), targetFile.toPath());
	}

	public static byte[] retrieveFileFromDisk(String path) throws FailProcessException, IOException {

		File file = new File(path);
		if (!file.exists()) {
			throw new FailProcessException();
		}

		return FileUtilities.fileToByteArray(file);
	}

	public static void deleteFile(String path) throws FailProcessException {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}

		if (!file.delete()) {
			throw new FailProcessException();
		}
	}

	public static File retrieveFileAsFileObjectFromDisk(String path) throws FailProcessException, IOException {

		File file = new File(path);
		if (!file.exists()) {
			throw new FailProcessException();
		}
		return file;
	}

	public static InputStream retrieveFileAsFileInputStreamFromDisk(String path) throws FailProcessException,
			IOException {

		File file = new File(path);
		if (!file.exists()) {
			throw new FailProcessException();
		}
		InputStream targetStream = new FileInputStream(file);
		return targetStream;
	}

	public static Boolean existsFiles(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public static File inputStreamToFile(InputStream inputStream, String path) throws FailProcessException {

		try {
			LOG.info("inputStream.available()=  " + inputStream.available());
			byte[] data = IOUtils.toByteArray(inputStream);
			File newFile = new File(path);
			FileUtils.writeByteArrayToFile(newFile, data);
			return new File(path);
		} catch (IOException e) {
			LOG.error("...inputStreamToFile :" + e.getLocalizedMessage());
			throw new FailProcessException();
		}
	}

	public static void createDirectoryAndSaveFile(String pathFolder, InputStream inputStream, String fileName)
			throws IOException, FailProcessException {
		createDirectory(pathFolder);
		createFile(inputStream, pathFolder + fileName);
	}
	
	public static void createFile(InputStream inputStream, String path) throws FailProcessException {

		try {
			LOG.info("inputStream.available()=  " + inputStream.available());
			File newFile = new File(path);
			System.out.println(path);
			FileUtils.copyInputStreamToFile(inputStream, newFile);
		} catch (IOException e) {
			LOG.error("...createFile :" + e.getLocalizedMessage());
			throw new FailProcessException();
		}
	}

}
