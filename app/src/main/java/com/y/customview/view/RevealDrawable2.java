package com.y.customview.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RevealDrawable2 extends Drawable {
    private Drawable d1,d2;
    private Rect mRect = new Rect();
    private int mOrientation;

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;


    public RevealDrawable2(Drawable d1, Drawable d2, int orientation){
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

        int level = getLevel();
        int gravity = level < 5000 ? Gravity.LEFT : Gravity.RIGHT;
        float ratio = Math.abs(level / 5000f - 1);

        Rect bounds = getBounds();
        int w = bounds.width();
        int h = bounds.height();

        if(mOrientation == HORIZONTAL){
            w *= ratio;
        }else if(mOrientation == VERTICAL){
            h *= ratio;
        }

        Gravity.apply(
                //从左边扣还是从右边扣
                gravity,
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

        w = bounds.width();
        h = bounds.height();
        gravity = level > 5000 ? Gravity.LEFT : Gravity.RIGHT;

        if(mOrientation == HORIZONTAL){
            w -= w * ratio;
        }else if(mOrientation == VERTICAL){
            h -= h * ratio;
        }

        Gravity.apply(
                //从左边扣还是从右边扣
                gravity,
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
