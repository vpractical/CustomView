package com.y.customview.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.y.customview.R;

public class GalleryHorizontalScrollView extends HorizontalScrollView{
    private LinearLayout container;
    private int w;//单张图的宽度

    public GalleryHorizontalScrollView(Context context) {
        super(context);
        init();
    }

    public GalleryHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        container = new LinearLayout(getContext());
        container.setLayoutParams(lp);
        addView(container);
    }


    /**
     * 渐变图片
     */
    private void reveal() {
        int scrollX = getScrollX();
        //处在中间位置的两张图片
        int indexLeft = scrollX / w;
        int indexRight = indexLeft + 1;
        //偏移了多少
        float trans = scrollX % w;
        int levelLeft = (int) (5000 - Math.abs(trans / w * 5000));
        int levelRight = levelLeft + 5000;

        Log.e("---------------","left = " + levelLeft + "   right = " + levelRight);

        int count = container.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView iv = (ImageView) container.getChildAt(i);
            if(i == indexLeft){
                iv.setImageLevel(levelLeft);
            }else if(i == indexRight){
                iv.setImageLevel(levelRight);
            }else{
                iv.setImageLevel(0);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View first = container.getChildAt(0);
        w = first.getWidth();
        int padding = getWidth() / 2 - w / 2;
        container.setPadding(padding,0,padding,0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        reveal();
    }










    public void addViews(){
        container.removeAllViews();
        for (int i = 0; i < mImgIds.length; i++) {
            container.addView(getRevealView(i));
        }

        ImageView childAt = (ImageView) container.getChildAt(0);
        childAt.setImageLevel(5000);
    }

    private View getRevealView(int i) {
        ImageView iv = new ImageView(getContext());
        Drawable d1 = getResources().getDrawable(mImgIds[i]);
        Drawable d2 = getResources().getDrawable(mImgIdsActive[i]);
        RevealDrawable2 rd = new RevealDrawable2(d1,d2,RevealDrawable2.HORIZONTAL);
        iv.setImageDrawable(rd);
        return iv;
    }

    private int[] mImgIds = new int[]{ //7个
            R.drawable.avft,
            R.drawable.box_stack,
            R.drawable.bubble_frame,
            R.drawable.bubbles,
            R.drawable.bullseye,
            R.drawable.circle_filled,
            R.drawable.circle_outline,

            R.drawable.avft,
            R.drawable.box_stack,
            R.drawable.bubble_frame,
            R.drawable.bubbles,
            R.drawable.bullseye,
            R.drawable.circle_filled,
            R.drawable.circle_outline
    };
    private int[] mImgIdsActive = new int[]{
            R.drawable.avft_active, R.drawable.box_stack_active, R.drawable.bubble_frame_active,
            R.drawable.bubbles_active, R.drawable.bullseye_active, R.drawable.circle_filled_active,
            R.drawable.circle_outline_active,
            R.drawable.avft_active, R.drawable.box_stack_active, R.drawable.bubble_frame_active,
            R.drawable.bubbles_active, R.drawable.bullseye_active, R.drawable.circle_filled_active,
            R.drawable.circle_outline_active
    };

}
