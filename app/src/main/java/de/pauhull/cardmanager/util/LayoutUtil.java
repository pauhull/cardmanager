package de.pauhull.cardmanager.util;

import android.content.Context;

public class LayoutUtil {

    public static int dpToPx(float dps, Context context) {

        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dps * density + 0.5f);
    }
}
