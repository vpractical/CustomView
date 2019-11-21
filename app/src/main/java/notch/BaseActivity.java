package notch;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import notch.notch.NotchUtil;
import notch.notch.i.OnCutoutListener;


public abstract class BaseActivity extends AppCompatActivity {
    private boolean isHasCutout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        NotchUtil.setImmersiveWithNotch(this, true, WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER);
//        if (getLocalClassName().contains("activity.SplashActivity")) {
//            StatusBarUtil.setStatusBarColor(this, R.color.white);
//        } else {
//            StatusBarUtil.setStatusBarColor(this, R.color.colorAccent);
//        }
//

    }

    protected boolean getIsHasCutout(){
        return isHasCutout;
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //判断有没有刘海屏(获取需要在View绑定到Window之后，否在拿不到)
        NotchUtil.isHasCutout(this, new OnCutoutListener() {
            @Override
            public void isHasCutout(boolean isHas) {
                isHasCutout = isHas;
                if (isHas) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    /**
                     * * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT 全屏模式，内容下移 非全屏模式下不受影响
                     * * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES 允许内容进入刘海区域
                     * * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER 不允许内容进入刘海区域
                     */
                    NotchUtil.setImmersiveWithNotch(BaseActivity.this, false,
                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES);
                }
            }
        });
    }
}