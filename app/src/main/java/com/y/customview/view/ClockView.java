package com.y.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 未设置生命周期
 */
public class ClockView extends View {

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;
    private int mLength;
    private int hour, minute, second;
    private Calendar calendar;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        minute = calendar.get(Calendar.MINUTE);
                        second = calendar.get(Calendar.SECOND);
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mLength = width > height ? height : width;
        mLength -= 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLength <= 0) return;
        canvas.translate(50, 50);

        mPaint.setStrokeWidth(8);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        //花时钟边框
        canvas.drawCircle(mLength / 2, mLength / 2, mLength / 2, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 12; i++) {
            boolean just = i % 3 == 0;
            mPaint.setStrokeWidth(just ? 8 : 5);
            mPaint.setTextSize(just ? 100 : 60);
            String text = i == 0 ? "12" : String.valueOf(i);
            float textX = mLength / 2f - mPaint.measureText(text) / 2;
            float textY = just ? 140 : 100;
            //绘制小时数字
            canvas.drawText(text, textX, textY, mPaint);
            //绘制指示每个小时的线条
            canvas.drawLine(mLength / 2, 0, mLength / 2, just ? 60 : 40, mPaint);
            //绘制指示每个小时区间的6个线条
            mPaint.setStrokeWidth(5);
            for (int j = 0; j < 5; j++) {
                if (j != 0) {
                    boolean mid = j == 3;
                    canvas.drawLine(mLength / 2, 0, mLength / 2, mid ? 30 : 20, mPaint);
                }
                canvas.rotate(6, mLength / 2, mLength / 2);
            }
        }
        //1s，1m都是6°，1h是360/12=30°
        float degreeS = second * 6;
        float degreeM = minute * 6 + 6 * second / 60f;
        float degreeH = hour % 12 * 30 + 30 * minute / 60f;

        float lenH = mLength / 4f;
        float lenM = mLength / 3f;
        float lenS = mLength / 2f - 60;
        //绘制时针分针秒针
        canvas.save();
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.BLUE);
        canvas.rotate(degreeH,mLength / 2,mLength / 2);
        canvas.drawLine(mLength / 2, mLength / 2 + 40, mLength / 2, mLength / 2f - lenH, mPaint);
        canvas.restore();
        canvas.save();
        mPaint.setStrokeWidth(8);
        mPaint.setColor(Color.GREEN);
        canvas.rotate(degreeM,mLength / 2,mLength / 2);
        canvas.drawLine(mLength / 2, mLength / 2 + 50, mLength / 2, mLength / 2f - lenM, mPaint);
        canvas.restore();
        canvas.save();
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.RED);
        canvas.rotate(degreeS,mLength / 2,mLength / 2);
        canvas.drawLine(mLength / 2, mLength / 2 + 60, mLength / 2, mLength / 2f - lenS, mPaint);
        canvas.restore();

        String time = hour + ":" + minute + ":" + second;
        mPaint.setTextSize(200);
        float tl = mPaint.measureText(time);
        canvas.drawText(time,mLength / 2f - tl / 2,mLength + 300,mPaint);
    }
}
