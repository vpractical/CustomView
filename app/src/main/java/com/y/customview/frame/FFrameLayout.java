package com.y.customview.frame;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FFrameLayout extends FrameLayout implements FScrollListener{
    public FFrameLayout(@NonNull Context context) {
        super(context);
    }

    public FFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static final int TRANSLATION_FROM_LEFT = 0x01;
    private static final int TRANSLATION_FROM_TOP = 0x02;
    private static final int TRANSLATION_FROM_RIGHT = 0x04;
    private static final int TRANSLATION_FROM_BOTTOM = 0x08;

    private boolean mAlpha;
    private int mTranslation;
    private int w,h;

    public void setAlpha(boolean alpha){
        this.mAlpha = alpha;
    }

    public void setTranslation(int translation){
        this.mTranslation = translation;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        onAnimReset();
    }

    /**
     * 属性值是否包含这个方向
     */
    private boolean isTransFrom(int from){
        return (from & mTranslation) == from;
    }

    @Override
    public void onScrollAnim(float progress) {
        if(mAlpha){
            setAlpha(1 - progress);
        }
        if(mTranslation >= 0){
            if(isTransFrom(TRANSLATION_FROM_LEFT)){
                setTranslationX(-progress * w);
            }
            if(isTransFrom(TRANSLATION_FROM_TOP)){
                setTranslationY(-progress * h);
            }
            if(isTransFrom(TRANSLATION_FROM_RIGHT)){
                setTranslationX(progress * w);
            }
            if(isTransFrom(TRANSLATION_FROM_BOTTOM)){
                setTranslationY(progress * h);
            }
        }
    }

    @Override
    public void onAnimReset() {
        onScrollAnim(0);
    }
}
