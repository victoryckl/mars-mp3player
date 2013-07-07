package ckl.lrc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class LrcView extends TextView {

	private static final String TAG = "MyView";
	private float float1 = 0.0f;
	private float float2 = 0.01f;
	private Handler handler;
	private Paint mPaint;
	private Paint mLightPaint;
	private float mOffsetY = 0.0f;
	private float mDy = 2.0f;
	private double mDf = 0.1f;
	private int mLightIndex = -1;
	private long mPlayTime;
	public static int count = 0;

	public LrcView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LrcView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setFocusable(true);
		if(mList==null){
			mList=new ArrayList<LrcSentence>();
			mList.add(0, new LrcSentence("nothing -_-!", Long.MAX_VALUE));
		}
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(34);
		mPaint.setColor(Color.BLACK);
		
		mLightPaint = new Paint(mPaint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mList == null || mList.size() == 0) {
			return ;
		}
		int size = mList.size();
		int vWidth = getWidth();
		int vHeight = getHeight();
		float textW = 0;
		int light = mLightIndex;
		String text = null;
		
		for (int i = 0; i < size; i++) {
//			if (i != light) {
				text = mList.get(i).getLrc();
				textW = mPaint.measureText(text);
				float x = (vWidth - textW)/2;
				float y = vHeight/2 + (i * 2) * mPaint.getTextSize() - mOffsetY;
				canvas.drawText(text, x, y, mPaint);
//			}
		}
		
		calcFloat();
		
		if (light >= 0 && light < mList.size()) {
			text = mList.get(light).getLrc();
			textW = mLightPaint.measureText(text);
			float x = (vWidth - textW)/2;
			float y = vHeight/2 + (light * 2) * mPaint.getTextSize() - mOffsetY;
			
			int[] a = new int[] { Color.YELLOW, Color.BLACK };
			float[] f = new float[] { float1, float2 };
			Shader shader = new LinearGradient(
					x, 0, 
					x + textW, 0, 
					a, f, TileMode.CLAMP);
			mLightPaint.setShader(shader);
			
	//		String msg = String.format("vW:%d, vH:%d, textW:%f, (%f,%f)", vWidth, vHeight, textW, x, y);
	//		Log.i(TAG, msg);
			
			canvas.drawText(mList.get(light).getLrc(), x, y, mLightPaint);
		}
		
		mOffsetY += mDy;
	}
	
	private void calcFloat() {
		float1 += mDf;
		float2 += mDf;
		if (float2 > 1.0 + mDf) {
			float1 = 0.0f;
			float2 = 0.01f;
			mLightIndex = -1; 
		}
	}
	
	public void setCurrentInfo(Intent intent) {
		mLightIndex = intent.getIntExtra("index", -1);
		if (mLightIndex < 0 || mLightIndex >= mList.size()) {
			return;
		}
		if (mPaint.measureText(mList.get(mLightIndex).getLrc()) <= 1) {
			mLightIndex = -1;
			return;
		}
		mPlayTime = intent.getLongExtra("playtime", 0);
		mDf = 1.0 / (mPlayTime / mTick);
		float1 = 0.0f;
		float2 = 0.01f;
	}
	
	private void update() {
		postInvalidate();
	}
	
	private List<LrcSentence> mList;
	private long mTotalTime;
	private long mTick = 200;
	public List<LrcSentence> getList() {
		return mList;
	}
	
	public void setList(List<LrcSentence> list) {
		mList = list;
		if (list != null && list.size() > 0) {
			int size = list.size();
			mTotalTime = list.get(size - 1).getTime() - list.get(0).getTime() + 1000;
			mDy = (size * mPaint.getTextSize() * 2) / (mTotalTime / mTick);
			mOffsetY = 0;
		}
	}
	
	public void updateUI(){
		new Thread(new updateThread()).start();
	}
	class updateThread implements Runnable {
		public void run() {
			while (true) {
				postInvalidate();
				try {
					Thread.sleep(mTick);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
		}
	}
}
