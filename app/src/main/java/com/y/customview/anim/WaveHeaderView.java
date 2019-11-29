package com.y.customview.anim;

import android.animation.ValueAnimator;
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

import com.y.customview.R;

public class WaveHeaderView extends View {
    public WaveHeaderView(Context context) {
        super(context);
        init();
    }

    public WaveHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;
    private Path mPath;
    private Path mPathWave;
    private PathMeasure mPm;
    private Bitmap mShip;
    private int width, height;
    private float progress;
    private Matrix matrix;
    /**
     * 波浪的总长度
     */
    private float mLength;

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL);

        matrix = new Matrix();

        mPath = new Path();
        mPathWave = new Path();
        mPm = new PathMeasure();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        mShip = BitmapFactory.decodeResource(getResources(), R.drawable.timg, options);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private void initPath() {
        mPathWave.reset();
        //奇数的话，右侧能超出屏幕一截，船能开出屏幕
        int num = 13;
        float w = width / num;
        int k = 1;
        mPathWave.moveTo(-w * 2, height);
        mPathWave.quadTo(-w,height + 40 * k, 0, height);
        for (int i = 1; i <= num; i++) {
            k *= -1;
            mPathWave.quadTo(w * i, height + 40 * k, w * ++i, height);
        }
        mPm.setPath(mPathWave, false);
        mLength = mPm.getLength();

        mPath.reset();
        mPath.moveTo(width, height);
        mPath.lineTo(width, 0);
        mPath.lineTo(0, 0);
        mPath.lineTo(0, height);
        mPath.addPath(mPathWave);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h / 3;
        initPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);

        float dist = mLength * progress;
        mPm.getMatrix(dist, matrix, PathMeasure.TANGENT_MATRIX_FLAG | PathMeasure.POSITION_MATRIX_FLAG);
        matrix.preTranslate(-mShip.getWidth() / 2, -mShip.getHeight());
        canvas.drawBitmap(mShip, matrix, mPaint);
    }
}
