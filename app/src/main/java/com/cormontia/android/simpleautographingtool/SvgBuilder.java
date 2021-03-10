package com.cormontia.android.simpleautographingtool;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SvgBuilder {

    private static final String TAG = "SimpleAutographTool";

    public static String buildSvg(List<Point> points) {

        Point leftBottom = maxPoint(points); //TODO?~ Isn't that thr _right_handside bottom point?
        //String res = "<svg width=\""+ leftBottom.x + "\" height=\"" + leftBottom.y + "\">";
        String res = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>";
        res += "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";
        res += "<svg version=\"1.1\" ";
        res += "width=\""+ leftBottom.x + "\" height=\"" + leftBottom.y + " ";

        res += "xmlns=\"http://www.w3.org/2000/svg\" ";
        res += "xmlns:xlink=\"http://www.w3.org/1999/xlink\"";
        res += ">";


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

    public static List<Point> parseSvg(String svg) {
        Log.i(TAG, svg);
        int pointsAttributeIdx = svg.indexOf("points=\"");
        int pointsListStartIdx = pointsAttributeIdx + "points=\"".length();
        int pointsListEndIdx = svg.indexOf("\"", pointsListStartIdx);
        Log.i(TAG, pointsAttributeIdx +  " " + pointsListStartIdx + " " + pointsListEndIdx);
        String pointsList = svg.substring(pointsListStartIdx, pointsListEndIdx);

        // "pointsList" now consists of a list of coordinates.
        // Separated by whitespace, each pair of coordinates is of the form "x,y", where x and y are integers.
        String[] arrayOfCoordinates = pointsList.split(" ");
        List<Point> loadedPoints = new ArrayList<>();
        for (String currentCoordinates : arrayOfCoordinates) {
            // It is possible that some of the elements in the array are just whitespace. Let's avoid that.
            String trimmedCoordinates = currentCoordinates.trim();
            if (trimmedCoordinates.length() > 0)
            {
                String[] coordinatePair = trimmedCoordinates.split(",");
                if (coordinatePair.length != 2) {
                    Log.e(TAG, "Syntax error while parsing list of integers - set of coordinates has wrong amount of numbers.");
                }
                try {
                    if (coordinatePair[0] == null || coordinatePair[1] == null) {
                        Log.e(TAG, "Encountered a NULL while parsing the list of points.");
                    }
                    int x = Integer.parseInt(coordinatePair[0]);
                    int y = Integer.parseInt(coordinatePair[1]);
                    loadedPoints.add(new Point(x, y));
                } catch (NumberFormatException exc) {
                    Log.e(TAG, "Failed to parse an integer, while parsing the list of points.");
                }
            }
        }
        return loadedPoints;
    }

}
