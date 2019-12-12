package com.y.customview.svg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.y.customview.L;
import com.y.customview.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ChinaView extends View {
    public ChinaView(Context context) {
        this(context,null);
    }

    public ChinaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public ChinaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private Context mContext;
    private List<Province> mProvinces = new ArrayList<>();
    //一共多少个省份
    private int mCount;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //地图原图所在矩阵
    private RectF mRectMap;
    //地图实际绘制缩放比
    private float mScale = 1f;
    //画布旋转角度
    private float mRotate;
    //绘制地图的颜色
    private int[] mColors= new int[]{0xFF339BD7, 0xFF50A9E5, 0xFF80CBF1, 0xFFAFFFFF};
    //解析svg的线程,就是解析xml
    private Thread mParseThread = new Thread(){
        @Override
        public void run() {
            InputStream is = mContext.getResources().openRawResource(R.raw.china);
            mProvinces.clear();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(is);
                //获取到xml文件的根目录
                Element rootElement = document.getDocumentElement();
                //获取根据节点下面的某些节点
                NodeList pathList = rootElement.getElementsByTagName("path");
                mCount = pathList.getLength();
                //整个地图的范围
                float left = -1;
                float right = -1;
                float top = -1;
                float bottom = -1;
                for (int i = 0; i < mCount; i++) {
                    //获取到每一个path节点
                    Element doc = (Element) pathList.item(i);
                    //获取到path节点中的android:pathData属性值
                    String pathData = doc.getAttribute("android:pathData");
                    //将path字符串转为path对象,拷的源码工具
                    Path path = PathParser.createPathFromPathData(pathData);
                    mProvinces.add(new Province("" + i,path,mColors[i % 4]));

                    //获取控件的宽高
                    RectF rect = new RectF();
                    //获取到每个省份的边界
                    path.computeBounds(rect,true);
                    //遍历取出每个path中的left取所有的最小值
                    left = left == -1 ? rect.left : Math.min(left, rect.left);
                    //遍历取出每个path中的right取所有的最大值
                    right = right == -1 ? rect.right : Math.max(right, rect.right);
                    //遍历取出每个path中的top取所有的最小值
                    top = top == -1 ? rect.top : Math.min(top, rect.top);
                    //遍历取出每个path中的bottom取所有的最大值
                    bottom = bottom == -1 ? rect.bottom : Math.max(bottom, rect.bottom);
                }
                mRectMap = new RectF(left,top,right,bottom);
                if(getMeasuredWidth() > 0){
                    calculateScale();
                }
                postInvalidate();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Province mSelected;

    private void init() {
        mParseThread.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mRectMap != null){
            calculateScale();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mRectMap == null){
            return;
        }
        canvas.rotate(mRotate);
        if(mRotate != 0){
            canvas.translate(0,-getWidth());
        }
        canvas.scale(mScale,mScale);
        for (Province p:mProvinces) {
            if(p != mSelected){
                //这是不选中的情况下   设置边界
                mPaint.setStrokeWidth(2);
                mPaint.setColor(Color.BLACK);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setShadowLayer(8,0,0,0xffffff);
                canvas.drawPath(p.mPath,mPaint);
                //后面是填充
                mPaint.clearShadowLayer();
                mPaint.setColor(p.mColor);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(2);
                canvas.drawPath(p.mPath, mPaint);
            }
        }

        if(mSelected != null){
            //选中时，绘制描边效果
            mPaint.clearShadowLayer();
            mPaint.setStrokeWidth(1);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mSelected.mColor);
            canvas.drawPath(mSelected.mPath, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.BLACK);
            canvas.drawPath(mSelected.mPath, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event.getX(),event.getY());
        return true;
    }

    private void handleTouch(float x,float y){
        for (int i = 0; i < mCount; i++) {
            Province p = mProvinces.get(i);
            if(mSelected != p && p.isTouch(x / mScale,y / mScale)){
                mSelected = p;
                L.e("province : " + i);
                postInvalidate();
            }
        }
    }

    private void calculateScale(){
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        mRotate = 0;
        mScale = w / mRectMap.width();

//        if(w > h == mRectMap.width() > mRectMap.height()){
//            mRotate = 0;
//            mScale = Math.min(w / mRectMap.width(),h / mRectMap.height());
//        }else{
//            mRotate = 90;
//            mScale = Math.min(h / mRectMap.width(),w / mRectMap.height());
//        }
    }

}
