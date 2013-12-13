package com.hueemulator.utils;

import java.util.HashMap;
import java.util.Locale;

/**
 * The Color class defines methods for creating and converting color ints.
 * Colors are represented as packed ints, made up of 4 bytes: alpha, red,
 * green, blue. The values are unpremultiplied, meaning any transparency is
 * stored solely in the alpha component, and not in the color components. The
 * components are stored as follows (alpha << 24) | (red << 16) |
 * (green << 8) | blue. Each component ranges between 0..255 with 0
 * meaning no contribution for that component, and 255 meaning 100%
 * contribution. Thus opaque-black would be 0xFF000000 (100% opaque but
 * no contributions from red, green, or blue), and opaque-white would be
 * 0xFFFFFFFF
 */
public class HueColor {
    public static final int BLACK       = 0xFF000000;
    public static final int DKGRAY      = 0xFF444444;
    public static final int GRAY        = 0xFF888888;
    public static final int LTGRAY      = 0xFFCCCCCC;
    public static final int WHITE       = 0xFFFFFFFF;
    public static final int RED         = 0xFFFF0000;
    public static final int GREEN       = 0xFF00FF00;
    public static final int BLUE        = 0xFF0000FF;
    public static final int YELLOW      = 0xFFFFFF00;
    public static final int CYAN        = 0xFF00FFFF;
    public static final int MAGENTA     = 0xFFFF00FF;
    public static final int TRANSPARENT = 0;

    /**
     * Return the alpha component of a color int. This is the same as saying
     * color >>> 24
     */
    public static int alpha(int color) {
        return color >>> 24;
    }

    /**
     * Return the red component of a color int. This is the same as saying
     * (color >> 16) & 0xFF
     */
    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    /**
     * Return the green component of a color int. This is the same as saying
     * (color >> 8) & 0xFF
     */
    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    /**
     * Return the blue component of a color int. This is the same as saying
     * color & 0xFF
     */
    public static int blue(int color) {
        return color & 0xFF;
    }

    /**
     * Return a color-int from red, green, blue components.
     * The alpha component is implicity 255 (fully opaque).
     * These component values should be [0..255], but there is no
     * range check performed, so if they are out of range, the
     * returned color is undefined.
     * @param red  Red component [0..255] of the color
     * @param green Green component [0..255] of the color
     * @param blue  Blue component [0..255] of the color
     */
    public static int rgb(int red, int green, int blue) {
        return (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Return a color-int from alpha, red, green, blue components.
     * These component values should be [0..255], but there is no
     * range check performed, so if they are out of range, the
     * returned color is undefined.
     * @param alpha Alpha component [0..255] of the color
     * @param red   Red component [0..255] of the color
     * @param green Green component [0..255] of the color
     * @param blue  Blue component [0..255] of the color
     */
    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Returns the hue component of a color int.
     * 
     * @return A value between 0.0f and 1.0f
     * 
     * @hide Pending API council
     */
    public static float hue(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int v = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));

        float h;

        if (v == temp) {
            h = 0;
        } else {
            final float vtemp = (float) (v - temp);
            final float cr = (v - r) / vtemp;
            final float cg = (v - g) / vtemp;
            final float cb = (v - b) / vtemp;

            if (r == v) {
                h = cb - cg;
            } else if (g == v) {
                h = 2 + cr - cb;
            } else {
                h = 4 + cg - cr;
            }

            h /= 6.f;
            if (h < 0) {
                h++;
            }
        }

        return h;
    }

    /**
     * Returns the saturation component of a color int.
     * 
     * @return A value between 0.0f and 1.0f
     * 
     * @hide Pending API council
     */
    public static float saturation(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;


        int v = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));

        float s;

        if (v == temp) {
            s = 0;
        } else {
            s = (v - temp) / (float) v;
        }

        return s;
    }

    /**
     * Returns the brightness component of a color int.
     *
     * @return A value between 0.0f and 1.0f
     *
     * @hide Pending API council
     */
    public static float brightness(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int v = Math.max(b, Math.max(r, g));

        return (v / 255.f);
    }

    /**
     * Parse the color string, and return the corresponding color-int.
     * If the string cannot be parsed, throws an IllegalArgumentException
     * exception. Supported formats are:
     * #RRGGBB
     * #AARRGGBB
     * 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta',
     * 'yellow', 'lightgray', 'darkgray', 'grey', 'lightgrey', 'darkgrey',
     * 'aqua', 'fuschia', 'lime', 'maroon', 'navy', 'olive', 'purple',
     * 'silver', 'teal'
     */
    public static int parseColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int)color;
        } else {
            Integer color = S_COLOR_NAME_MAP.get(colorString.toLowerCase(Locale.US));
            if (color != null) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color");
    }


    private static final HashMap<String, Integer> S_COLOR_NAME_MAP;

    static {
        S_COLOR_NAME_MAP = new HashMap<String, Integer>();
        S_COLOR_NAME_MAP.put("black", BLACK);
        S_COLOR_NAME_MAP.put("darkgray", DKGRAY);
        S_COLOR_NAME_MAP.put("gray", GRAY);
        S_COLOR_NAME_MAP.put("lightgray", LTGRAY);
        S_COLOR_NAME_MAP.put("white", WHITE);
        S_COLOR_NAME_MAP.put("red", RED);
        S_COLOR_NAME_MAP.put("green", GREEN);
        S_COLOR_NAME_MAP.put("blue", BLUE);
        S_COLOR_NAME_MAP.put("yellow", YELLOW);
        S_COLOR_NAME_MAP.put("cyan", CYAN);
        S_COLOR_NAME_MAP.put("magenta", MAGENTA);
        S_COLOR_NAME_MAP.put("aqua", 0x00FFFF);
        S_COLOR_NAME_MAP.put("fuchsia", 0xFF00FF);
        S_COLOR_NAME_MAP.put("darkgrey", DKGRAY);
        S_COLOR_NAME_MAP.put("grey", GRAY);
        S_COLOR_NAME_MAP.put("lightgrey", LTGRAY);
        S_COLOR_NAME_MAP.put("lime", 0x00FF00);
        S_COLOR_NAME_MAP.put("maroon", 0x800000);
        S_COLOR_NAME_MAP.put("navy", 0x000080);
        S_COLOR_NAME_MAP.put("olive", 0x808000);
        S_COLOR_NAME_MAP.put("purple", 0x800080);
        S_COLOR_NAME_MAP.put("silver", 0xC0C0C0);
        S_COLOR_NAME_MAP.put("teal", 0x008080);

    }
}