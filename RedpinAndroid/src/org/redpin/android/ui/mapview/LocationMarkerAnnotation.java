/**
 *  Filename: LocationMarkerAnnotation.java (in org.repin.android.ui.mapview)
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

import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * {@link LocationMarkerAnnotation} displays the symbolic id of an
 * {@link Location}
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class LocationMarkerAnnotation extends EditText implements
		OnEditorActionListener {

	private LocationMarker marker;
	private FrameLayout.LayoutParams layout;
	boolean markerPositionChanged = false;

	int default_width = 150;
	int default_height = 40;

	float density = 1;

	/**
	 *
	 * @param marker
	 *            {@link LocationMarker} the {@link LocationMarkerAnnotation}
	 *            belongs to
	 */
	public LocationMarkerAnnotation(LocationMarker marker) {
		super(marker.getContext());
		setVisibility(INVISIBLE);

		this.marker = marker;

		setSingleLine();

		setText(marker.getLocation().getSymbolicID());
		setEnabled(false);

		setOnEditorActionListener(this);
		setImeOptions(EditorInfo.IME_ACTION_DONE);
		setBackgroundResource(R.drawable.annotation);
		setGravity(Gravity.CENTER_HORIZONTAL);

		setTextColor(Color.WHITE);

		density = getContext().getResources().getDisplayMetrics().density;
		default_width = (int) (default_width * density);
		default_height = (int) (default_height * density);

		setWidth(default_width);
		setMinWidth(default_width);
		setMaxWidth(250);

		setMinHeight(default_height);
		setMaxHeight(default_height);
		setHeight(default_height);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutParams getLayoutParams() {
		if (layout == null || markerPositionChanged) {
			layout = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, Gravity.NO_GRAVITY);

			int w = getMeasuredWidth();
			int h = default_height;
			if (w == 0) {
				measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
				w = getMeasuredWidth();
				// w = default_width;

			}

			System.out.println("w, mw, mh: " + w + ", " + getMeasuredWidth()
					+ ", " + getMeasuredHeight());
			if(marker != null) {
				layout.setMargins(marker.markerX - w / 2 + LocationMarker.size / 2,
					marker.markerY - (h + (int) (3 * density)), 0, 0);
					markerPositionChanged = false;
			}
		}
		return layout;

	}

	/**
	 * Updates the symbolic id on the server if it was changed.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		if (actionId == EditorInfo.IME_ACTION_DONE) {
			// setCursorVisible(false);
			clearFocus();

			setSelected(false);

			Location location = marker.getLocation();
			String newSId = getText().toString();
			if (!location.getSymbolicID().equals(newSId)) {
				location.setSymbolicID(getText().toString());
				LocationRemoteHome.updateLocation(location);
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		if (visibility == VISIBLE) {
			setText(marker.getLocation().getSymbolicID());
		}
		super.onVisibilityChanged(changedView, visibility);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		markerPositionChanged = true;
		requestLayout();
	}

}
