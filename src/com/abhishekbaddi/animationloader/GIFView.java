/*
 * ----------------------------------------------------------------------------
 * "THE BURGER-WARE LICENSE" (Revision 42):
 * <abybaddi009 gmail.com=""> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a burger in return. Abhishek Baddi
 * ----------------------------------------------------------------------------
 */

package com.abhishekbaddi.animationloader;

import java.io.InputStream;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

public class GIFView extends View {
	private Movie movie;								//class that loads the animation
	private int gifID;									//stores the id of the drawable gif file
	private long movieStart;							//the time of start of movie
	private float x, y;									//stores the x and y position of the view from the parent layout

	public int getGifID() {
		return gifID;
	}

	public void setGifID(int gifID) {
		this.gifID = gifID;
	}

	public GIFView(Context context) {
		super(context);
	}

	public GIFView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//initialize the view and the above variables from the attributes
		initializeView(context, attrs); 
	}

	private void initializeView(Context context, AttributeSet attrs) {									
		
		//the name of the stylable as defined in the attrs.xml
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.GIFView, 0, 0);
		
		//extract the src string
		String gifSource = a.getString(R.styleable.GIFView_src);
		String sourceName = Uri.parse(gifSource).getLastPathSegment()
				.replace(".gif", "");
		gifID = getResources().getIdentifier(sourceName, "drawable",
				getContext().getPackageName());
		a.recycle();
		
		//setting the x and y co-ordinate of the view
		x = getX();
		y = getY();
		if (gifID != 0) {
			InputStream is = getContext().getResources().openRawResource(gifID);
			movie = Movie.decodeStream(is);
			movieStart = 0;													
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
		//String tag = "width: " + width + "\nheight: " + height + "\nwidthSpec: " + widthMeasureSpec + "\nheighSpect: " + heightMeasureSpec + "\nmovwidth: " + movie.width() + "\nmovheight: " + movie.height();
		//Toast.makeText(getContext(), tag, Toast.LENGTH_SHORT).show();
	}

	private int measureWidth(int measureSpec) {
		int preferred = movie.width();
		return getMeasurement(measureSpec, preferred);
	}

	private int measureHeight(int measureSpec) {
		int preferred = movie.height();
		return getMeasurement(measureSpec, preferred);
	}

	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement = 0;

		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.EXACTLY:
			// This means the width of this view has been given.
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			// Take the minimum of the preferred size and what
			// we were told to be.
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}

		return measurement;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		super.onDraw(canvas);
		long now = android.os.SystemClock.uptimeMillis();

		if (movieStart == 0) {
			movieStart = now;
		}
		if (movie != null) {
			int relTime = (int) ((now - movieStart) % movie.duration());
			movie.setTime(relTime);
			movie.draw(canvas, x, y);
			this.invalidate();
		}
	}

}
