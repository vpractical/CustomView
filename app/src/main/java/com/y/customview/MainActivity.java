package com.y.customview;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.y.customview.view.ClockView;
import com.y.customview.view.LayerView;
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

        views.add(new ClockView(this));
        views.add(new ScratchcardView(this));
        views.add(new LayerView(this));

        adapter.notifyDataSetChanged();
    }


}
