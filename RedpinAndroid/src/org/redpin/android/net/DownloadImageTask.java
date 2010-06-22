/**
 *  Filename: DownloadImageTask.java (in org.repin.android.net)
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
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 * 
 *  www.redpin.org
 */
package org.redpin.android.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import org.redpin.android.ApplicationContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

/**
 * {@link AsyncTask} for downloading images in the background and saving them
 * locally if not yet cached.
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 * 
 */
public class DownloadImageTask extends AsyncTask<String, Void, String> {

	private static final String TAG = DownloadImageTask.class.getSimpleName();

	private static final String FILE_EXT = ".jpg";

	private Context context = ApplicationContext.get();
	private DownloadImageTaskCallback callback;
	private String url;

	public DownloadImageTask() {
	}

	public DownloadImageTask(DownloadImageTaskCallback callback) {
		this.callback = callback;
	}

	/**
	 * Checks if images is already cached. If not downloads the images and
	 * caches it.
	 * 
	 * @param params
	 *            URL of image to be downloaded (only first is considered)
	 * @return Absolute file path to the cached image
	 */
	@Override
	protected String doInBackground(String... params) {

		Bitmap bm = null;

		if (params.length == 0)
			return null;

		String urlStr = params[0];
		url = urlStr;
		String md5 = "";

		try {
			md5 = md5(urlStr);
			File f = context.getFileStreamPath(md5 + FILE_EXT);

			if (f.exists()) {
				return f.getAbsolutePath();
			}

		} catch (FileNotFoundException fnf) {
		} catch (Exception e1) {
		}

		InputStream is = null;
		try {
			
			if (urlStr != null && url.indexOf("http://{HOST}:{PORT}") != -1) {
				urlStr = urlStr.replace("{HOST}", ConnectionHandler.host);
				urlStr = urlStr.replace("{PORT}", ConnectionHandler.port + "");
			}
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.connect();
			
			is = conn.getInputStream();
			bm = BitmapFactory.decodeStream(is);


		} catch (IOException e) {
			Log.i(TAG, "Download of image failed: " + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		if (bm != null) {
			FileOutputStream fos = null;
			try {

				fos = context.openFileOutput(md5 + FILE_EXT,
						Context.MODE_WORLD_READABLE);
				bm.compress(CompressFormat.JPEG, 90, fos);

			} catch (Exception e) {
				Log.e(TAG, "Storage of image failed: " + e.getMessage());
			} finally {
				if (fos != null) {
					try {
						fos.flush();
						fos.close();
					} catch (IOException e) {
					}
				}

				if (bm != null) {
					bm.recycle();
				}
			}
		}

		File f = context.getFileStreamPath(md5 + FILE_EXT);

		if (f.exists()) {
			return f.getAbsolutePath();
		}

		return null;
	}

	/**
	 * Calls the callback (if supplied) after the image is downloaded
	 * 
	 * @param result
	 *            Absolute file path of cached image
	 */
	@Override
	protected void onPostExecute(String result) {

		if (callback != null) {
			try {
				if (result != null) {
					callback.onImageDownloaded(url, result);
				} else {
					callback.onImageDownloadFailure(url);
				}
			} catch (BadTokenException e) {
				Log.w(TAG, "Callback failed, caught BadTookenException: " + e.getMessage(), e);
			} catch (Exception e) {
				Log.w(TAG, "Callback failed, caught Exception: " + e.getMessage(), e);
			}
			callback = null;
			url = null;
			context = null;
		}

	}

	/**
	 * 
	 * @param s
	 *            URL of image
	 * @return md5 hash value of image URL
	 * @throws Exception
	 */
	static protected String md5(String s) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");

		md.update(s.getBytes());

		byte digest[] = md.digest();
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < digest.length; i++) {
			result.append(Integer.toHexString(0xFF & digest[i]));
		}

		return result.toString();
	}

	/**
	 * Callback Interface for {@link DownloadImageTask}
	 * 
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 * 
	 */
	public interface DownloadImageTaskCallback {
		/**
		 * 
		 * @param url
		 *            URL of the image that was downloaded
		 * @param path
		 *            Absolute path of the downloaded and cached image
		 */
		public void onImageDownloaded(String url, String path);
		public void onImageDownloadFailure(String url);
	}

}
