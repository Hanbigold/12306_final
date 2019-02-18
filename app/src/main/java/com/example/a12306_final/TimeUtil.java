package com.example.a12306_final;

/**
 * Created by 老汉 on 2017/12/18.
 */

public class TimeUtil {
    private static long lastClickTime = 0;
    private static long DIFF = 1000;
    private static int lastButtonId = -1;

    public static boolean isFastDoubleClick()
    {
        return isFastDoubleClick(-1,DIFF);
    }
    public static boolean isFastDoubleClick(int buttonId)
    {
        return isFastDoubleClick(buttonId,DIFF);
    }
    public static boolean isFastDoubleClick(int buttonId,long diff)
    {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime>0 && timeD < diff)
        {
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }
}
