package qqyadf.popoverview;

import android.content.res.Resources;

/**
 * Created by Yeah on 2016-06-08.
 */

public class Misc {
    public static int dpToPx(int dp, Resources resources) {
        return (int) (resources.getDisplayMetrics().density * dp + 0.5f);
    }
}

class SizeClass {
    public int width;
    public int height;

    public SizeClass(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public SizeClass(SizeClass size) {
        width = size.width;
        height = size.height;
    }
}
