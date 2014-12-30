/**
 * ClickUtils.java
 */
package freelancer.worldvideo.util;

/**
 *@function: 判断连击
 *@author jiangbing
 *@data: 2014-3-8
 */
public class ClickUtils
{
	private static long lastClickTime;
	 
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
