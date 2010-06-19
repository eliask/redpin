/**
 *  Filename: ImageHandler.java (in org.redpin.server.standalone.net)
 *  This file is part of the Redpin project.
 * 
 *  Redpin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Redpin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Redpin. If not, see <http://www.gnu.org/licenses/>.
 *
 *  (c) Copyright ETH Zurich, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.server.standalone.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.redpin.server.standalone.util.Configuration;
import org.redpin.server.standalone.util.Log;

/**
 * Handler for HTTP requests used to up- and download image.
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class ImageHandler {

	private DataInputStream in;
	private DataOutputStream out;

	final static String CRLF = "\r\n";
	final static int HTTP_OK = 200;
	final static int HTTP_NOT_FOUND = 404;
	
	final static int BUFFER_SIZE = 1024;
	private Logger log = Log.getLogger();

	public ImageHandler(DataInputStream in, DataOutputStream out) {
		this.in = in;
		this.out = out;
	}

	public void handle(String firstLine) {
		System.out.println(firstLine);
		StringTokenizer tokenizer = new StringTokenizer(firstLine);
		String httpMethod = tokenizer.nextToken();
		String httpQuery = tokenizer.nextToken();

		if (httpMethod.equals("GET")) {
			requestGET(httpQuery);
		}

		if (httpMethod.equals("POST")) {
			requestPOST(firstLine);
		}

	}

	public void requestGET(String httpQuery) {

		if (!httpQuery.equals("/")) {
			String fileName = httpQuery.substring(1);
			File f = new File(Configuration.ImageUploadPath + "/" + fileName);
			if (f.exists() && f.canRead()) {
				Log.getLogger().log(Level.FINER,
						"sending map image " + fileName);
				sendResponse(HTTP_OK, fileName, true);
			} else {
				Log.getLogger().log(Level.FINE,
						"client requested non existing map image " + fileName);
				sendResponse(HTTP_NOT_FOUND);
			}
		} else {
			sendResponse(HTTP_NOT_FOUND);
		}

	}

	@SuppressWarnings("deprecation")
	public void requestPOST(String firstLine) {
		String currentLine;
		int dataLength = 0;
		String boundary = "";
		DataOutputStream fileout = null;
		
		try {

			while (true) {
				currentLine = in.readLine();
				if (currentLine == null) {
					break;
				}
				//convert to lowercase, due to a bug in android http://code.google.com/p/android/issues/detail?id=6684
				if (currentLine.toLowerCase().indexOf("Content-Type: multipart/form-data".toLowerCase()) != -1) {
					boundary = currentLine.split("boundary=")[1];
					break;
				}
			}

			while (true) {
				currentLine = in.readLine();
				if (currentLine == null) {
					break;
				}
				if (currentLine.indexOf("--" + boundary) != -1) {
					String filename = in.readLine().split("filename=")[1]
							.replaceAll("\"", "");
					String[] filelist = filename.split("\\"
							+ System.getProperty("file.separator"));
					filename = filelist[filelist.length - 1];
					if (!filename.equals("redpinfile")) {
						Log
								.getLogger()
								.log(Level.FINE,
										"unauthorized client tried to upload map image");
						sendResponse(HTTP_NOT_FOUND);
					}

					break;
				}
			}

			while (true) {

				currentLine = in.readLine();
				if (currentLine == null) {
					break;
				}
				if (currentLine.indexOf("Content-Length:") != -1) {
					dataLength = Integer.parseInt(currentLine.split(" ")[1]);
					break;
				}
			}
			// skip CRLF
			in.readLine();
			String fileName = generateFilename();
			fileout = new DataOutputStream(new FileOutputStream(
					Configuration.ImageUploadPath + "/" + fileName));
			
			byte[] buf = new byte[BUFFER_SIZE];
			
			int l = dataLength;
			int bytesRead;
			while (((bytesRead = in.read(buf,0,Math.min(BUFFER_SIZE, l))) != -1) && (l != 0)) {
				fileout.write(buf, 0, bytesRead);
				l -= bytesRead;
			}
			
			in.readLine();
			currentLine = in.readLine();
			if (currentLine != null && currentLine.indexOf("--" + boundary + "--") != -1) {
				// everything ok
			}
			log.log(Level.FINER,
					"successful uploaded map image " + fileName);
			sendResponse(HTTP_OK, fileName, false);

		} catch (IOException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		} finally {
			if (fileout != null) {
				try {
					fileout.close();
				} catch (IOException e) {}
			}
		}
	}

	public void sendResponse(int code) {
		sendResponse(code, null, false);
	}

	public void sendResponse(int code, String fileName, boolean isFile) {
		String status = "";
		String contentLength = "";
		String contentType = "";

		String responseBody = "";

		FileInputStream fileInput;
		DataInputStream fileReader = null;

		if (code == HTTP_OK) {
			status = "HTTP/1.1 200 OK" + CRLF;
		}

		if (code == HTTP_NOT_FOUND) {
			status = "HTTP/1.1 404 Not Found" + CRLF;
		}

		if (isFile) {

			try {
				fileInput = new FileInputStream(Configuration.ImageUploadPath
						+ "/" + fileName);
				fileReader = new DataInputStream(fileInput);
				int available = fileInput.available();
				contentLength = "Content-Length: " + available + CRLF;
				contentType = "Content-Type: application/octet-stream" + CRLF;
			} catch (FileNotFoundException e) {
				log.log(Level.WARNING, e.getMessage(), e);
			} catch (IOException e) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		} else {
			if (fileName != null) {
				responseBody = "http://{HOST}:{PORT}/" + fileName;
			}
			contentType = "Content-Type: text/plain" + CRLF;
			contentLength = "Content-Length: " + responseBody.length() + CRLF;
		}

		try {
			out.write(toByte(status));
			out.write(toByte(contentType));
			out.write(toByte(contentLength));
			out.write(toByte("Connection: close" + CRLF));
			out.write(toByte(CRLF));

			if (isFile) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = fileReader.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}

			} else {
				out.write(toByte(responseBody));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
				}
			}
		}

	}

	private static byte[] toByte(String s) {
		if (s == null || s.isEmpty()) {
			return new byte[0];
		}

		byte[] r = null;
		try {
			r = s.getBytes("US-ASCII");
		} catch (Exception e) {
			r = s.getBytes();
		}
		return r;
	}

	public String generateFilename() {
		MessageDigest md;
		byte[] sha1hash = new byte[40];
		Random r = new Random();
		String fileName = "";
		String token = "";
		while (true) {
			token = Long.toString(Math.abs(r.nextLong()), 36)
					+ Long.toString(System.currentTimeMillis());

			try {
				md = MessageDigest.getInstance("SHA-1");
				md.update(token.getBytes("iso-8859-1"), 0, token.length());
				sha1hash = md.digest();
			} catch (Exception e) {
				log.log(Level.WARNING, e.getMessage(), e);
			}

			fileName = convertToHex(sha1hash);

			if (!new File(Configuration.ImageUploadPath + fileName).exists()) {
				break;
			}

		}
		return fileName;
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buffer.append((char) ('0' + halfbyte));
				} else {
					buffer.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buffer.toString();
	}
}