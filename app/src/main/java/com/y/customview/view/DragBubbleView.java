package com.y.customview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.y.customview.R;

public class DragBubbleView extends View {
    public DragBubbleView(Context context) {
        this(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, 0);
        init();
    }

    private TypedArray mTypedArray;

    /**
     * ['sepəreɪt']
     * adj.单独的;独立的;分开的;不同的;不相关的
     * v.(使)分开，分离;分割;划分;(使)分离，分散;隔开;阻隔
     * [ˈvænɪʃ']
     * v.(莫名其妙地)突然消失;不复存在;消亡;绝迹
     */
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_CONNET = 1;
    private static final int STATE_SEPARATE = 2;
    private static final int STATE_VANISH = 3;
    private int mState;


    private Paint mPaintBubble;
    private Paint mPaintText;
    private Paint mPaintVanish;

    private String mBubbleText;
    private int mBubbleTextColor;
    private float mBubbleTextSize;
    //文字绘制位置
    private Rect mTextRect;
    //消失图形绘制位置
    private Rect mVanishRect;

    private int mBubbleColor;
    private float mBubbleRadius;
    //分开后，占位气泡的半径
    private float mBubbleHolderRadius;
    //分开后，距离越大占位气泡越小，最小半径
    private float mBubbleHolderMinRadius;
    //连接状态下绘制路径
    private Path mConnPath;

    //两气泡圆心距离
    private float mDist;
    //两气泡最大圆心距离
    private float mMaxDist;

    //气泡和分离后原地气泡的圆心位置
    private PointF mLeavedCenter;
    private PointF mHolderCenter;

    //气泡消失的图片id数组
    private int[] mVanishArray = {R.drawable.burst_1, R.drawable.burst_2, R.drawable.burst_3, R.drawable.burst_4, R.drawable.burst_5};
    private Bitmap[] mVanishBitmaps = new Bitmap[mVanishArray.length];
    //气泡消失时图片显示索引
    private int mVanishIndex;

    private void init() {
        mBubbleColor = mTypedArray.getColor(R.styleable.DragBubbleView_bubble_color, Color.RED);
        mBubbleRadius = mTypedArray.getDimension(R.styleable.DragBubbleView_bubble_radius, 45f);
        mBubbleText = mTypedArray.getString(R.styleable.DragBubbleView_bubble_text);
        mBubbleTextColor = mTypedArray.getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE);
        mBubbleTextSize = mTypedArray.getDimension(R.styleable.DragBubbleView_bubble_textSize, 40f);

        mBubbleHolderRadius = mBubbleRadius;
        mBubbleHolderMinRadius = mBubbleRadius / 5f;
        mMaxDist = mBubbleRadius * 8;

        mLeavedCenter = new PointF();
        mHolderCenter = new PointF();

        //气泡画笔
        mPaintBubble = new Paint();
        mPaintBubble.setColor(mBubbleColor);
        mPaintBubble.setStyle(Paint.Style.FILL);
        mConnPath = new Path();

        //文字
        mPaintText = new Paint();
        mPaintText.setTextSize(mBubbleTextSize);
        mPaintText.setColor(mBubbleTextColor);
        mPaintText.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextRect = new Rect();

        //消失画笔
        mPaintVanish = new Paint();
        mPaintVanish.setFlags(Paint.ANTI_ALIAS_FLAG);
        //消除位图锯齿
        mPaintVanish.setFilterBitmap(true);
        mVanishRect = new Rect();
        for (int i = 0; i < mVanishArray.length; i++) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), mVanishArray[i]);
            mVanishBitmaps[i] = bmp;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBubbleText == null) {
            return;
        }

        if (mState == STATE_DEFAULT) {
            drawBubble(canvas);
        }

        if (mState == STATE_CONNET) {
            drawConnect(canvas);
            drawBubble(canvas);
        }

        if (mState == STATE_SEPARATE) {
            drawBubble(canvas);
        }

        if (mState == STATE_VANISH) {
            drawVanish(canvas);
        }

    }

    /**
     * 绘制气泡
     */
    private void drawBubble(Canvas canvas) {
        canvas.drawCircle(mLeavedCenter.x, mLeavedCenter.y, mBubbleRadius, mPaintBubble);
        //获取文字的大小，放入Rect中绘制要用
        mPaintText.getTextBounds(mBubbleText, 0, mBubbleText.length(), mTextRect);
        //文字y轴向上偏移一定值以居中
        canvas.drawText(mBubbleText, mLeavedCenter.x - mTextRect.width() / 2f,
                mLeavedCenter.y + mTextRect.height() / 2f - mTextRect.height() / 8f, mPaintText);
    }

    /**
     * 绘制连接状态
     * ·A
     * ·圆心  占位圆横切线
     * ·B   ·
     * ·
     * ·
     * ·
     * ·           ·D
     * ·     ·
     * ·  连接状态下移动的气泡 移动圆的横切线
     * ·
     * ·C
     * 连接时绘制原点圆，移动圆，还绘制路径
     * 路径从A->B->C->D->A,中间填充气泡颜色，并且A->B,C->D是切线路径，B->C,D->A是贝塞尔路径
     */
    private void drawConnect(Canvas canvas) {
        canvas.drawCircle(mHolderCenter.x, mHolderCenter.y, mBubbleHolderRadius, mPaintBubble);

        float anchorX = (mHolderCenter.x + mLeavedCenter.x) / 2;
        float anchorY = (mHolderCenter.y + mLeavedCenter.y) / 2;

        float sin = (mHolderCenter.y - mLeavedCenter.y) / mDist;
        float cos = (mHolderCenter.x - mLeavedCenter.x) / mDist;

        float ax = mHolderCenter.x + mBubbleHolderRadius * sin;
        float ay = mHolderCenter.y - mBubbleHolderRadius * cos;
        float bx = mHolderCenter.x - mBubbleHolderRadius * sin;
        float by = mHolderCenter.y + mBubbleHolderRadius * cos;
        float cx = mLeavedCenter.x - mBubbleRadius * sin;
        float cy = mLeavedCenter.y + mBubbleRadius * cos;
        float dx = mLeavedCenter.x + mBubbleRadius * sin;
        float dy = mLeavedCenter.y - mBubbleRadius * cos;

        mConnPath.reset();
        mConnPath.moveTo(ax, ay);
        mConnPath.lineTo(bx, by);
        mConnPath.quadTo(anchorX, anchorY, cx, cy);
        mConnPath.lineTo(dx, dy);
        mConnPath.quadTo(anchorX, anchorY, ax, ay);

        canvas.drawPath(mConnPath, mPaintBubble);
    }

    /**
     * 绘制分离放开后的消失状态
     * 消失画面共5页，动画会刷新6次，最后一次不绘制，让动画结束后消失
     */
    private void drawVanish(Canvas canvas) {
        if (mVanishIndex >= mVanishBitmaps.length) {
            return;
        }
        mVanishRect.left = (int) (mLeavedCenter.x - mBubbleRadius / 2);
        mVanishRect.top = (int) (mLeavedCenter.y - mBubbleRadius / 2);
        mVanishRect.right = (int) (mLeavedCenter.x + mBubbleRadius / 2);
        mVanishRect.bottom = (int) (mLeavedCenter.y + mBubbleRadius / 2);
        canvas.drawBitmap(mVanishBitmaps[mVanishIndex], null, mVanishRect, mPaintVanish);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() / 2f != mHolderCenter.x || getMeasuredHeight() / 2f != mHolderCenter.y) {
            reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mState == STATE_CONNET || mState == STATE_SEPARATE) {
            getParent().requestDisallowInterceptTouchEvent(true);
//            ((View)getParent()).bringToFront();
        }
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mState == STATE_DEFAULT) {
                    mState = STATE_CONNET;
                } else if (mState == STATE_SEPARATE) {
                    mDist = (float) Math.hypot(event.getX() - mHolderCenter.x, event.getY() - mHolderCenter.y);
                    if (nearHolder(mDist)) {
                        mState = STATE_CONNET;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mState == STATE_CONNET || mState == STATE_SEPARATE) {
                    mLeavedCenter.set(event.getX(), event.getY());
                    if (mState == STATE_CONNET) {
                        mDist = (float) Math.hypot(event.getX() - mHolderCenter.x, event.getY() - mHolderCenter.y);
                        if (mDist > mMaxDist) {
                            mState = STATE_SEPARATE;
                        } else {
                            //连接状态时，原点占位气泡半径随气泡距离变化,在最大距离占位圆有个最小半径
                            mBubbleHolderRadius = (mBubbleRadius - mBubbleHolderMinRadius) * (1 - mDist / mMaxDist) + mBubbleHolderMinRadius;
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mState == STATE_CONNET) {
                    recover();
                } else if (mState == STATE_SEPARATE) {
                    mDist = (float) Math.hypot(event.getX() - mHolderCenter.x, event.getY() - mHolderCenter.y);
                    if (nearHolder(mDist)) {
                        recover();
                    } else {
                        vanish();
                    }
                }

                break;
        }

        return true;
    }

    /**
     * 松开后气泡恢复原状
     * AccelerateDecelerateInterolator：先加速后减速
     * AccelerateInterpolator：加速
     * DecelerateInterpolator：减速
     * AnticipateInterpolator：先向相反方向改变一段再加速播放
     * AnticipateOvershootInterpolator：先向相反方向改变，再加速播放，会超出目标值然后缓慢移动至目标值，类似于弹簧回弹
     * BounceInterpolator：快到目标值时值会跳跃
     * CycleIinterpolator：动画循环一定次数，值的改变为一正弦函数：Math.sin(2 * mCycles * Math.PI
     * input)
     * LinearInterpolator：线性均匀改变
     * OvershottInterpolator：最后超出目标值然后缓慢改变到目标值
     * TimeInterpolator：一个允许自定义Interpolator的接口，以上都实现了该接口
     */
    private void recover() {
        ValueAnimator animator = ValueAnimator.ofObject(new PointFEvaluator(),
                new PointF(mLeavedCenter.x, mLeavedCenter.y),
                new PointF(mHolderCenter.x, mHolderCenter.y));
//                mLeavedCenter,mHolderCenter);
        animator.setDuration(200);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mLeavedCenter = (PointF) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mState = STATE_DEFAULT;
            }
        });
        animator.start();
    }

    /**
     * 松开后消失
     */
    private void vanish() {
        mState = STATE_VANISH;
        mVanishIndex = -1;
        ValueAnimator animator = ValueAnimator.ofInt(0, mVanishArray.length);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                if (val != mVanishIndex) {
                    mVanishIndex = val;
                    invalidate();
                }
            }
        });
        animator.start();
    }

    /**
     * 气泡拖到最大距离进入STATE_SEPARATE分离状态时，如果又拖动到原点附近，则松开后进入STATE_CONNECT连接状态
     */
    private boolean nearHolder(float dist) {
        return dist < mBubbleRadius * 3;
    }

    private void reset() {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        mLeavedCenter.set(w / 2, h / 2);
        mHolderCenter.set(w / 2, h / 2);
        mState = STATE_DEFAULT;
    }

    /**
     * 设置未读数
     */
    public void setBubbleText(String BubbleText) {
        mBubbleText = BubbleText;
        reset();
        invalidate();
    }


}
