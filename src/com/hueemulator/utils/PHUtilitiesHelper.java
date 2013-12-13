package com.hueemulator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PHUtilitiesHelper {

    private static final int CPT_RED = 0;
    private static final int CPT_GREEN = 1;
    private static final int CPT_BLUE = 2;


    /**
     * Generates the color for the given XY values and light model Id. Note:
     * When the exact values cannot be represented, it will return the closest
     * match.
     * 
     * @param points
     *            the float array contain x and the y value.
     * @param model
     *            the model of the lamp, example: "LCT001" for hue bulb. Used to
     *            calculate the color gamut. If this value is empty the default
     *            gamut values are used.
     * @return int the Android Color value. If xy is null OR xy is not an array
     *         of size 2, Color.BLACK will be returned
     * @throws NullPointerException
     *             If either points or model is null
     */
    public static int colorFromXY(float[] points, String model) {

        PointF xy = new PointF(points[0], points[1]);
        
        xy = fixIfOutOfRange(xy, model);
        
        float x = xy.x;
        float y = xy.y;
        float z = 1.0f - x - y;
        float y2 = 1.0f;
        float x2 = (y2 / y) * x;
        float z2 = (y2 / y) * z;

        // sRGB D65 conversion
        float r = x2 * 3.2406f - y2 * 1.5372f - z2 * 0.4986f;
        float g = -x2 * 0.9689f + y2 * 1.8758f + z2 * 0.0415f;
        float b = x2 * 0.0557f - y2 * 0.2040f + z2 * 1.0570f;

        if (r > b && r > g && r > 1.0f) {
            // red is too big
            g = g / r;
            b = b / r;
            r = 1.0f;
        } else if (g > b && g > r && g > 1.0f) {
            // green is too big
            r = r / g;
            b = b / g;
            g = 1.0f;
        } else if (b > r && b > g && b > 1.0f) {
            // blue is too big
            r = r / b;
            g = g / b;
            b = 1.0f;
        }
        // Apply gamma correction
        r = r <= 0.0031308f ? 12.92f * r : (1.0f + 0.055f)
                * (float) Math.pow(r, (1.0f / 2.4f)) - 0.055f;
        g = g <= 0.0031308f ? 12.92f * g : (1.0f + 0.055f)
                * (float) Math.pow(g, (1.0f / 2.4f)) - 0.055f;
        b = b <= 0.0031308f ? 12.92f * b : (1.0f + 0.055f)
                * (float) Math.pow(b, (1.0f / 2.4f)) - 0.055f;

        if (r > b && r > g) {
            // red is biggest
            if (r > 1.0f) {
                g = g / r;
                b = b / r;
                r = 1.0f;
            }
        } else if (g > b && g > r) {
            // green is biggest
            if (g > 1.0f) {
                r = r / g;
                b = b / g;
                g = 1.0f;
            }
        } else if (b > r && b > g && b > 1.0f) {
                r = r / b;
                g = g / b;
                b = 1.0f;
        }

        // neglecting if the value is negative.
        if (r < 0.0f) {
            r = 0.0f;
        }
        if (g < 0.0f) {
            g = 0.0f;
        }
        if (b < 0.0f) {
            b = 0.0f;
        }

        // Converting float components to int components.
        int r1 = (int) (r * 255.0f);
        int g1 = (int) (g * 255.0f);
        int b1 = (int) (b * 255.0f);
        return HueColor.rgb(r1, g1, b1);
    }

    public static PointF fixIfOutOfRange(PointF xy, String model) {
     List<PointF> colorPoints = colorPointsForModel(model);
        boolean inReachOfLamps = checkPointInLampsReach(xy, colorPoints);
        if (!inReachOfLamps) {
            // It seems the colour is out of reach
            // let's find the closest colour we can produce with our lamp and
            // send this XY value out.
            // Find the closest point on each line in the triangle.
            PointF pAB = getClosestPointToPoints(colorPoints.get(CPT_RED),
                    colorPoints.get(CPT_GREEN), xy);
            PointF pAC = getClosestPointToPoints(colorPoints.get(CPT_BLUE),
                    colorPoints.get(CPT_RED), xy);

            PointF pBC = getClosestPointToPoints(colorPoints.get(CPT_GREEN),
                    colorPoints.get(CPT_BLUE), xy);

            // Get the distances per point and see which point is closer to our point.
     
            float dAB = getDistanceBetweenTwoPoints(xy, pAB);
            float dAC = getDistanceBetweenTwoPoints(xy, pAC);
            float dBC = getDistanceBetweenTwoPoints(xy, pBC);
            float lowest = dAB;
            PointF closestPoint = pAB;
            if (dAC < lowest) {
                lowest = dAC;
                closestPoint = pAC;
            }
            if (dBC < lowest) {
                lowest = dBC;
                closestPoint = pBC;
            }
            // Change the xy value to a value which is within the reach of the lamp.
            xy.x = closestPoint.x;
            xy.y = closestPoint.y;
        }
        return new PointF(xy.x, xy.y);
    }
    
    /**
     * Return x, y value from RGB
     * 
     * @param red    the amount of red.
     * @param blue   the amount of blue.
     * @param green  the amount of green.
     * @param model
     *            the model Id of Light
     * @return float[] the float array of length 2, where index 0, 1 gives
     *         respective x, y values.
     */
    public float[] calculateXYFromRGB(int red, int green, int blue, String model) {
        int rgb = HueColor.rgb(red, green, blue);
        return calculateXY(rgb, model);      
    }
    
    /**
     * Return x, y value from android color & light model Id.
     * 
     * @param color
     *            the color value
     * @param model
     *            the model Id of Light
     * @return float[] the float array of length 2, where index 0, 1 gives
     *         respective x, y values.
     */
    public float[] calculateXY(int color, String model) {

        // Default to white
        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;

        // Get no. of components
        red = HueColor.red(color) / 255.0f;
        green = HueColor.green(color) / 255.0f;
        blue = HueColor.blue(color) / 255.0f;

        // Wide gamut conversion D65
        float r = ((red > 0.04045f) ? (float) Math.pow((red + 0.055f)
                / (1.0f + 0.055f), 2.4f) : (red / 12.92f));
        float g = (green > 0.04045f) ? (float) Math.pow((green + 0.055f)
                / (1.0f + 0.055f), 2.4f) : (green / 12.92f);
        float b = (blue > 0.04045f) ? (float) Math.pow((blue + 0.055f)
                / (1.0f + 0.055f), 2.4f) : (blue / 12.92f);

        // Why values are different in ios and android , IOS is considered
        // Modified conversion from RGB -> XYZ with better results on colors for
        // the lights
        float x = r * 0.649926f + g * 0.103455f + b * 0.197109f;
        float y = r * 0.234327f + g * 0.743075f + b * 0.022598f;
        float z = r * 0.0000000f + g * 0.053077f + b * 1.035763f;

        float xy[] = new float[2];

        xy[0] = (x / (x + y + z));
        xy[1] = (y / (x + y + z));
        if (Float.isNaN(xy[0])) {
            xy[0] = 0.0f;
        }
        if (Float.isNaN(xy[1])) {
            xy[1] = 0.0f;
        }
        // Check if the given XY value is within the colourreach of our lamps.
        PointF xyPoint = new PointF(xy[0], xy[1]);
        List<PointF> colorPoints = colorPointsForModel(model);
        boolean inReachOfLamps = checkPointInLampsReach(xyPoint, colorPoints);
        if (!inReachOfLamps) {
            // It seems the colour is out of reach
            // let's find the closes colour we can produce with our lamp and
            // send this XY value out.

            // Find the closest point on each line in the triangle.
            PointF pAB = getClosestPointToPoints(colorPoints.get(CPT_RED),   colorPoints.get(CPT_GREEN), xyPoint);
            PointF pAC = getClosestPointToPoints(colorPoints.get(CPT_BLUE),  colorPoints.get(CPT_RED), xyPoint);
            PointF pBC = getClosestPointToPoints(colorPoints.get(CPT_GREEN), colorPoints.get(CPT_BLUE), xyPoint);

            // Get the distances per point and see which point is closer to our
            // Point.
            float dAB = getDistanceBetweenTwoPoints(xyPoint, pAB);
            float dAC = getDistanceBetweenTwoPoints(xyPoint, pAC);
            float dBC = getDistanceBetweenTwoPoints(xyPoint, pBC);

            float lowest = dAB;
            PointF closestPoint = pAB;
            if (dAC < lowest) {
                lowest = dAC;
                closestPoint = pAC;
            }
            if (dBC < lowest) {
                lowest = dBC;
                closestPoint = pBC;
            }

            // Change the xy value to a value which is within the reach of the lamp.
            xy[0] = closestPoint.x;
            xy[1] = closestPoint.y;
        }
        xy[0] = precision(4, xy[0]);
        xy[1] = precision(4, xy[1]);
        return xy;
    }

    /**
     * Method to see if the given XY value is within the reach of the lamps.
     * 
     * @param point
     *            the point containing the X,Y value
     * @param colorPoints
     *            array list of color points for a lamp
     * @return boolean true if within reach, false otherwise.
     */
    private static boolean checkPointInLampsReach(PointF point, List<PointF> colorPoints) {
        if (point == null || colorPoints == null) {
            return false;
        }
        PointF red = colorPoints.get(CPT_RED);
        PointF green = colorPoints.get(CPT_GREEN);
        PointF blue = colorPoints.get(CPT_BLUE);
        PointF v1 = new PointF(green.x - red.x, green.y - red.y);
        PointF v2 = new PointF(blue.x - red.x, blue.y - red.y);
        PointF q = new PointF(point.x - red.x, point.y - red.y);
        float s = crossProduct(q, v2) / crossProduct(v1, v2);
        float t = crossProduct(v1, q) / crossProduct(v1, v2);
        if ((s >= 0.0f) && (t >= 0.0f) && (s + t <= 1.0f)) {
            return true;
        }
        
        return false;

    }

    /**
     * Find the distance between two points.
     * 
     * @param one      the point object
     * @param two      the point object
     * @return float   the distance between point one and two
     */
    private static float getDistanceBetweenTwoPoints(PointF one, PointF two) {
        float dx = one.x - two.x; // horizontal difference
        float dy = one.y - two.y; // vertical difference
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        return dist;
    }

    /**
     * Calculates crossProduct of two 2D vectors / points.
     * 
     * @param point1
     *            first point used as vector
     * @param point2
     *            second point used as vector
     * @return crossProduct of vectors
     */
    private static float crossProduct(PointF point1, PointF point2) {

        return (point1.x * point2.y - point1.y * point2.x);
    }

    /**
     * Retrieves color points from a given lamp model number
     * 
     * @param model
     *            Model number of a lamp
     * @return ArrayList<PointF> the List of color points
     */
    private static List<PointF> colorPointsForModel(String model) {
        // LLC001, // LedStrip // LWB001, // LivingWhite
        if (model == null) { // if model is not known go for the default choice
            model = " ";
        }
        ArrayList<PointF> colorPoints = new ArrayList<PointF>();

        ArrayList<String> hueBulbs = new ArrayList<String>();
        hueBulbs.add("LCT001"); /* Hue A19 */
        hueBulbs.add("LCT002"); /* Hue BR30 */
        hueBulbs.add("LCT003"); /* Hue GU10 */

        ArrayList<String> livingColors = new ArrayList<String>();
        livingColors.add("LLC001"); /* Monet, Renoir, Mondriaan (gen II) */
        livingColors.add("LLC005"); /* Bloom (gen II) */
        livingColors.add("LLC006"); /* Iris (gen III) */
        livingColors.add("LLC007"); /* Bloom, Aura (gen III) */
        livingColors.add("LLC011"); /* Hue Bloom */
        livingColors.add("LLC013"); /* Disney Story Teller */
        livingColors.add("LST001"); /* Light Strips */

        if (hueBulbs.contains(model)) {
            // Hue bulbs color gamut triangle
            colorPoints.add(new PointF(.674F, 0.322F)); // Red
            colorPoints.add(new PointF(0.408F, 0.517F)); // Green
            colorPoints.add(new PointF(0.168F, 0.041F)); // Blue
        } else if (livingColors.contains(model)) {
            // LivingColors color gamut triangle
            colorPoints.add(new PointF(0.703F, 0.296F)); // Red
            colorPoints.add(new PointF(0.214F, 0.709F)); // Green
            colorPoints.add(new PointF(0.139F, 0.081F)); // Blue
        } else {
            // Default construct triangle which contains all values
            colorPoints.add(new PointF(1.0F, 0.0F));// Red
            colorPoints.add(new PointF(0.0F, 1.0F)); // Green
            colorPoints.add(new PointF(0.0F, 0.0F));// Blue
        }
        return colorPoints;
    }

    /**
     * Find the closest point on a line. This point will be within reach of the
     * lamp.
     * 
     * @param pointA
     *            the point where the line starts
     * @param pointB
     *            the point where the line ends
     * @param pointP
     *            the point which is close to a line.
     * @return PointF the point which is on the line.
     */
    private static PointF getClosestPointToPoints(PointF pointA, PointF pointB,
            PointF pointP) {
        if (pointA == null || pointB == null || pointP == null) {
            return null;
        }
        PointF pointAP = new PointF(pointP.x - pointA.x, pointP.y - pointA.y);
        PointF pointAB = new PointF(pointB.x - pointA.x, pointB.y - pointA.y);
        float ab2 = pointAB.x * pointAB.x + pointAB.y * pointAB.y;
        float apAb = pointAP.x * pointAB.x + pointAP.y * pointAB.y;
        float t = apAb / ab2;
        if (t < 0.0f) {
            t = 0.0f;
        }
        else if (t > 1.0f) {
            t = 1.0f;
        }
        PointF newPoint = new PointF(pointA.x + pointAB.x * t, pointA.y
                + pointAB.y * t);
        return newPoint;
    }

 public static float precision(int decimalPlace, float val) {
  if(Float.isNaN(val)){
   return 0.0f;
  }

  String str=String.format(Locale.ENGLISH,"%."+decimalPlace+'f', val);
  return Float.valueOf(str);
  
   }
}
