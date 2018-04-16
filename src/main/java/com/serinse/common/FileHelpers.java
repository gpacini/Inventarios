package com.serinse.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;

public class FileHelpers {

	public static void deleteFile(String filename){
		File file = new File(filename);
		file.delete();
	}
	
	//get uploaded filename, is there a easy way in RESTEasy?
	public static String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}
	
	public static File createIfNotExist(String filename){
		File file = new File(filename);
		if( !file.exists() ){
			File parent = file.getParentFile();
			if( parent != null ){
				file.getParentFile().mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return file;
	}

	//save to somewhere
	public static void writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		createIfNotExist(filename);

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}
	
	public static boolean isStringEmptyOrNull(String s){
		if( s.equals("") || s.equals(" ") || s == null || s.trim().equals("") ) return true;
		return false;
	}
	
	public static String hashName(String filename){
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		String hash = "";
		try {
			hash = DatatypeConverter.printHexBinary( 
			           MessageDigest.getInstance("SHA-1").digest(filename.getBytes("UTF-16")));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return hash;
	}
}
