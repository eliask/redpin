/**
 *  Filename: ZoomAndScrollImageView.java (in org.repin.android.ui.mapview)
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.Scroller;
import android.widget.ZoomButtonsController;
import android.widget.ZoomButtonsController.OnZoomListener;

/**
 * ImageView that is capable of zooming and scrolling an image.
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class ZoomAndScrollImageView extends View implements OnZoomListener,
		OnDoubleTapListener, OnGestureListener, OnScaleGestureListener {

	private static final String TAG = ZoomAndScrollImageView.class
			.getSimpleName();

	private static final float ZOOM_STEP = 0.5f;

	private static final float DPAD_MOVEMENT_STEP = 20;

	static float MAX_ZOOM = 4.0f;
	static float MIN_ZOOM = 1f;

	float scale = 1.0f;

	private Scroller scroller;
	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;

	private ZoomButtonsController zoomController;
	private Matrix matrix;

	private float currentX;
	private float currentY;
	private float contentWidth;
	private float contentHeight;

	private ZoomAndScrollViewListener listener;

	private static float[] ORIGIN = new float[] { 0, 0 };
	private float[] destination;

	private Picture picture;

	/**
	 * Construct a new ZoomAndScrollImageView with layout parameters and a
	 * default style.
	 *
	 * @param context
	 *            A Context object used to access application assets.
	 * @param attrs
	 *            An AttributeSet passed to our parent.
	 * @param defStyle
	 *            The default style resource ID.
	 */
	public ZoomAndScrollImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}

	/**
	 * Construct a new ZoomAndScrollImageView with layout parameters.
	 *
	 * @param context
	 *            A Context object used to access application assets.
	 * @param attrs
	 *            An AttributeSet passed to our parent.
	 */
	public ZoomAndScrollImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Construct a new ZoomAndScrollImageView with a Context object.
	 *
	 * @param context
	 *            A Context object used to access application assets.
	 */
	public ZoomAndScrollImageView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Initializes the view
	 *
	 * @param context
	 *            {@link Context}
	 */
	private void init(Context context) {
		setFocusable(true);
		scroller = new Scroller(context);
		gestureDetector = new GestureDetector(context, this);
		scaleGestureDetector = new ScaleGestureDetector(context, this);

		zoomController = new ZoomButtonsController(this);
		zoomController.setOnZoomListener(this);

		matrix = new Matrix();
		destination = new float[2];

		// setVerticalScrollBarEnabled(true);
		// setHorizontalScrollBarEnabled(true);

	}

	/**
	 * Displays a bitmap
	 *
	 * @param bitmap
	 *            {@link Bitmap}
	 */
	public void setImageBitmap(Bitmap bitmap) {
		setZoom(1.0f, false);
		setContentSize(bitmap.getWidth(), bitmap.getHeight());

		picture = new Picture();
		Canvas c = picture
				.beginRecording(bitmap.getWidth(), bitmap.getHeight());
		c.drawBitmap(bitmap, 0, 0, null);
		picture.endRecording();
	}

	/**
	 * Displays a drawable
	 *
	 * @param bDrawable
	 *            {@link BitmapDrawable}
	 */
	public void setImageDrawable(BitmapDrawable bDrawable) {
		setImageBitmap(bDrawable.getBitmap());
	}

	/**
	 * Adjusts minimal zoom level depending on the image size
	 */
	public void adjustMinZoom() {
		int w = getWidth();
		int h = getHeight();
		if (w == 0 || h == 0) {
			return;
		}

		MIN_ZOOM = Math.max(w / contentWidth, h / contentHeight);
	}

	/**
	 * Sets the image content size
	 *
	 * @param width
	 *            Image width
	 * @param height
	 *            Image height
	 */
	public void setContentSize(int width, int height) {
		contentWidth = width;
		contentHeight = height;
		adjustMinZoom();
	}

	/**
	 *
	 * @return Current zoom scale
	 */
	public float getScale() {
		return scale;
	}

	/**
	 *
	 * @param scale
	 *            Desired zoom scale
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * Notifies the listener about the changed matrix
	 *
	 * @param m
	 *            Changed matrix
	 */
	private void notifyMatrix(Matrix m) {
		if (listener != null) {
			listener.onMatrixChange(m, this);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrollBy(int x, int y) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrollTo(int x, int y) {
		currentX = -x;
		currentY = -y;

		currentX = Math.max(getWidth() - contentWidth, Math.min(0, currentX));
		currentY = Math.max(getHeight() - contentHeight, Math.min(0, currentY));

		invalidate();
	}

	/**
	 *
	 * @return current x and y coordinate of the
	 */
	public float[] getCurrentXY() {
		return new float[] { currentX, currentY };
	}

	/*
	 * {@link OnDoubleTapListener}
	 */

	private boolean zoomedIn = false;

	@Override
	public boolean onDoubleTap(MotionEvent e) {

		float oldX, oldY;
		oldX = currentX;
		oldY = currentY;
		currentX -= (e.getX() - getWidth() / 2) / scale;
		currentY -= (e.getY() - getHeight() / 2) / scale;

		currentX = Math.max(getWidth() - contentWidth, Math.min(0, currentX));

		zoomedIn = !zoomedIn;

		changeZoom(zoomedIn ? 1 : -1, oldX, currentX, oldY, currentY);

		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		if (listener != null) {
			listener.onSingleTab(e);
		}
		return true;
	}

	/*
	 * {@link OnGestureListener}
	 */
	@Override
	public boolean onDown(MotionEvent e) {
		zoomController.setVisible(false);
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		final float velocityFactor = 1.5f;
		int minX = (int) (getWidth() - contentWidth);
		int minY = (int) (getHeight() - contentHeight);
		scroller.fling((int) currentX, (int) currentY,
				(int) (velocityX / velocityFactor),
				(int) (velocityY / velocityFactor), minX, 0, minY, 0);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		zoomController.setVisible(true);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		currentX -= distanceX / scale;
		currentY -= distanceY / scale;

		currentX = Math.max(getWidth() - contentWidth, Math.min(0, currentX));
		currentY = Math.max(getHeight() - contentHeight, Math.min(0, currentY));

		// notifyOnScroll();

		invalidate();
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/*
	 * {@link OnZoomListener}
	 */
	@Override
	public void onVisibilityChanged(boolean visible) {
	}

	@Override
	public void onZoom(boolean zoomIn) {
		float toX = currentX;
		float toY = currentY;
		changeZoom(zoomIn ? ZOOM_STEP : -ZOOM_STEP, currentX, toX, currentY,
				toY);
	}

	public void changeZoom(float amount, float fromX, float toX, float fromY,
			float toY) {

		myTranslation.start(amount, fromX, toX, fromY, toY);

	}

	public void setZoom(float zoom, boolean adjust) {
		Log.d(TAG, "Before: " + zoom + "(max: " + MAX_ZOOM + ",min: "
				+ MIN_ZOOM + ")");
		scale = zoom;
		if (adjust) {
			scale = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, scale));
		}
		Log.d(TAG, "After: " + scale);
		zoomController.setZoomInEnabled(scale != MAX_ZOOM);
		zoomController.setZoomOutEnabled(scale != MIN_ZOOM);
		invalidate();
	}

	/*
	 * {@link View}
	 */
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		zoomController.setVisible(false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean h1 = scaleGestureDetector.onTouchEvent(ev);
		boolean h2 = gestureDetector.onTouchEvent(ev);

		return h1 || h2;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (myTranslation != null && myTranslation.hasStarted()
				&& !myTranslation.hasEnded()) {
			myTranslation.getTransformation(AnimationUtils
					.currentAnimationTimeMillis(), null);
		}

		int saveCount = canvas.save();

		if (scroller.computeScrollOffset()) {
			currentX = scroller.getCurrX();
			currentY = scroller.getCurrY();
			invalidate();
		}

		int width = getWidth();
		int height = getHeight();

		matrix.reset();

		float scaledWidth = contentWidth * scale;
		float scaledHeigth = contentHeight * scale;

		float transX = scaledWidth > width ? currentX * scale
				: (width - scaledWidth) / 2;
		float transY = scaledHeigth > height ? currentY * scale
				: (height - scaledHeigth) / 2;

		matrix.preTranslate(transX, transY);

		float pivotX = 0;
		if (scaledWidth > width) {
			pivotX = Math.max(Math.min(-currentX, width / 2), 2 * width
					- contentWidth - currentX);
		}

		float pivotY = 0;
		if (scaledHeigth > height) {
			pivotY = Math.max(Math.min(-currentY, height / 2), 2 * height
					- contentHeight - currentY);
		}

		matrix.preScale(scale, scale, pivotX, pivotY);

		notifyMatrix(matrix);

		canvas.concat(matrix);

		if (picture != null) {
			picture.draw(canvas);
		}

		canvas.restoreToCount(saveCount);

	}

	@Override
	protected int computeHorizontalScrollExtent() {
		return Math.round(computeHorizontalScrollRange() * getWidth()
				/ (contentWidth * scale));
	}

	@Override
	protected int computeHorizontalScrollOffset() {
		matrix.mapPoints(destination, ORIGIN);
		float x = -destination[0] / scale;
		return Math.round(computeHorizontalScrollRange() * x / contentWidth);
	}

	@Override
	protected int computeVerticalScrollExtent() {
		return Math.round(computeVerticalScrollRange() * getHeight()
				/ (contentHeight * scale));
	}

	@Override
	protected int computeVerticalScrollOffset() {
		matrix.mapPoints(destination, ORIGIN);
		float y = -destination[1] / scale;
		return Math.round(computeVerticalScrollRange() * y / contentHeight);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handeled = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			currentX += DPAD_MOVEMENT_STEP / scale;
			currentX = Math.max(getWidth() - contentWidth, Math
					.min(0, currentX));

			invalidate();
			handeled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			currentX -= DPAD_MOVEMENT_STEP / scale;
			currentX = Math.max(getWidth() - contentWidth, Math
					.min(0, currentX));

			invalidate();
			handeled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:

			currentY += DPAD_MOVEMENT_STEP / scale;
			currentY = Math.max(getHeight() - contentHeight, Math.min(0,
					currentY));

			invalidate();
			handeled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:

			currentY -= DPAD_MOVEMENT_STEP / scale;
			currentY = Math.max(getHeight() - contentHeight, Math.min(0,
					currentY));

			invalidate();
			handeled = true;
			break;

		case KeyEvent.KEYCODE_DPAD_CENTER:
			zoomedIn = !zoomedIn;
			changeZoom(zoomedIn ? 1 : -1, currentX, currentX, currentX,
					currentY);
			handeled = true;
			break;
		default:
			break;
		}

		return handeled;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent e) {

		boolean handeled = false;

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			zoomedIn = !zoomedIn;
			changeZoom(zoomedIn ? 1 : -1, currentX, currentX, currentX,
					currentY);
			handeled = true;
			break;

		case MotionEvent.ACTION_MOVE:
			currentX -= e.getX() * DPAD_MOVEMENT_STEP / scale;
			currentY -= e.getY() * DPAD_MOVEMENT_STEP / scale;

			currentX = Math.max(getWidth() - contentWidth, Math
					.min(0, currentX));
			currentY = Math.max(getHeight() - contentHeight, Math.min(0,
					currentY));

			handeled = true;
			invalidate();
			break;

		default:
			break;
		}

		return handeled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		adjustMinZoom();
	}

	ZoomAndTranslate myTranslation = new ZoomAndTranslate();

	/**
	 * Animation that zoom and translates to a given position
	 *
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	class ZoomAndTranslate extends Animation {
		private static final int DURATION = 1000;

		private float mFrom;
		private float mTo;
		private Interpolator translateInterpolator;

		private float fromX;
		private float toX;
		private float fromY;
		private float toY;
		private Interpolator zoomInterpolator;

		public ZoomAndTranslate() {
			setDuration(DURATION);
			setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					notifyScaleBegin();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					notifyScaleEnd();
				}
			});
		}

		/**
		 * Starts the animation
		 *
		 * @param amount
		 *            Zoom amount
		 * @param fromX
		 *            From x coordinate
		 * @param toX
		 *            To x coordinate
		 * @param fromY
		 *            From y coordinate
		 * @param toY
		 *            To y coordinate
		 */
		public void start(float amount, float fromX, float toX, float fromY,
				float toY) {
			this.fromX = fromX;
			this.toX = toX;
			this.fromY = fromY;
			this.toY = toY;

			translateInterpolator = new DecelerateInterpolator(); // new
			// LinearInterpolator();//
			// new
			// DecelerateInterpolator();
			zoomInterpolator = new AccelerateDecelerateInterpolator(); // new
			// LinearInterpolator();//
			// AccelerateDecelerateInterpolator();
			mFrom = scale;
			mTo = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, scale + amount));

			start();
			long t = AnimationUtils.currentAnimationTimeMillis();
			getTransformation(t, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			float time = interpolatedTime;

			float tInterpolatedTime = translateInterpolator
					.getInterpolation(time);
			float zInterpolatedTime = zoomInterpolator.getInterpolation(time);
			currentX = fromX + (toX - fromX) * tInterpolatedTime;
			currentY = fromY + (toY - fromY) * tInterpolatedTime;
			setZoom(mFrom + (mTo - mFrom) * zInterpolatedTime, false);

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		// Log.i(TAG, "onScale, factor:" + detector.getScaleFactor());
		float factor = detector.getScaleFactor();

		scale *= factor;
		setZoom(scale, true);
		Log.i(TAG, "onScale, " + scale + "(factor: +" + factor + ")");

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		notifyScaleBegin();
		Log.i(TAG, "onScale Begin");
		return true;
	}

	/**
	 * Notifies the beginning of the scaling to the listener
	 */
	private void notifyScaleBegin() {
		if (listener != null) {
			listener.onScaleBegin(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

		notifyScaleEnd();
		Log.i(TAG, "onScale End");
		System.out.println("End scale: " + scale);

	}

	/**
	 * Notifies the ending of the scaling to the listener
	 */
	private void notifyScaleEnd() {
		if (listener != null) {
			listener.onScaleEnd(this);
		}
	}

	public void setListener(ZoomAndScrollViewListener l) {
		listener = l;
	}

	/**
	 * Listener-Interface for {@link ZoomAndScrollImageView}
	 *
	 * @author Pascal Brogle (broglep@student.ethz.ch)
	 *
	 */
	public interface ZoomAndScrollViewListener {
		/**
		 * Called when a change in the drawing matrix occours
		 *
		 * @param m
		 *            New matrix
		 * @param view
		 *            View that calls the method
		 */
		public void onMatrixChange(Matrix m, ZoomAndScrollImageView view);

		/**
		 * Called when the user begins to scale
		 *
		 * @param view
		 *            View that calls the method
		 */
		public void onScaleBegin(ZoomAndScrollImageView view);

		/**
		 * Called when the user ends scaling
		 *
		 * @param view
		 *            View that calls the method
		 */
		public void onScaleEnd(ZoomAndScrollImageView view);

		/**
		 * Called when the user tabs the view
		 *
		 * @param e
		 *            MotionEvent
		 */
		public void onSingleTab(MotionEvent e);
	}

}
