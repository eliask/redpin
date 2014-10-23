/**
 *  Filename: MapView.java (in org.repin.android.ui.mapview)
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
package org.redpin.android.ui.mapview;

import java.util.HashMap;
import java.util.List;

import org.redpin.android.R;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.db.EntityHomeFactory;
import org.redpin.android.net.DownloadImageTask;
import org.redpin.android.net.DownloadImageTask.DownloadImageTaskCallback;
import org.redpin.android.provider.RedpinContract;
import org.redpin.android.ui.mapview.ZoomAndScrollImageView.ZoomAndScrollViewListener;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * {@link MapView} displays a {@link Map} on the screen and its {@link Location}
 * s depending on the selection of the user.
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class MapView extends FrameLayout implements DownloadImageTaskCallback,
		ZoomAndScrollViewListener {

	private ZoomAndScrollImageView imageView;

	private FrameLayout contentView;
	private TextView loadingView;

	private Map currentMap;
	private Location currentLocation;
	private HashMap<Location, LocationMarker> locationMarker = new HashMap<Location, LocationMarker>();
	private LocationMarker requestedCenterMarker;

	private boolean loadPending;
	private int[] requestedScroll;
	private Uri currentUri;

	private int imageWidth;

	private int imageHeight;

	private final static long FADE_DURATION = 500;
	private static final String TAG = MapView.class.getSimpleName();

	/**
	 * Construct a new MapView with a Context object.
	 *
	 * @param context
	 *            A Context object used to access application assets.
	 */
	public MapView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * Construct a new MapView with layout parameters and a default style.
	 *
	 * @param context
	 *            A Context object used to access application assets.
	 * @param attrs
	 *            An AttributeSet passed to our parent.
	 * @param defStyle
	 *            The default style resource ID.
	 */
	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * Construct a new MapView with layout parameters.
	 *
	 * @param context
	 *            A Context object used to access application assets.
	 * @param attrs
	 *            An AttributeSet passed to our parent.
	 */
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	/**
	 * Initializes the MapView
	 *
	 * @param context
	 *            {@link Context}
	 */
	protected void initView(Context context) {

		imageView = new ZoomAndScrollImageView(context);
		imageView.setListener(this);
		addView(imageView);

		contentView = new FrameLayout(context);
		contentView.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));

		addView(contentView);

		loadingView = new TextView(getContext());

		loadingView.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
		loadingView.setGravity(Gravity.CENTER);
		loadingView.setTextColor(R.color.light_grey);
		loadingView.setTextSize(30);
		loadingView.setTypeface(Typeface.DEFAULT_BOLD);
		loadingView.setText(R.string.loading_text);
		loadingView.setVisibility(INVISIBLE);
		loadingView.setBackgroundColor(Color.WHITE);

		addView(loadingView);
	}

	/**
	 * Shows an {@link LocationMarker}
	 *
	 * @param marker
	 *            {@link LocationMarker} to be shown
	 */
	private void showLocationMarker(LocationMarker marker) {
		if (loadPending) {
			Log
					.v(TAG,
							"showLocationMarker: Map image is pending, marker not shown.");
			return;
		}

		marker.setVisibility(VISIBLE);
	}

	/**
	 * Shows all {@link LocationMarker}s
	 */
	private void showLocationMarkers() {
		for (LocationMarker m : locationMarker.values()) {
			showLocationMarker(m);
		}
	}

	/**
	 *
	 * @return The current displayed {@link Map}
	 */
	public Map getCurrentMap() {
		return currentMap;
	}

	/**
	 *
	 * @return The current estimated {@link Location}
	 */
	public Location getCurrentLocation() {
		return currentLocation;
	}

	/**
	 *
	 * @return uri of the current image
	 */
	public String getUrl() {
		if (currentUri == null)
			return null;

		return currentUri.toString();
	}

	/**
	 *
	 * @return Fade out {@link Animation}
	 */
	protected Animation fadeOut() {
		AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setDuration(FADE_DURATION);
		return fadeOut;
	}

	/**
	 *
	 * @return Fade in {@link Animation}
	 */
	protected Animation fadeIn() {
		AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setDuration(FADE_DURATION);
		return fadeIn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onTrackballEvent(MotionEvent e) {
		System.out.println(e);
		return false;
	}

	/**
	 * Shows the map image.
	 *
	 * @param url
	 *            URL of the map image
	 */
	protected void showImage(String url) {

		loadPending = true;
		loadingView.startAnimation(fadeIn());
		loadingView.setVisibility(VISIBLE);

		DownloadImageTask task = new DownloadImageTask(this);
		task.execute(url);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onImageDownloaded(String url, String path) {
		loadPending = false;
		Bitmap bm = BitmapFactory.decodeFile(path);
		imageWidth = bm.getWidth();
		imageHeight = bm.getHeight();

		contentView.setLayoutParams(new LayoutParams(imageWidth, imageHeight));
		/*
		 * ViewGroup.LayoutParams params = contentView.getLayoutParams();
		 * params.width = w; params.height = h;
		 * contentView.setLayoutParams(params);
		 */
		imageView.setImageBitmap(bm);
		// imageView.requestFocus();
		showLocationMarkers();
		processRequestMarkerOnCenter();
		processRequestScroll();

		loadingView.startAnimation(fadeOut());
		loadingView.setVisibility(INVISIBLE);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onImageDownloadFailure(String url) {
		loadPending = false;
		new AlertDialog.Builder(getContext()).setMessage(
				getContext().getString(R.string.map_download_failed)).show();

	}

	public int[] getScrollXY() {
		float[] current = imageView.getCurrentXY();
		return new int[] { -(int) current[0], -(int) current[1] };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrollBy(int x, int y) {
		imageView.scrollBy(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrollTo(int x, int y) {
		imageView.scrollTo(x, y);
	}

	/**
	 * Shows either {@link Map} or {@link Location} represented by an
	 * {@link Uri}
	 *
	 * @param uri
	 *            {@link Uri} of the {@link Map} or {@link Location}
	 */
	public void show(Uri uri) {
		if (uri == null)
			return;

		currentUri = uri;

		long id = ContentUris.parseId(uri);

		String type = getContext().getContentResolver().getType(uri);

		if (RedpinContract.Map.ITEM_TYPE.equals(type)) {
			Map m = EntityHomeFactory.getMapHome().getById(id);
			if (m == null) {
				return;
			}
			showMap(m);
			return;
		}

		if (RedpinContract.Location.ITEM_TYPE.equals(type)) {
			Location l = EntityHomeFactory.getLocationHome().getById(id);
			if (l == null) {
				return;
			}
			showLocation(l, false);
			return;
		}

	}

	/**
	 * Adds a new {@link LocationMarker} on the center of the screen and sets
	 * the {@link Location}s coordinates accordingly.
	 *
	 * @param newLocation
	 *            {@link Location} for the {@link LocationMarker}
	 */
	public void addNewLocation(Location newLocation) {
		if (newLocation == null)
			return;

		int w = getWidth();
		int h = getHeight();

		float[] point = new float[] { 0, 0 };
		lastMatrix.mapPoints(point);
		int offsetX = (int) ((-point[0] + (w / 2)) / lastScale);
		int offsetY = (int) ((-point[1] + (h / 2)) / lastScale);

		newLocation.setMapXcord(offsetX);
		newLocation.setMapYcord(offsetY);

		LocationMarker marker = addMarkerForLocation(newLocation);
		marker.onScaleChanged(lastScale);
		marker.showAnnotation();
		marker.setVisibility(VISIBLE);
		marker.beginEdit();

	}

	/**
	 * Add a {@link LocationMarkerAnnotation} for the specified {@link Location}
	 *
	 * @param l
	 *            {@link Location}
	 * @return The added {@link LocationMarkerAnnotation}
	 */
	protected LocationMarker addMarkerForLocation(Location l) {
		LocationMarker marker = locationMarker.get(l);

		if (marker == null) {
			marker = new LocationMarker(getContext(), l, contentView);
			locationMarker.put(l, marker);
			contentView.addView(marker);
		}

		if (l.equals(currentLocation)) {
			marker.setIsCurrentLocation(true);
		}

		marker.setEnabled(modifiable);

		return marker;
	}

	/**
	 * Scrolls the view so that the {@link LocationMarker} is on the center.
	 *
	 * @param marker
	 *            {@link LocationMarker} to be centered on the screen
	 */
	public void centerMarker(LocationMarker marker) {

		// imageView.setZoom(1.0f, true);
		Location location = marker.getLocation();
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		int left = location.getMapXcord() - w / 2;
		int top = location.getMapYcord() - h / 2;

		// due to a bug javascriptScrollTo has to be used
		// scrollTo(left, top);

		scrollTo(left, top);

	}

	/**
	 * Centers the requested {@link LocationMarker}
	 *
	 */
	public void processRequestMarkerOnCenter() {
		if (requestedCenterMarker == null) {
			return;
		}

		centerMarker(requestedCenterMarker);
		requestedCenterMarker = null;
	}

	/**
	 * Requests that a {@link LocationMarker} is centered on the screen.
	 *
	 * @param marker
	 *            {@link LocationMarker} to be centered on the screen
	 */
	public void requestMarkerOnCenter(LocationMarker marker) {
		if (loadPending) {
			requestedCenterMarker = marker;
		} else {
			centerMarker(marker);
		}
	}

	/**
	 * Processes pending scroll request (if any)
	 */
	public void processRequestScroll() {
		if (requestedScroll == null) {
			return;
		}

		scrollTo(requestedScroll[0], requestedScroll[1]);
		requestedScroll = null;
	}

	/**
	 * Scrolls to requested x,y position
	 *
	 * @param x
	 *            position
	 * @param y
	 *            position
	 * @param forceDelay
	 *            Whether scrolling should forced to be delayed after the page
	 *            loading
	 */
	public void requestScroll(int x, int y, boolean forceDelay) {
		if (loadPending || forceDelay) {
			requestedScroll = new int[] { x, y };
		} else {
			scrollTo(x, y);
		}
	}

	/**
	 * Requests that the view should be scrolled
	 *
	 * @param x
	 * @param y
	 */
	public void requestScroll(int x, int y) {
		requestScroll(x, y, false);
	}

	/**
	 * Shows a {@link Location} on the {@link MapView}
	 *
	 * @param location
	 *            The {@link Location} to be shown
	 * @param isCurrentLocation
	 *            Whether the {@link Location} is the current estimated location
	 */
	public void showLocation(Location location, boolean isCurrentLocation) {
		if (isCurrentLocation) {
			currentLocation = location;
		}

		removeAllMarkers();

		Map locMap = (Map) location.getMap();
		if (!locMap.equals(currentMap)) {
			currentMap = locMap;
			setupMapViewImage(currentMap);

		}

		checkCurrentLocationMarker();

		LocationMarker marker = addMarkerForLocation(location);

		showLocationMarkers();
		requestMarkerOnCenter(marker);

	}

	/**
	 * Setups the {@link MapView} image
	 *
	 * @param map
	 *            {@link Map} to be shown
	 */
	protected void setupMapViewImage(Map map) {
		showImage(map.getMapURL());
	}

	/**
	 * Shows a {@link Map}
	 *
	 * @param map
	 *            {@link Map} to be shown
	 */
	public void showMap(Map map) {
		if(map == null) {
			Log.v(TAG, "tried to show null map");
			return;
		}
		if (!map.equals(currentMap)) {
			currentMap = map;
			Log.v(TAG, "showMap: initializing new map view");
			removeAllMarkers();

			setupMapViewImage(currentMap);
			setupAllMarkers(currentMap);

			checkCurrentLocationMarker();

		} else {

			Log.v(TAG, "showMap: map already shown");
			List<Location> list = EntityHomeFactory.getLocationHome()
					.getListByMap(map);

			if (list.size() != locationMarker.size()) {
				Log
						.v(TAG,
								"showMap: map already shown, but location number changed");
				removeAllMarkers();
				setupMarkers(list);
			}

		}
		showLocationMarkers();
	}

	/**
	 * Checks if current location is on the same map and displays it if
	 * necessary.
	 */
	private void checkCurrentLocationMarker() {
		if (currentLocation != null && currentLocation.getMap() != null) {
			if (currentLocation.getMap().equals(currentMap)) {
				if (!locationMarker.containsKey(currentLocation)) {
					addMarkerForLocation(currentLocation);
				}
			} else {
				removeMarkerForLocation(currentLocation);
			}
		}

	}

	/**
	 * Removes a {@link LocationMarker}.
	 *
	 * @param location
	 *            {@link Location} of the {@link LocationMarker} that has to be
	 *            removed
	 */
	private void removeMarkerForLocation(Location location) {

		LocationMarker marker = locationMarker.remove(location);

		if (marker == null)
			return;

		marker.removeFromContainer();

	}

	/**
	 * Removes all {@link LocationMarker}
	 */
	private void removeAllMarkers() {
		for (LocationMarker m : locationMarker.values()) {
			m.removeFromContainer();
		}

		locationMarker.clear();
	}

	/**
	 * Adds {@link LocationMarker} for a {@link List} of {@link Location}s.
	 *
	 * @param list
	 *            {@link List} of {@link Location} to be shown
	 */
	private void setupMarkers(List<Location> list) {
		for (Location l : list) {
			addMarkerForLocation(l);
		}
	}

	/**
	 * Adds {@link LocationMarker} for all {@link Location}s of a {@link Map}
	 *
	 * @param map
	 *            {@link Map}
	 */
	private void setupAllMarkers(Map map) {
		setupMarkers(EntityHomeFactory.getLocationHome().getListByMap(map));
	}

	private boolean modifiable = false;

	/**
	 * Enables or disables the possibility to modify {@link LocationMarker}s and
	 * {@link LocationMarkerAnnotation}s.
	 *
	 * @param enabled
	 *            Whether the {@link MapView} should be modifiable
	 */
	public void setModifiable(boolean enabled) {

		if (modifiable == enabled)
			return;

		for (LocationMarker m : locationMarker.values()) {
			m.setEnabled(enabled);
		}
		modifiable = enabled;
	}

	Matrix lastMatrix = new Matrix();
	float lastScale;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMatrixChange(Matrix matrix, ZoomAndScrollImageView view) {
		lastMatrix = matrix;
		float[] values = new float[9];
		matrix.getValues(values);
		float scale = values[Matrix.MSCALE_X];

		lastScale = scale;

		float[] point = new float[] { 0, 0 };
		matrix.mapPoints(point);
		contentView.scrollTo((int) -point[0], (int) -point[1]);

		// contentView.startAnimation(sa);

		for (LocationMarker m : locationMarker.values()) {
			m.onScaleChanged(scale);
		}

		ViewGroup.LayoutParams params = contentView.getLayoutParams();
		params.width = (int) (imageWidth * scale);
		params.height = (int) (imageHeight * scale);
		contentView.setLayoutParams(params);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSingleTab(MotionEvent e) {
		for (LocationMarker l : locationMarker.values()) {
			if (l.clicked) {
				l.onClick(this);
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onScaleBegin(ZoomAndScrollImageView view) {
		contentView.setVisibility(INVISIBLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onScaleEnd(ZoomAndScrollImageView view) {
		Animation a = fadeIn();
		a.setDuration(a.getDuration() / 2);
		contentView.startAnimation(a);
		contentView.setVisibility(VISIBLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (imageView.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean dispatchTrackballEvent(MotionEvent event) {
		imageView.dispatchTrackballEvent(event);
		return true;
	}

	/*
	 * @Override protected void onRestoreInstanceState(Parcelable state) { if
	 * (!(state instanceof SavedState)) { super.onRestoreInstanceState(state);
	 * return; }
	 *
	 * //currentLocation =
	 * EntityHomeFactory.getLocationHome().getById(ss.currentLocation);
	 *
	 * SavedState ss = (SavedState)state;
	 * super.onRestoreInstanceState(ss.getSuperState());
	 *
	 * if (ss.shown != null) { show(ss.shown); }
	 *
	 * requestScroll(ss.scrollX, ss.scrollY);
	 *
	 *
	 *
	 * }
	 *
	 * @Override protected Parcelable onSaveInstanceState() { Parcelable
	 * superState = super.onSaveInstanceState(); SavedState ss = new
	 * SavedState(superState); ss.shown = currentUri; ss.scrollX = getScrollX();
	 * ss.scrollY = getScrollY(); return ss; }
	 *
	 *
	 * public static class SavedState extends BaseSavedState {
	 *
	 * Uri shown; int scrollX; int scrollY;
	 *
	 *
	 * public SavedState(Parcelable arg0) { super(arg0); }
	 *
	 * @Override public void writeToParcel(Parcel out, int flags) {
	 * super.writeToParcel(out, flags); Uri.writeToParcel(out, shown);
	 *
	 * out.writeInt(scrollX); out.writeInt(scrollY);
	 * //out.writeLong(currentLocation); //out.writeLong(currentMap);
	 *
	 * }
	 *
	 * public static final Parcelable.Creator<SavedState> CREATOR = new
	 * Parcelable.Creator<SavedState>() { public SavedState
	 * createFromParcel(Parcel in) { return new SavedState(in); }
	 *
	 * public SavedState[] newArray(int size) { return new SavedState[size]; }
	 * };
	 *
	 * private SavedState(Parcel in) {
	 *
	 * super(in);
	 *
	 * shown = Uri.CREATOR.createFromParcel(in); scrollX = in.readInt(); scrollY
	 * = in.readInt(); //currentLocation = in.readLong(); //currentMap =
	 * in.readLong();
	 *
	 * }
	 *
	 *
	 *
	 * }
	 */

}
