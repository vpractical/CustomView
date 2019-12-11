package com.y.customview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.view.MotionEvent;
import android.view.View;

import com.y.customview.R;

public class ScratchcardView extends View {
    public ScratchcardView(Context context) {
        super(context);
        init();
    }

    private Paint mPaint;
    private int mWidth, mHeight;
    private Bitmap mBmpBottom, mDstBmp, mSrcBmp;
    private Path mPath;
    private Matrix mMatrix = new Matrix();
    private float lastX, lastY;
    private Xfermode xfermode;

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(60);
        mPath = new Path();
        mBmpBottom = BitmapFactory.decodeResource(getResources(), R.drawable.guaguaka_text1);
        mDstBmp = BitmapFactory.decodeResource(getResources(), R.drawable.guaguaka);
        mSrcBmp = Bitmap.createBitmap(mDstBmp.getWidth(),mDstBmp.getHeight(), Bitmap.Config.ARGB_8888);

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        float scale = mSrcBmp.getWidth() / mWidth;
        mMatrix.setScale(scale, scale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth <= 0) return;
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mBmpBottom, mMatrix, mPaint);

        //刮奖效果使用xfermode实现，但是xfermode是将画笔setXfermode后的图和canvas(图层一)对象合成，
        //所以未加saveLayer时界面显示全黑。saveLayer新建图层二后，再绘制dst和src图，达到效果
        canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mDstBmp,mMatrix,mPaint);

        Canvas c = new Canvas(mSrcBmp);
        c.drawPath(mPath,mPaint);
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(mSrcBmp,mMatrix,mPaint);
        mPaint.setXfermode(null);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                mPath.moveTo(lastX, lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                float curX = event.getX();
                float curY = event.getY();
                float endX = (lastX + curX) / 2;
                float endY = (lastY + curY) / 2;
                mPath.quadTo(lastX, lastY, endX, endY);
                lastX = curX;
                lastY = curY;
                break;
            default:
                break;
        }
        postInvalidate();
        return true;
    }
}
