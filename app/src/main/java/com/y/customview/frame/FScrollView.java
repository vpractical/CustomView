package com.y.customview.frame;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FScrollView extends ScrollView {
    public FScrollView(@NonNull Context context) {
        super(context);
    }

    public FScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private FLinearLayout mChild;
    private int mChildChildCount;
    private int h;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChild = (FLinearLayout) getChildAt(0);
        mChildChildCount = mChild.getChildCount();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.h = h;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        for (int i = 0; i < mChildChildCount; i++) {
            View childchild = mChild.getChildAt(i);
            if(!(childchild instanceof FScrollListener)){
                continue;
            }
            int measuredHeight = childchild.getMeasuredHeight();
            FScrollListener listener = (FScrollListener) childchild;
            int childchildTop = childchild.getTop() - t;

            if(childchildTop > h || childchildTop + measuredHeight <= h){
                listener.onAnimReset();
            }else{
                float progress = (h - childchildTop) * 1f / measuredHeight;
                progress = progress > 1 ? 1 : progress;
                progress = progress < 0 ? 0 : progress;
                listener.onScrollAnim(1 - progress);
            }
        }
    }
}
