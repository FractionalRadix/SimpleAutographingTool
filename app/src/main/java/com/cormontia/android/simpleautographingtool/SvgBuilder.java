package com.cormontia.android.simpleautographingtool;

import android.graphics.Point;

import java.util.List;

public class SvgBuilder {

    public static String buildSvg(List<Point> points) {

        Point leftBottom = maxPoint(points);
        String res = "<svg width=\""+ leftBottom.x + "\" height=\"" + leftBottom.y + "\">";

        res += "<polyline";
        res += " " + determinePointsAttribute(points);
        res += " " + "style=\"fill:none;stroke:black;stroke-width:1\"";
        res += "/>";

        res += "</svg>";

        return res;
    }

    /**
     * Given a list of points, build a the points attribute for an SVG line.
     * @param points A list of points
     * @return An SVG attribute for a line that visits all the points in the list, in order.
     */
    private static StringBuilder determinePointsAttribute(List<Point> points) {
        StringBuilder pointsAttribute = new StringBuilder("points=\"");
        if (points != null) {
            for (Point point : points) {
                pointsAttribute.append(" ");
                pointsAttribute.append(point.x);
                pointsAttribute.append(",");
                pointsAttribute.append(point.y);
            }
        }
        pointsAttribute.append("\"");
        return pointsAttribute;
    }

    /** Given a list of points, determine the highest X-coordinate and the highest Y-coordinate.
     * In other words: determine the maximum X value in the list, and the maximum Y value, at the same time.
     * @param list A list of points.
     * @return A new point (x,y), where x is the highest X-coordinate in the list, and y is the highest Y-coordinate in the list.
     */
    private static Point maxPoint(List<Point> list) {
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point point : list) {
            if (point.x > maxX) { maxX = point.x; }
            if (point.y > maxY) { maxY = point.y; }
        }
        return new Point(maxX, maxY);
    }
}
