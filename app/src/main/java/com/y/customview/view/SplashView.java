package com.y.customview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.y.customview.R;

/*
        Path.Op.DIFFERENCE 减去path1中path1与path2都存在的部分;
        path1 = (path1 - path1 ∩ path2)
        Path.Op.INTERSECT 保留path1与path2共同的部分;
        path1 = path1 ∩ path2
        Path.Op.UNION 取path1与path2的并集;
        path1 = path1 ∪ path2
        Path.Op.REVERSE_DIFFERENCE 与DIFFERENCE刚好相反;
        path1 = path2 - (path1 ∩ path2)
        Path.Op.XOR 与INTERSECT刚好相反;
        path1 = (path1 ∪ path2) - (path1 ∩ path2)
 */
public class SplashView extends View {
    public SplashView(Context context) {
        super(context);
        init();
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //小球画笔
    private Paint mPaint;
    //展开背景时的画笔
    private Paint mPaintExpand;

    //小球颜色组
    private int[] rotateColors;
    //小球组成的大圆半径
    private float radius = 200;
    //小球的角度变化改变值
    private float radianOffset;
    private float w, h;
    //最大半径，即屏幕对角线一半
    private float maxRadius;
    private Path pathDst, pathSrc;

    private void init() {
        mPaint = new Paint();
        mPaintExpand = new Paint();
        mPaintExpand.setStyle(Paint.Style.FILL);
        mPaintExpand.setColor(Color.WHITE);

        pathDst = new Path();
        pathSrc = new Path();

        rotateColors = getResources().getIntArray(R.array.splash_rotate_colors);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == null) {
                    initAnim();
                    return;
                }

                if (mState.animator.isPaused()) {
                    mState.animator.resume();
                } else {
                    mState.animator.pause();
                }
            }
        });

        initAnim();
    }

    private void initAnim() {
        if(mState == null){
            radius = 200;
            mState = mRotateState = new RotateState();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mState = new MergingState();
                }
            }, 3000);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        maxRadius = (float) Math.sqrt((w * w + h * h)) / 2f;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mState == null) {
            return;
        }
        mState.draw(canvas);
    }

    private void drawCircle(Canvas canvas) {
        canvas.translate(w / 2, h / 2);
        canvas.drawColor(Color.WHITE);
        float per = (float) (2 * Math.PI / rotateColors.length);
        float x, y;
        for (int i = 0; i < rotateColors.length; i++) {
            mPaint.setColor(rotateColors[i]);
            x = (float) (radius * Math.cos(per * i + radianOffset));
            y = (float) (radius * Math.sin(per * i + radianOffset));
            canvas.drawCircle(x, y, 30, mPaint);
        }
    }

    private State mState;
    private RotateState mRotateState;

    /**
     * 旋转、分散、聚合状态基类
     */
    private abstract class State {
        public ValueAnimator animator;

        abstract void draw(Canvas canvas);

        void cancel() {
            animator.cancel();
        }
    }

    private class RotateState extends State {

        public RotateState() {
            animator = ValueAnimator.ofFloat(0, (float) (2 * Math.PI));
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setDuration(2500);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radianOffset = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.start();
        }

        @Override
        void draw(Canvas canvas) {
            drawCircle(canvas);
        }
    }

    private class MergingState extends State {
        public MergingState() {
            animator = ValueAnimator.ofFloat(radius, 2 * radius, 0);
            animator.setDuration(2500);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRotateState.cancel();
                    mState = new ExpandState();
                }
            });
            animator.start();
        }

        @Override
        void draw(Canvas canvas) {
            drawCircle(canvas);
        }
    }

    private class ExpandState extends State {

        public ExpandState() {
            animator = ValueAnimator.ofFloat(0, maxRadius);
            animator.setDuration(2500);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mState = null;
                }
            });
            animator.start();
        }

        @Override
        void draw(Canvas canvas) {
            pathDst.rewind();
            pathDst.moveTo(0, 0);
            pathDst.lineTo(w, 0);
            pathDst.lineTo(w, h);
            pathDst.lineTo(0, h);
            pathDst.lineTo(0,0);

            pathSrc.rewind();
            pathSrc.addCircle(w / 2, h / 2, radius, Path.Direction.CW);
            pathDst.op(pathSrc, Path.Op.DIFFERENCE);
            canvas.drawPath(pathDst, mPaintExpand);
        }
    }

}


