package com.y.customview;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.y.customview.svg.ChinaView;
import com.y.customview.view.ClockView;
import com.y.customview.view.DragBubbleView;
import com.y.customview.view.GalleryHorizontalScrollView;
import com.y.customview.view.LayerView;
import com.y.customview.view.LoadingView1;
import com.y.customview.view.LoadingView2;
import com.y.customview.view.RevealDrawable;
import com.y.customview.view.ScratchcardView;
import com.y.customview.view.WaveHeaderView;

import java.util.ArrayList;
import java.util.List;

/**
 * 从5.0 开始，在同一个layout下，Button将总是位于最上层，就算在Button上覆盖了相应的View
 * 使用android:stateListAnimator=”@null”去掉阴影效果而使Button可以被正常的覆盖
 * 属性使控件在点击时产生不同的交互，对于Button，点击时默认有个阴影的效果用于表示按下的状态
 */
public class MainActivity extends AppCompatActivity {

    ViewPager vp;
    MainAdapter adapter;
    List<View> views = new ArrayList<>();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = findViewById(R.id.vp);
        vp.setAdapter(adapter = new MainAdapter(views));

        views.add(new ChinaView(this));
        views.add(getFrameView());
        views.add(getSplashView());
        views.add(new WaveHeaderView(this));
        views.add(new LoadingView2(this));
        views.add(getLoadingView1());
        views.add(getDragDelView());
        views.add(getScrollRevealView());
        views.add(getRevealView());
        views.add(new ClockView(this));
        views.add(new ScratchcardView(this));
        views.add(new LayerView(this));

        adapter.notifyDataSetChanged();
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(views.get(position).getClass().getSimpleName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setTitle(views.get(0).getClass().getSimpleName());
    }

    private View getFrameView() {
        return LayoutInflater.from(this).inflate(R.layout.view_frame, null);
    }

    private View getSplashView() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_splashview, null);
        return view;
    }

    private View getLoadingView1() {
        LoadingView1 loadingView1 = new LoadingView1(this);
        getLifecycle().addObserver(loadingView1);
        return loadingView1;
    }

    private View getDragDelView() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_dragdelview, null);
        final DragBubbleView ddView1 = view.findViewById(R.id.dragDelView1);
        final DragBubbleView ddView2 = view.findViewById(R.id.dragDelView2);
        view.findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ddView1.setBubbleText("3");
                ddView2.setBubbleText("10");
            }
        });
        ddView1.setBubbleText("2");
        return view;
    }

    private View getScrollRevealView() {
        GalleryHorizontalScrollView sv = new GalleryHorizontalScrollView(this);
        sv.addViews();
        return sv;
    }

    private View getRevealView() {
        ImageView iv = new ImageView(this);
        Drawable d1 = getResources().getDrawable(R.drawable.avft);
        Drawable d2 = getResources().getDrawable(R.drawable.avft_active);
        RevealDrawable rd = new RevealDrawable(d1, d2, RevealDrawable.HORIZONTAL);
        iv.setImageDrawable(rd);
        return iv;
    }


}
