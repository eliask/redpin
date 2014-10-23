/**
 *  Filename: CurrentLocationCircle.java (in org.repin.android.ui.mapview)
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

import org.redpin.android.core.Location;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

/**
 * {@link CurrentLocationCircle} displays a circle around the current
 * {@link Location}
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class CurrentLocationCircle extends View {

	private LocationMarker marker;
	private FrameLayout.LayoutParams layout;
	boolean markerPositionChanged = false;
	private float radius;

	Paint redFilledPaint;
	Paint redLinePaint;
	private float density;
	private float strokeWidth = 2;

	/**
	 *
	 * @param marker
	 *            {@link LocationMarker} the {@link CurrentLocationCircle}
	 *            belongs to
	 * @param radius
	 *            Radius of the circle
	 */
	public CurrentLocationCircle(LocationMarker marker, float radius) {
		super(marker.getContext());

		setVisibility(INVISIBLE);

		density = getContext().getResources().getDisplayMetrics().density;

		this.marker = marker;
		this.radius = radius * density;
		this.strokeWidth = strokeWidth * density;

		setMinimumWidth((int) ((radius * 2) + strokeWidth * 2));
		setMinimumHeight((int) ((radius * 2) + strokeWidth * 2));

		redFilledPaint = new Paint();
		redFilledPaint.setStyle(Style.FILL);
		redFilledPaint.setColor(Color.RED);
		redFilledPaint.setAlpha((int) (255.0 * 0.1f));

		redLinePaint = new Paint();
		redLinePaint.setStyle(Style.STROKE);
		redLinePaint.setStrokeWidth(strokeWidth);
		redLinePaint.setColor(Color.RED);
		redLinePaint.setAntiAlias(true);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutParams getLayoutParams() {
		if (layout == null || markerPositionChanged) {
			layout = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, Gravity.NO_GRAVITY);

			int offset = (int) (radius + strokeWidth)
					- (LocationMarker.size / 2);
			layout.setMargins(marker.markerX - offset, marker.markerY - offset,
					0, 0);
			markerPositionChanged = false;
		}
		return layout;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(radius + strokeWidth, radius + strokeWidth, radius,
				redFilledPaint);
		canvas.drawCircle(radius + strokeWidth, radius + strokeWidth, radius,
				redLinePaint);
		super.onDraw(canvas);
	}

}