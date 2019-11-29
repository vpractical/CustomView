package com.y.customview.anim;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RevealDrawable extends Drawable {
    private Drawable d1,d2;
    private Rect mRect = new Rect();
    private int mOrientation;

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;


    public RevealDrawable(Drawable d1,Drawable d2,int orientation){
        this.d1 = d1;
        this.d2 = d2;
        this.mOrientation = orientation;
    }

    @Override
    public int getIntrinsicWidth() {
        return Math.max(d1.getIntrinsicWidth(),d2.getIntrinsicWidth());
    }

    @Override
    public int getIntrinsicHeight() {
        return Math.max(d1.getIntrinsicHeight(),d2.getIntrinsicHeight());
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        d1.setBounds(bounds);
        d2.setBounds(bounds);
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return super.onLevelChange(level);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        int w = bounds.width();
        int h = bounds.height();

        if(mOrientation == HORIZONTAL){
            w /= 2;
        }else{
            h /= 2;
        }

        Gravity.apply(
                //从左边扣还是从右边扣
                Gravity.LEFT,
                //目标矩形宽
                w,
                //目标矩形高
                h,
                //被扣对象
                bounds,
                //承载对象
                mRect
        );

        canvas.save();
        canvas.clipRect(mRect);
        d1.draw(canvas);
        canvas.restore();

        Gravity.apply(
                //从左边扣还是从右边扣
                Gravity.RIGHT,
                //目标矩形宽
                w,
                //目标矩形高
                h,
                //被扣对象
                bounds,
                //承载对象
                mRect
        );
        canvas.save();
        canvas.clipRect(mRect);
        d2.draw(canvas);
        canvas.restore();

    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
