package com.example.android_chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ColorView extends View {
	private int color = Color.BLACK;
	
	public ColorView(Context context){
		super(context);
	}
	
	public ColorView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public int getColor(){
		return color;
	}
	
	public void setColor(String color){
		this.color = Color.parseColor(color);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(16, getMeasuredHeight());
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		final Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		colorPaint.setColor(getColor());
		canvas.drawRect(0, 0, getWidth(), getHeight(), colorPaint);
	}
}
