package com.y.customview.anim;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.y.customview.L;
import com.y.customview.R;

public class LoadingView1 extends View implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        L.e("LoadingView1::onResume");
        isAlive = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        L.e("LoadingView1::onPause");
        isAlive = false;
    }

    public LoadingView1(Context context) {
        super(context);
        init();
    }

    public LoadingView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;
    private int w, h;
    private Bitmap mArrowBmp;
    private Matrix matrix;


    private Path mPath;
    private PathMeasure pm;
    private float[] pos = new float[2];
    private float[] tan = new float[2];

    private float progress;
    private boolean isAlive;

    private void init() {
        mPaint = new Paint();
        mPaint.setFilterBitmap(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.arrow, options);
        options.inSampleSize = options.outWidth / 30;
        options.inJustDecodeBounds = false;
        mArrowBmp = BitmapFactory.decodeResource(getResources(), R.drawable.arrow, options);
        matrix = new Matrix();
        mPath = new Path();
        pm = new PathMeasure();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlive = !isAlive;
                if(isAlive){
                    invalidate();
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(w / 2, h / 2);
        mPath.reset();

        mPath.addCircle(0, 0, 300, Path.Direction.CW);

        pm.setPath(mPath, false);
        float dist = pm.getLength() * progress;

        //让箭头顺着圆的边前进
        //方案1，通过PathMeasure获取矩阵
        //PathMeasure.POSITION_MATRIX_FLAG：矩阵包含各个点位置信息
        //PathMeasure.TANGENT_MATRIX_FLAG：矩阵包含各个点切线角度信息
//        pm.getMatrix(dist,matrix,PathMeasure.POSITION_MATRIX_FLAG|PathMeasure.TANGENT_MATRIX_FLAG);
//        matrix.preTranslate(-mArrowBmp.getWidth() / 2,-mArrowBmp.getHeight() / 2);

        //方案2，通过位置和正切算
        pm.getPosTan(dist, pos, tan);
        float degree = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
        matrix.reset();
        matrix.postRotate(degree,mArrowBmp.getWidth() / 2,mArrowBmp.getHeight() / 2);
        matrix.postTranslate(pos[0]- (mArrowBmp.getWidth() >> 1),pos[1]- (mArrowBmp.getHeight() >> 1));

        canvas.drawPath(mPath, mPaint);
        canvas.drawBitmap(mArrowBmp, matrix, mPaint);

        if (isAlive) {
            progress += 0.005;
            if(progress >= 1){
                progress = 0;
            }
            invalidate();
        }
    }
}
