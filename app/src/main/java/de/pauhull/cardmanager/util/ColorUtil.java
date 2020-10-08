package de.pauhull.cardmanager.util;

public class ColorUtil {

    // https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
    public static float getLuminance(int color) {

        float r = ((color & (0xff << 16)) >> 16) / 255f;
        float g = ((color & (0xff << 8)) >> 8) / 255f;
        float b = (color & (0xff)) / 255f;

        if (r <= 0.03928) {
            r /= 12.92;
        } else {
            r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
        }

        if (g <= 0.03928) {
            g /= 12.92;
        } else {
            g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
        }

        if (b <= 0.03928) {
            b /= 12.92;
        } else {
            b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
        }

        return 0.1612f * r + 0.7152f * g + 0.0722f * b;
    }

    public static int highestContrast(int a, int b, int bg) {

        float lumBg = getLuminance(bg);
        float lumA = getLuminance(a);
        float lumB = getLuminance(b);

        float aContrast = (Math.max(lumBg, lumA) + 0.05f) / (Math.min(lumBg, lumA) + 0.05f);
        float bContrast = (Math.max(lumBg, lumB) + 0.05f) / (Math.min(lumBg, lumB) + 0.05f);

        return aContrast > bContrast ? a : b;
    }

    public static int mixColor(int aArgb, int bArgb, float mix) {

        int aa = (aArgb & (0xff << 24)) >> 24;
        int ar = (aArgb & (0xff << 16)) >> 16;
        int ag = (aArgb & (0xff << 8)) >> 8;
        int ab = aArgb & 0xff;

        if (aa == -1) aa = 0xff; // no alpha

        int ba = (bArgb & (0xff << 24)) >> 24;
        int br = (bArgb & (0xff << 16)) >> 16;
        int bg = (bArgb & (0xff << 8)) >> 8;
        int bb = bArgb & 0xff;

        if (ba == -1) ba = 0xff; // no alpha

        int a = (int) (aa * (1f - mix) + ba * mix);
        int r = (int) (ar * (1f - mix) + br * mix);
        int g = (int) (ag * (1f - mix) + bg * mix);
        int b = (int) (ab * (1f - mix) + bb * mix);

        return ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

}
