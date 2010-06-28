package org.redpin.android.ui.mapview;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.ZoomButtonsController;
import android.widget.RemoteViews.ActionException;
import android.widget.ZoomButtonsController.OnZoomListener;

public class ZoomAndScrollImageView extends View implements OnZoomListener, OnDoubleTapListener, OnGestureListener, OnScaleGestureListener {
	
	private static final String TAG = ZoomAndScrollImageView.class.getSimpleName();

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

	
	
	private static float[] ORIGIN = new float[] {0, 0};
	private float[] destination;

	private Picture picture;

	public ZoomAndScrollImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}

	public ZoomAndScrollImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ZoomAndScrollImageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		setFocusable(true);
		scroller = new Scroller(context);
		gestureDetector = new GestureDetector(context, this);
		scaleGestureDetector = new ScaleGestureDetector(context, this);
		
		zoomController = new ZoomButtonsController(this);
		zoomController.setOnZoomListener(this);
		
		matrix = new Matrix();
		destination = new float[2];
		
		setVerticalScrollBarEnabled(true);
		setHorizontalScrollBarEnabled(true);
				
	}
	
	public void setImageBitmap(Bitmap bitmap) {
		setZoom(1.0f, false);
		setContentSize(bitmap.getWidth(), bitmap.getHeight());
		
		picture = new Picture();
		Canvas c = picture.beginRecording(bitmap.getWidth(), bitmap.getHeight());
		c.drawBitmap(bitmap, 0, 0, null);
		picture.endRecording();
	}
	
	public void setImageDrawable(BitmapDrawable bDrawable) {
		setImageBitmap(bDrawable.getBitmap());
	}
	
	public void adjustMinZoom() {
		int w = getWidth();
		int h = getHeight();
		
		MIN_ZOOM = Math.max(w/contentWidth, h/contentHeight);
	}
	
	public void setContentSize(int width, int height) {
		adjustMinZoom();
	    contentWidth = width;
	    contentHeight = height;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	

	private void notifyMatrix(Matrix m) {
		if(listener != null) {
			listener.onMatrixChange(m, this);
		}
		
	}
	
	@Override
	public void scrollBy(int x, int y) {
		
	}

	@Override
	public void scrollTo(int x, int y) {
		currentX = -x;
		currentY = -y;

		currentX = Math.max(getWidth() - contentWidth, Math.min(0, currentX));
		currentY = Math.max(getHeight() - contentHeight, Math.min(0, currentY));
		
		invalidate();
	}

	/**
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
		
		changeZoom(zoomedIn ? 1 : -1, oldX, currentX, oldY,  currentY);

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

	/**
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
		scroller.fling((int)currentX, (int)currentY, (int) (velocityX / velocityFactor), (int) (velocityY / velocityFactor), minX, 0, minY, 0);
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
		
		//notifyOnScroll();
		
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

	/**
	 * {@link OnZoomListener}
	 */
	@Override
	public void onVisibilityChanged(boolean visible) {		
	}

	@Override
	public void onZoom(boolean zoomIn) {		
		float toX = currentX;
		float toY = currentY;
		changeZoom(zoomIn ? ZOOM_STEP : -ZOOM_STEP, currentX, toX, currentY, toY);
	}

	public void changeZoom(float amount, float fromX, float toX, float fromY, float toY) {
		
		myTranslation.start(amount, fromX, toX, fromY, toY);

	}
	
	public void setZoom(float zoom, boolean adjust) {
		scale = zoom;
		if(adjust) {
			scale = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, scale));
		}
		
		zoomController.setZoomInEnabled(scale != MAX_ZOOM);
		zoomController.setZoomOutEnabled(scale != MIN_ZOOM);
		invalidate();
	}

	/**
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
		boolean isAnimating = false;
		
		if (myTranslation != null && myTranslation.hasStarted() && !myTranslation.hasEnded()) {
			myTranslation.getTransformation(AnimationUtils.currentAnimationTimeMillis(), null);
			isAnimating = true;
		} 
		
		int saveCount = canvas.save();
		
		if (scroller.computeScrollOffset()) {
			currentX = scroller.getCurrX();
			currentY = scroller.getCurrY();
			//notifyOnScroll();
			invalidate();
		}
		
		//System.out.println("CurrentXY: " + currentX + "," + currentY);
		
		int width = getWidth();		
		int height = getHeight();
		
		matrix.reset();
		
		float scaledWidth = contentWidth * scale;
		float scaledHeigth = contentHeight * scale;
		
		float transX = scaledWidth > width ? currentX * scale : (width - scaledWidth) / 2;
		float transY = scaledHeigth > height ? currentY * scale : (height - scaledHeigth) / 2;
		
		matrix.preTranslate(transX, transY);
			
		float pivotX = 0;
		if (scaledWidth > width) {
			pivotX = Math.max(Math.min(-currentX, width / 2), 2 * width - contentWidth - currentX);
		}
		
		float pivotY = 0;
		if (scaledHeigth > height) {
			pivotY = Math.max(Math.min(-currentY, height / 2), 2 * height - contentHeight - currentY);
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
		return Math.round(computeHorizontalScrollRange() * getWidth() / (contentWidth * scale));
	}
	
	@Override
	protected int computeHorizontalScrollOffset() {
		matrix.mapPoints(destination, ORIGIN);
		float x = -destination[0] / scale;
		return Math.round(computeHorizontalScrollRange() * x / contentWidth);
	}
	
	@Override
	protected int computeVerticalScrollExtent() {
		return Math.round(computeVerticalScrollRange() * getHeight() / (contentHeight * scale));
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
			currentX = Math.max(getWidth() - contentWidth, Math.min(0, currentX));			
			
			invalidate();
			handeled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			currentX -= DPAD_MOVEMENT_STEP / scale;									
			currentX = Math.max(getWidth() - contentWidth, Math.min(0, currentX));
			
			invalidate();
			handeled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:

			currentY += DPAD_MOVEMENT_STEP / scale;
			currentY = Math.max(getHeight() - contentHeight, Math.min(0, currentY));
			
			invalidate();
			handeled = true;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:		
			
			currentY -= DPAD_MOVEMENT_STEP / scale;
			currentY = Math.max(getHeight() - contentHeight, Math.min(0, currentY));
			
			invalidate();
			handeled = true;
			break;
			
		case KeyEvent.KEYCODE_DPAD_CENTER:
			zoomedIn = !zoomedIn;
			changeZoom(zoomedIn ? 1 : -1, currentX, currentX, currentX,  currentY);	
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
			changeZoom(zoomedIn ? 1 : -1, currentX, currentX, currentX,  currentY);	
			handeled = true;
			break;

		case MotionEvent.ACTION_MOVE:
			currentX -= e.getX()*DPAD_MOVEMENT_STEP / scale;
			currentY -= e.getY()*DPAD_MOVEMENT_STEP / scale;
						
			currentX = Math.max(getWidth() - contentWidth, Math.min(0, currentX));
			currentY = Math.max(getHeight() - contentHeight, Math.min(0, currentY));
			
			handeled = true;
			invalidate();
			break;
			
		default:
			break;
		}	
		
		return handeled;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		adjustMinZoom();
	}


	public interface OnDrawListener {
	    public void onDraw(Canvas canvas, Matrix matrix);
	}
	
	
	ZoomAndTranslate myTranslation = new ZoomAndTranslate();
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
        public void start(float amount, float fromX, float toX, float fromY, float toY) {
        	this.fromX = fromX;
        	this.toX = toX;
        	this.fromY = fromY;
        	this.toY = toY;
        	
	        translateInterpolator = new DecelerateInterpolator(); //new LinearInterpolator();// new DecelerateInterpolator();
	        zoomInterpolator = new AccelerateDecelerateInterpolator(); // new LinearInterpolator();// AccelerateDecelerateInterpolator();
	        mFrom = scale;
	        mTo = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, scale + amount));
	            
	        start();
            long t = AnimationUtils.currentAnimationTimeMillis();
            getTransformation(t, null);
	    }
	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        float time = interpolatedTime;

	        float tInterpolatedTime = translateInterpolator.getInterpolation(time);
	        float zInterpolatedTime = zoomInterpolator.getInterpolation(time);
	        currentX = fromX + (toX -fromX) * tInterpolatedTime;
	        currentY = fromY + (toY -fromY) * tInterpolatedTime;
	        setZoom(mFrom + (mTo - mFrom) * zInterpolatedTime, false);

	   }
	}
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		//Log.i(TAG, "onScale, factor:" + detector.getScaleFactor());
		float factor = detector.getScaleFactor();
		
		scale *= factor;
		setZoom(scale, true);
		Log.i(TAG, "onScale, " + scale);
		
	
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		notifyScaleBegin();
		Log.i(TAG, "onScale Begin");
		return true;
	}

	private void notifyScaleBegin() {
		if (listener != null) {
			listener.onScaleBegin(this);
		}
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		
		notifyScaleEnd();
		Log.i(TAG, "onScale End");
		System.out.println("End scale: " + scale);

	}
	
	private void notifyScaleEnd() {
		if (listener != null) {
			listener.onScaleEnd(this);
		}
	}

	public void setListener(ZoomAndScrollViewListener l) {
		listener = l;
	}
	
	
	public interface ZoomAndScrollViewListener {
		public void onMatrixChange(Matrix m, ZoomAndScrollImageView view);
		public void onScaleBegin(ZoomAndScrollImageView view);
		public void onScaleEnd(ZoomAndScrollImageView view);

		public void onSingleTab(MotionEvent e);
	}

}
