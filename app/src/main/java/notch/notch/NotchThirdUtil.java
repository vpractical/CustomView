package notch.notch;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.y.customview.BuildConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import notch.notch.i.OnCutoutDetailListener;
import notch.notch.i.OnCutoutListener;

public class NotchThirdUtil {

    private static final String TAG = "NotchThirdUtil";

    /**
     * 小米删除刘海区域
     *
     * @param context context
     */
    public static void clearExtraFlag(Context context) {
        int flag = 0x00000100 | 0x00000200 | 0x00000400;
        if (context instanceof AppCompatActivity) {
            AppCompatActivity app = (AppCompatActivity) context;
            try {
                Method method = Window.class.getMethod("clearExtraFlags",
                        int.class);
                method.invoke(app.getWindow(), flag);
            } catch (Exception e) {
                Log.i(TAG, "addExtraFlags not found.");
            }
        }
    }

    /**
     * 小米添加刘海区域
     *
     * @param context context
     */
    public static void addExtraFlag(Context context) {
        int flag = 0x00000100 | 0x00000200 | 0x00000400;
        if (context instanceof AppCompatActivity) {
            AppCompatActivity app = (AppCompatActivity) context;
            try {
                Method method = Window.class.getMethod("addExtraFlags",
                        int.class);
                method.invoke(app.getWindow(), flag);
            } catch (Exception e) {
                Log.i(TAG, "addExtraFlags not found.");
            }
        }
    }

    /**
     * 判断用户是否开启了隐藏刘海区域
     *
     * @param context
     * @return
     */
    public static boolean isHideNotchScreen4Xiaomi(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(context.getContentResolver(), "force_black", 0) == 1;
        }else{
            return false;
        }
    }

    /**
     * 判断小米是否有刘海屏
     *
     * @return 是否有刘海屏
     */
    public static boolean hasNotchInScreenAtXiaomi() {
        return SystemProperties.getint("ro.miui.notch", 0) == 1;
    }

    /**
     * 获得小米刘海屏幕高度
     *
     * @param context context
     * @return 小米屏幕高度
     */
    public static int getNotchXiaomiHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("notch_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获得小米刘海屏幕宽度
     *
     * @param context context
     * @return 小米屏幕宽度
     */
    public static int getNotchXiaomiWidth(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("notch_width", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;//是否有凹槽
    private static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;//是否有圆角

    /**
     * 判断是否有刘海屏
     *
     * @param context context
     * @return 是否有刘海屏
     */
    public static boolean hasNotchInScreenAtVoio(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("android.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VOIO);

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 判断oppo是否有刘海屏
     *
     * @param context context
     * @return 是否有刘海屏
     */
    public static boolean hasNotchInScreenAtOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /*刘海屏全屏显示FLAG*/
    private static final int FLAG_NOTCH_SUPPORT = 0x00010000;

    /**
     * 设置应用窗口在华为刘海屏手机使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
        } catch (Exception e) {
            Log.e(TAG, "other Exception");
        }
    }

    /**
     * 设置应用窗口在华为刘海屏手机使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setNotFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("clearHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
        } catch (Exception e) {
            Log.e(TAG, "other Exception");
        }
    }

    /**
     * 华为手机是否有刘海屏
     *
     * @param context context
     * @return 是否有刘海屏
     */
    public static boolean hasNotchInScreenAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInScreen Exception");
        }
        return ret;
    }

    /**
     * 获取华为刘海屏的刘海尺寸
     *
     * @param context context
     * @return 刘海尺寸
     */
    public static int[] getNotchSize4Huawei(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "getNotchSize Exception");
        }
        return ret;
    }

    /**
     * 设置正常模式
     *
     * @param context context
     */
    public static void setNormalMode(Context context, boolean isShowActionBar) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity app = (AppCompatActivity) context;
            if (app.getSupportActionBar() != null) {
                app.getSupportActionBar().hide();
            }
            app.getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }

    /**
     * 针对Android P适配刘海
     *
     * @param context     context
     * @param isUseCutout 是否使用刘海
     * @param cutoutMode  刘海适配方式，如果不适配，则传-1，使用默认策略
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void setNotchModeforApi28(Context context, boolean isUseImmersiveBars, boolean isUseCutout, boolean isLightMode, int cutoutMode) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity app = (AppCompatActivity) context;
            if (app.getSupportActionBar() != null) {
                app.getSupportActionBar().hide();
            }
            //LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            // 只有当DisplayCutout完全包含在系统状态栏中时，才允许窗口延伸到DisplayCutout区域显示。
            //LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            //该窗口决不允许与DisplayCutout区域重叠,但是会把状态栏变成黑色，效果很差，建议这种情况使用DEFAULT。
            //LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            //该窗口始终允许延伸到屏幕短边上的DisplayCutout区域。
            //PS:如果需要应用的布局延伸到刘海区显示，需要设置SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN。
            if (isUseImmersiveBars) {
                app.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); //设置页面全屏显示
            } else {
                if (isUseCutout) {
                    app.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); //设置页面全屏显示
                } else {
                    app.getWindow().getDecorView().setSystemUiVisibility(0);
                }
            }
            if (cutoutMode == -1) {
                cutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            }
            WindowManager.LayoutParams lp = app.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = cutoutMode;
            //设置页面延伸到刘海区显示
            app.getWindow().setAttributes(lp);
        }
    }

    /**
     * 获取刘海高度等信息
     *
     * @param context context
     */
    @RequiresApi(api = 28)
    public static void getNotchSize4Google(Context context, @Nullable final OnCutoutListener listener) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity app = (AppCompatActivity) context;
            DisplayCutout cutout = app.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
            if (cutout == null) {
                if (listener != null) {
                    listener.isHasCutout(false);
                }
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "cutout==null, is not notch screen");//通过cutout是否为null判断是否刘海屏手机
                }
            } else {
                List<Rect> rects = cutout.getBoundingRects();
                if (rects == null || rects.size() == 0) {
                    listener.isHasCutout(false);
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "rects==null || rects.size()==0, is not notch screen");
                    }
                } else {
                    listener.isHasCutout(true);
                    //如需用到cutout信息，则使用
                    if (listener instanceof OnCutoutDetailListener) {
                        ((OnCutoutDetailListener) listener).onCutout(cutout);
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "rect size:" + rects.size());//注意：刘海的数量可以是多个
                        for (Rect rect : rects) {
                            Log.e(TAG, "cutout.getSafeInsetTop():" + cutout.getSafeInsetTop()
                                    + ", cutout.getSafeInsetBottom():" + cutout.getSafeInsetBottom()
                                    + ", cutout.getSafeInsetLeft():" + cutout.getSafeInsetLeft()
                                    + ", cutout.getSafeInsetRight():" + cutout.getSafeInsetRight()
                                    + ", cutout.rects:" + rect
                            );
                        }
                    }
                }
            }
        }
    }
}
