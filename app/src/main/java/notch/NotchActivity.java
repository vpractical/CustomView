package notch;

import android.os.Bundle;

import com.y.customview.R;


public class NotchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(getIsHasCutout()){
//            ScreenAdapterLayout layout = findViewById(R.id.layout);
//            layout.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        }
    }

}
