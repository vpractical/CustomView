package notch.notch.i;

import android.view.DisplayCutout;

/**
 * <p>
 * {@link OnCutoutListener}child class，为了获取相关刘海信息
 * </p>
 **/
public interface OnCutoutDetailListener extends OnCutoutListener {

    // has cutout
    void onCutout(DisplayCutout cutout);

}
