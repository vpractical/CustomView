package com.y.customview.frame;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.y.customview.R;

public class FLinearLayout extends LinearLayout {
    public FLinearLayout(Context context) {
        super(context);
    }

    public FLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FLayoutParams(getContext(),attrs);
    }

    /**
     * 如果child有自定义属性，给child包裹一个FFrameLayout
     */
    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        FLayoutParams p = (FLayoutParams) params;
        if(p.hasAnimAttr()){
            FFrameLayout ff = new FFrameLayout(getContext());
            ff.setAlpha(p.mAlpha);
            ff.setTranslation(p.mTranslation);
            ff.addView(child);
            super.addView(ff,params);
        }else{
            super.addView(child, params);
        }
    }

    /**
     * 包含自定义属性的LayoutParams,generateLayoutParams(attrs）时生成FLayoutParams
     * LayoutInflater部分源码：
     * final View view = createViewFromTag(parent, name, context, attrs);
     * final ViewGroup viewGroup = (ViewGroup) parent;
     * final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
     * rInflateChildren(parser, view, attrs, true);
     * viewGroup.addView(view, params);
     */
    private class FLayoutParams extends LayoutParams{
        private boolean mAlpha;//child透明动画
        private int mTranslation;//child平移动画

        public FLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.ScrollAnimParams);
            mAlpha = ta.getBoolean(R.styleable.ScrollAnimParams_anim_alpha,false);
            mTranslation = ta.getInt(R.styleable.ScrollAnimParams_anim_translation,-1);
            ta.recycle();
        }

        /**
         * child是否有自定义动画属性
         */
        public boolean hasAnimAttr(){
            return mAlpha || mTranslation >= 0;
        }
    }
}
