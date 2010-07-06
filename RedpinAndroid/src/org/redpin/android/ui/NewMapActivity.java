/**
 *  Filename: NewMapActivity.java (in org.repin.android.ui)
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
package org.redpin.android.ui;

import org.redpin.android.R;
import org.redpin.android.core.Map;
import org.redpin.android.net.DownloadImageTask;
import org.redpin.android.net.UploadImageTask;
import org.redpin.android.net.DownloadImageTask.DownloadImageTaskCallback;
import org.redpin.android.net.UploadImageTask.UploadImageTaskCallback;
import org.redpin.android.net.home.MapRemoteHome;
import org.redpin.android.ui.list.MapListActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Class represents an activity responsible for the "Add new map" view.
 * 
 * @author Luba Rogoleva (lubar@student.ethz.ch)
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class NewMapActivity extends Activity implements UploadImageTaskCallback, DownloadImageTaskCallback {

	@SuppressWarnings("unused")
	private final static String TAG = NewMapActivity.class.getSimpleName();

	private EditText inputUrl = null;
	private EditText inputMapName = null;
	private String mapPath = null;
	private String LOADING_MAP = "";
	private String UPLOADING_MAP = "";
	private String FETCH_PROBLEM = "";
	private String UPLOAD_PROBLEM = "";
	private String EMPTY_URL = "";
	private String EMPTY_MAP_NAME = "";


	private final int ID_DIALOG_LOADING = 1;
	private final int ID_DIALOG_UPLOADING = 2;


	/**
	 * Called when the activity is starting inflating the activity's UI. This is
	 * where most initialization should go.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		
		
		LOADING_MAP = getString(R.string.newmap_loading_map);
		UPLOADING_MAP = getString(R.string.newmap_uploading_map);
		FETCH_PROBLEM = getString(R.string.newmap_loading_problem);
		UPLOAD_PROBLEM = getString(R.string.newmap_uploading_problem);
		EMPTY_URL = getString(R.string.newmap_empty_url);
		EMPTY_MAP_NAME = getString(R.string.newmap_empty_map_name);
		
		
		setContentView(R.layout.newmap_view);
		inputUrl = (EditText) findViewById(R.id.map_url);
		inputUrl.setImeOptions(EditorInfo.IME_ACTION_GO);
		inputUrl.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					String urlString = v.getText().toString();
					if (urlString == null || urlString.trim().length() == 0) {
						showAlert(EMPTY_URL);
						return false;
					}
					

					showDialog(ID_DIALOG_LOADING);
					DownloadImageTask task = new DownloadImageTask(NewMapActivity.this);
					task.execute(urlString);		

				}

				return false;
				
			}
		});
		
		inputMapName = (EditText) findViewById(R.id.map_name);
		inputMapName.setImeOptions(EditorInfo.IME_ACTION_DONE);
		inputMapName.setOnFocusChangeListener(new OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				checkEnableSave();
			}
		});
		
		inputMapName.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String mapName = v.getText().toString();
					if (mapName == null || mapName.trim().length() == 0) {
						showAlert(EMPTY_MAP_NAME);
						return false;
					}

					checkEnableSave();

				}

				return false;
				
			}
		});
		setSave(false);
		Button urlChoiceButton = (Button) findViewById(R.id.pick_image_url_button);
		urlChoiceButton.setOnClickListener(enableURLChoice);
		Button phoneChoiceButton = (Button) findViewById(R.id.pick_image_phone_button);
		phoneChoiceButton.setOnClickListener(enablePhoneChoice);
		Button saveButton = (Button) findViewById(R.id.save_map_button);
		saveButton.setOnClickListener(saveMap);
	}

	/**
	 * Called when the launched activity exits, giving the requestCode with
	 * which it was started, the resultCode it returned, and any additional data
	 * from it.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			if (requestCode == 1) {
				Uri selectedImageURI = data.getData();
				String selectedImagePath = getPath(selectedImageURI);
				ImageView imgView = new ImageView(this);
				imgView = (ImageView) findViewById(R.id.addmap_map);
				Drawable image = Drawable.createFromPath(selectedImagePath);
				imgView.setImageDrawable(image);
				
				UploadImageTask task = new UploadImageTask(this);
				task.execute(selectedImagePath);
				showDialog(ID_DIALOG_UPLOADING);

			}
		}
	}

	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private OnClickListener saveMap = new OnClickListener() {
		public void onClick(View view) {
			String mapName = inputMapName.getText().toString();
		
			// save the new map
			Map map = new Map();
			map.setMapName(mapName);
			map.setMapURL(mapPath);
			MapRemoteHome.setMap(map);

			
			Intent i = new Intent(NewMapActivity.this, MapListActivity.class);
			startActivity(i);
		}
	};

	private OnClickListener enablePhoneChoice = new OnClickListener() {
		public void onClick(View view) {
			
			inputUrl.setVisibility(View.INVISIBLE);
	
			
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(photoPickerIntent,
					"Select Picture"), 1);

		}
	};

	private OnClickListener enableURLChoice = new OnClickListener() {
		public void onClick(View view) {
			inputUrl.setVisibility(View.VISIBLE);
			inputUrl.requestFocus();
			InputMethodManager imm = (InputMethodManager) NewMapActivity.this
			.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
			InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == ID_DIALOG_LOADING) {
			ProgressDialog dialog = ProgressDialog.show(this, "", LOADING_MAP,
					true);
			return dialog;
		} else if (id == ID_DIALOG_UPLOADING) {
			ProgressDialog dialog = ProgressDialog.show(this, "",
					UPLOADING_MAP, true);
			return dialog;
		}

		return super.onCreateDialog(id);
	}

	private void setSave(boolean mode) {
		Button saveMapButton = (Button) findViewById(R.id.save_map_button);
		saveMapButton.setEnabled(mode);
	}

	private void showAlert(String alertMsg) {
		new AlertDialog.Builder(this).setCancelable(false).setMessage(
				alertMsg).setPositiveButton(android.R.string.ok, null).show();
	}

	@Override
	public void onImageUploadFailure() {
		dismissDialog(ID_DIALOG_UPLOADING);
		showAlert(UPLOAD_PROBLEM);	
	}

	@Override
	public void onImageUploaded(String path) {
		dismissDialog(ID_DIALOG_UPLOADING);	
		
		mapPath = path;
		
		checkEnableSave();
	}

	private void checkEnableSave() {
		String mapName = inputMapName.getText().toString();
		if (mapName == null || mapName.trim().length() == 0 || mapPath == null) {
			setSave(false);
		} else {
			setSave(true);
		}
		
	}

	@Override
	public void onImageDownloadFailure(String url) {
		
		dismissDialog(ID_DIALOG_LOADING);
		showAlert(FETCH_PROBLEM);
		
	}

	@Override
	public void onImageDownloaded(String url, String path) {
		dismissDialog(ID_DIALOG_LOADING);
		mapPath = url;
		ImageView imgView = (ImageView) findViewById(R.id.addmap_map);
		imgView.setImageDrawable(Drawable.createFromPath(path));
		checkEnableSave();
		
		
	}

}
