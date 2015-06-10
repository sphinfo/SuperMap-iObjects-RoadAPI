package com.sphinfo.roadapi.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.omg.CORBA.portable.ApplicationException;

public final class ReadWriteUtil {

	public ReadWriteUtil() {
	}

	public static void writeResponse(InputStream inputstream,
			ServletResponse servletresponse) throws IOException {
		ServletOutputStream servletoutputstream;
		byte abyte0[];
		boolean flag1;

		servletoutputstream = servletresponse.getOutputStream();
		abyte0 = new byte[5000];
		boolean flag = false;

		do {
			int i;
			if ((i = inputstream.read(abyte0, 0, 5000)) <= 0)
				break;
			servletoutputstream.write(abyte0, 0, i);
		} while (true);
		try {
			inputstream.close();
		} catch (Exception exception) {
		}
		inputstream = null;
		Object obj = null;

	}

	public static void writeToFile(byte abyte0[], String s) throws IOException {
		FileOutputStream fileoutputstream = new FileOutputStream(s);
		fileoutputstream.write(abyte0);
		try {
			fileoutputstream.close();
		} catch (Exception exception) {
		}
		fileoutputstream = null;
	}

	public static byte[] readFromFile(String s) throws IOException {
		FileInputStream fileinputstream = null;
		byte abyte0[];
		fileinputstream = new FileInputStream(s);
		abyte0 = readFromStream(fileinputstream);
		if (fileinputstream != null)
			try {
				fileinputstream.close();
			} catch (Exception exception) {
			}
		return abyte0;

	}

	public static byte[] readFromStream(InputStream inputstream)
			throws IOException {
		ByteArrayOutputStream bytearrayoutputstream;
		boolean flag1;

		bytearrayoutputstream = new ByteArrayOutputStream();
		byte abyte1[];
		byte abyte0[] = new byte[5000];
		boolean flag = false;
		do {
			int i;
			if ((i = inputstream.read(abyte0, 0, 5000)) <= 0)
				break;
			bytearrayoutputstream.write(abyte0, 0, i);
		} while (true);
		abyte1 = bytearrayoutputstream.toByteArray();
		try {
			bytearrayoutputstream.close();
		} catch (Exception exception) {
		}
		return abyte1;

	}

	public static byte[] readFromURL(String s) throws IOException {
		URL url;
		InputStream inputstream;
		url = new URL(s);
		inputstream = null;
		byte abyte0[];
		inputstream = url.openStream();
		abyte0 = readFromStream(inputstream);
		if (inputstream != null)
			try {
				inputstream.close();
			} catch (Exception exception) {
			}

		return abyte0;

	}

	public static String requestURL(String strURL)
			throws UnsupportedEncodingException, IOException {
		URL url = new URL(strURL);
		URLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(10000);
		conn.setDoOutput(true);
		String s2 = null;
		;
		BufferedReader in = null;

		in = new BufferedReader(new InputStreamReader(conn.getInputStream(),
				"UTF-8"));

		StringBuilder sb = new StringBuilder();
		while ((s2 = in.readLine()) != null) {
			sb.append(s2);
		}

		in.close();
		conn = null;
		url = null;
		return sb.toString();

	}

	public static void deleteFile(String file) {
		File f = new File(file);

		if (f.delete()) {
		} else {
			System.err.println(": " + file);
		}
	}

	public static void deleteDirectory(String pathString) {

		File path = new File(pathString);
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		// return (path.delete());
	}

	public static void deleteDirectory(File path) {

		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		// return (path.delete());
	}

	public static void writeJsonStringResponse(JSONObject json,
			HttpServletResponse resp) {
		// TODO Auto-generated method stub
		resp.setContentType("text/x-json;charset=UTF-8");
		resp.setHeader("Cache-Control", "no-cache");

		try {
			resp.getWriter().write(json.toString());
		} catch (IOException e) {
			
		}

	}
	

	// converting Stream to String
	public static String convertStreamToString(InputStream is) throws IOException {

		if (is != null) {
			java.io.Writer writer = new java.io.StringWriter();
			char[] buffer = new char[1024];
			try {
				java.io.Reader reader = new java.io.BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

}
