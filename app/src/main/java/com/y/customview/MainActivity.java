package com.y.customview;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.y.customview.view.ClockView;
import com.y.customview.view.DragBubbleView;
import com.y.customview.view.GalleryHorizontalScrollView;
import com.y.customview.view.LayerView;
import com.y.customview.view.RevealDrawable;
import com.y.customview.view.ScratchcardView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager vp;
    MainAdapter adapter;
    List<View> views = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp = findViewById(R.id.vp);
        vp.setAdapter(adapter = new MainAdapter(views));

        views.add(getDragDelView());
        views.add(getScrollRevealView());
        views.add(getRevealView());
        views.add(new ClockView(this));
        views.add(new ScratchcardView(this));
        views.add(new LayerView(this));

        adapter.notifyDataSetChanged();
    }

    private View getDragDelView() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_dragdelview,null);
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
        RevealDrawable rd = new RevealDrawable(d1,d2,RevealDrawable.HORIZONTAL);
        iv.setImageDrawable(rd);
        return iv;
    }




}
