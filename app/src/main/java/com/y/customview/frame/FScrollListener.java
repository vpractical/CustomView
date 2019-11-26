package com.y.customview.frame;

public interface FScrollListener {

    /**
     *  随滚动时动画进度
     * @param progress 0 .. 1
     */
    void onScrollAnim(float progress);

    /**
     * 还原动画
     */
    void onAnimReset();
}
