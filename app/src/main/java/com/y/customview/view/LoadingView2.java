package com.y.customview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class LoadingView2 extends View {
    public LoadingView2(Context context) {
        super(context);
        init();
    }

    public LoadingView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;
    private Path mPath;
    private Path mPathDst;
    private PathMeasure mPm;

    private float progress;

    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        mPath = new Path();
        mPath.addCircle(0,0,300, Path.Direction.CCW);
        mPathDst = new Path();
        mPm = new PathMeasure();
        mPm.setPath(mPath,false);

        ValueAnimator animator = ValueAnimator.ofFloat(0,1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2,getHeight() / 2);
        mPathDst.reset();
        /*
         * 由于硬件加速的问题，PathMeasure中的getSegment添加到dst数组中时会被导致一些错误，
         * 需要通过mDst.lineTo(0,0)来避免这样一个Bug。
         */
        mPathDst.lineTo(0,0);

        float len = mPm.getLength();
        float start = 0;
        if(progress > 0.5){
            start = (float) (len * 2 * (progress - 0.5));
        }
        mPm.getSegment(start,len * progress,mPathDst,true);

        canvas.drawPath(mPathDst,mPaint);
    }
}
