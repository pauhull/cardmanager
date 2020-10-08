package de.pauhull.cardmanager.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorUtil {

    public static void vibratePulse(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
        }
    }

}
