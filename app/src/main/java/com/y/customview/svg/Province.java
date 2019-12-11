package com.y.customview.svg;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

public class Province {

    public String mName;
    public Path mPath;
    public int mColor;

    public Province(String name,Path path,int color){
        this.mName = name;
        this.mPath = path;
        this.mColor = color;
    }

    public boolean isTouch(float x,float y){
        //创建一个矩形
        RectF rectF = new RectF();
        //获取到当前省份的矩形边界
        mPath.computeBounds(rectF, true);
        //创建一个区域对象
        Region region = new Region();
        //将path对象放入到Region区域对象中
        region.setPath(mPath, new Region((int)rectF.left, (int)rectF.top,(int)rectF.right, (int)rectF.bottom));
        //返回是否这个区域包含传进来的坐标
        return region.contains((int)x,(int)y);
    }
}
