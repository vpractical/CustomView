package com.y.customview.anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * 图层api测试
 * 功能和save类似，会新生成一个offscreen bitmap，
 * 用过ps类似工具的完全可以把它理解为一个图层。因为新生成了一个bitmap所以就更耗费内存了，
 * 当心oom.后面是一些建议的用法。里面提到restore()。就是把当前图层所绘制的内容绘制到前一个图层。
 * 类比就是PS里面的合并图层了
 */
public class LayerView extends View {
    private int mWidth,mHeight;
    private Paint mPaint;

    public LayerView(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //同一图层的绘制效果
        mPaint.setColor(Color.RED);
        canvas.drawCircle(100,100,100,mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(150,150,100,mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(200,200,100,mPaint);

        canvas.translate(0,400);
        //图层1
        mPaint.setColor(Color.RED);
        canvas.drawCircle(100,100,100,mPaint);
        RectF rectF = new RectF(0,0,mWidth,400);
        canvas.saveLayer(rectF,mPaint);
        //图层2
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(150,150,100,mPaint);
        canvas.restore();

        canvas.translate(0,400);
        //图层1
        mPaint.setColor(Color.RED);
        canvas.drawCircle(100,100,100,mPaint);
        canvas.saveLayer(rectF,mPaint);
        //图层2
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(150,150,100,mPaint);
        canvas.restore();
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(200,200,100,mPaint);

        canvas.translate(0,400);
        //图层1
        mPaint.setColor(Color.RED);
        canvas.drawCircle(100,100,100,mPaint);
        canvas.saveLayerAlpha(rectF,120);
        //图层2
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(150,150,100,mPaint);
        canvas.restore();
    }
}
