package notch.notch.i;

/**
 * <p>
 * 针对API>= 28时，判断手机是否含有刘海区
 * </p>
 **/
public interface OnCutoutListener {

    // is has cutout?
    void isHasCutout(boolean isHas);
}
