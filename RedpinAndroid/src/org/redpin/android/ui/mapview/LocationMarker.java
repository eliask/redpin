/**
 *  Filename: LocationMarker.java (in org.repin.android.ui.mapview)
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

import org.redpin.android.R;
import org.redpin.android.core.Location;
import org.redpin.android.net.home.LocationRemoteHome;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * {@link LocationMarker} is the graphical representation of an {@link Location}
 * on the {@link MapView}
 * 
 * @author Pascal Brogle (broglep@student.ethz.ch)
 * 
 */
public class LocationMarker extends Button implements OnClickListener {

	private Location location;
	int markerX, markerY;

	static int size = 44;
	static boolean densityCalculated = false;

	private FrameLayout.LayoutParams layout;
	boolean clicked = false;
	boolean positionChanged = false;
	private float mLastMotionX;
	private float mLastMotionY;
	private int unscaledX;
	private int unscaledY;

	private float scale;
	private ViewGroup container;

	private LocationMarkerAnnotation annotation;
	private CurrentLocationCircle circle;

	private boolean isCurrentLocation = false;
	private float density;

	/**
	 * 
	 * @param context
	 *            {@link Context}
	 * @param l
	 *            {@link Location} that the marker respresent
	 * @param container
	 *            The {@link ViewGroup} the marker will be added to
	 */
	public LocationMarker(Context context, Location l, ViewGroup container) {
		super(context);
		this.container = container;
		initView(context);
		setLocation(l);
	}

	/**
	 * Initiates the {@link LocationMarker}
	 * 
	 * @param context
	 *            {@link Context}
	 */
	private void initView(Context context) {
		setVisibility(INVISIBLE);
		setIsCurrentLocation(false);
		updateBackground();

		setEnabled(false);
		setOnClickListener(this);

		if (!densityCalculated) {
			density = getContext().getResources().getDisplayMetrics().density;
			size = (int) (size * density);
			LocationMarker.densityCalculated = true;
		}

		setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, Gravity.NO_GRAVITY));

		setWidth(size);
		setHeight(size);

	}

	/**
	 * Sets the markers location
	 * 
	 * @param l
	 *            {@link Location}
	 */
	private void setLocation(Location l) {
		this.location = l;
		markerX = location.getMapXcord() - size / 2;
		markerY = location.getMapYcord() - size / 2;
		unscaledX = markerX;
		unscaledY = markerY;

		System.out.println("Unscaled Marker: " + unscaledX + "," + unscaledY);

		positionChanged();
	}

	/**
	 * 
	 * @return {@link Location} represented by the {@link LocationMarker}
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Updates the coordinates of the locations and saves the change on the
	 * server
	 */
	private void updateLocation() {
		updateLocationCords();
		LocationRemoteHome.updateLocation(location);
	}

	/**
	 * Updates the coordinates
	 */
	private void updateLocationCords() {
		location.setMapXcord(unscaledX + size / 2);
		location.setMapYcord(unscaledY + size / 2);
	}

	/**
	 * Changes the look of the marker depending if it represent an current
	 * {@link Location}
	 * 
	 * @param b
	 *            Whether the {@link Location} represented by the
	 *            {@link Location} is the current {@link Location}
	 */
	public void setIsCurrentLocation(boolean b) {
		isCurrentLocation = b;

		updateBackground();

		if (isCurrentLocation) {
			showAnnotation();
			showCircle();
		} else {
			hideAnnotation();
			hideCircle();
		}

	}

	private boolean enabled = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean b) {

		if (isCurrentLocation) {
			enabled = false;
			showAnnotation();
		} else {
			enabled = b;
		}

		if (annotation != null) {
			annotation.setEnabled(b);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(View v) {

		clicked = !clicked;

		updateBackground();

		if (clicked) {
			showAnnotation();
		} else {
			hideAnnotation();
		}

	}

	/**
	 * Updates the background depending on the state
	 */
	protected void updateBackground() {
		if (isCurrentLocation) {
			setBackgroundResource(R.drawable.current_location);
			return;
		}

		if (clicked && enabled) {
			setBackgroundResource(R.drawable.purple_map_pin_big);
		} else {
			setBackgroundResource(R.drawable.red_map_pin_big);
		}

		return;

	}

	/**
	 * Shows the {@link LocationMarkerAnnotation}
	 */
	void showAnnotation() {
		if (annotation == null) {
			annotation = new LocationMarkerAnnotation(this);

			container.addView(annotation);
		}
		annotation.setEnabled(enabled);
		annotation.setVisibility(getVisibility());
	}

	/**
	 * Starts editing of the {@link LocationMarkerAnnotation}
	 */
	void beginEdit() {
		if (annotation == null || !enabled)
			return;

		annotation.requestFocus();
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/**
	 * Hides the {@link LocationMarkerAnnotation}
	 */
	void hideAnnotation() {
		if (annotation == null)
			return;

		annotation.setVisibility(GONE);

	}

	/**
	 * Shows a {@link CurrentLocationCircle} around the {@link LocationMarker}
	 */
	private void showCircle() {
		if (circle == null) {
			circle = new CurrentLocationCircle(this, 25);
			container.addView(circle);
		}

		circle.setVisibility(getVisibility());
	}

	/**
	 * Hides {@link CurrentLocationCircle}
	 */
	private void hideCircle() {
		if (circle == null)
			return;

		circle.setVisibility(GONE);
	}

	boolean isDragging = false;

	/**
	 * Handles the dragging of the {@link LocationMarker}
	 * 
	 * @param ev
	 *            The motion event
	 * @return <code>True</code> if the event was handled, <code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (!enabled || (!clicked && !isDragging)) {
			return super.onTouchEvent(ev);
		}

		boolean handled = false;

		final int action = ev.getAction();
		final float y = ev.getY();
		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			System.out.println("start dragging " + mLastMotionX + ","
					+ mLastMotionY);

			break;

		case MotionEvent.ACTION_MOVE:

			isDragging = true;

			final float scrollX = mLastMotionX - x;
			final float scrollY = mLastMotionY - y;

			System.out.println("move by " + scrollX + "," + scrollY);
			moveMarkerBy((int) scrollX, (int) scrollY);

			break;

		case MotionEvent.ACTION_UP:
			// dragging is finished, update location
			if (isDragging) {
				handled = true;
				updateLocation();
				isDragging = false;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			isDragging = false;
			break;
		}

		if (handled) {
			return true;
		}

		return super.onTouchEvent(ev);

	}

	/**
	 * Notify {@link LocationMarkerAnnotation} and {@link CurrentLocationCircle}
	 * that the {@link LocationMarker} position changed
	 */
	public void positionChanged() {
		layout = (android.widget.FrameLayout.LayoutParams) getLayoutParams();
		layout.setMargins(markerX, markerY, 0, 0);
		setLayoutParams(layout);

		if (annotation != null) {
			annotation.markerPositionChanged = true;
		}
		if (isCurrentLocation && circle != null) {
			circle.markerPositionChanged = true;
		}
	}

	/**
	 * Moves the marker by specified distance
	 * 
	 * @param distanceX
	 *            Distance in X direction
	 * @param distanceY
	 *            Distance in Y direction
	 */
	private void moveMarkerBy(int distanceX, int distanceY) {

		setMarkerXY(markerX - distanceX, markerY - distanceY);

		unscaledX = (int) (markerX / scale);
		unscaledY = (int) (markerY / scale);

		System.out.println("Marker: " + markerX + "," + markerY);
		System.out.println("Unscaled Marker: " + unscaledX + "," + unscaledY);
	}

	public float[] getPosition() {
		return new float[] { markerX, markerY };
	}

	public void onScaleChanged(float newScale) {
		if (newScale == 0)
			return;

		scale = newScale;

		int x, y;

		x = (int) ((((float) (unscaledX + size / 2)) * scale) - size / 2);
		y = (int) ((((float) (unscaledY + size / 2)) * scale) - size / 2);
		setMarkerXY(x, y);

	}

	int lastMarkerX = -1;
	int lastMarkerY = -1;

	private void setMarkerXY(int x, int y) {

		int oldX = markerX;
		int oldY = markerY;
		markerX = x;
		markerY = y;

		positionChanged();
		requestLayout();

		lastMarkerX = oldX;
		lastMarkerY = oldY;

	}

	/**
	 * Removes the {@link LocationMarker} from the container and if applicable
	 * the {@link LocationMarkerAnnotation} and {@link CurrentLocationCircle}
	 */
	protected void removeFromContainer() {
		if (annotation != null) {
			container.removeView(annotation);
		}
		if (circle != null) {
			container.removeView(circle);
		}

		container.removeView(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibility(int visibility) {
		if (annotation != null) {
			annotation.setVisibility(visibility);
		}

		if (circle != null) {
			circle.setVisibility(visibility);
		}
		super.setVisibility(visibility);
	}

}
