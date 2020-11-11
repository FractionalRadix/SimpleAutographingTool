package com.cormontia.android.simpleautographingtool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements ListOfPoints {

    private static final String TAG = "SimpleAutographTool";

    private AutographViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(AutographViewModel.class);
        AutographingView autographingView = findViewById(R.id.autographingView);
        autographingView.setPointsOwner(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void addPoint(Point pt) {
        MutableLiveData<List<Point>> pointsHolder = viewModel.getPoints();
        List<Point> points = pointsHolder.getValue();
        if (points == null) {
            points = new ArrayList<>();
        }
        points.add(pt);
        pointsHolder.postValue(points);
    }

    @Override
    public List<Point> getPoints() {
        return viewModel.getPoints().getValue();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Android warns you not to use these constants in switch statements.
        int itemId = item.getItemId();
        if (itemId == R.id.load) {
            loadAutograph();
        } else if (itemId == R.id.save) {
            saveAutograph();
        } else if (itemId == R.id.share) {
            String svg = buildSvg();

            Intent share = new Intent(Intent.ACTION_SEND);
            //TODO?~ Is this the right type for the Intent...?
            share.setType("image/svg+xml");
            //TODO?~ Is this the right name for the Intent...?
            share.putExtra(Intent.EXTRA_STREAM, svg);
            //share.putExtra(Intent.ACTION_SEND, svg);
            //share.putExtra(Intent.ACTION_MEDIA_SHARED, svg);

            startActivity(share);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAutograph() {
        String filename = "autograph.svg";
        String svg = buildSvg();
        
        try (FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(svg.getBytes());
            fos.flush();
            Toast.makeText(this, "File saved!", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException fnfe) {
            Toast.makeText(this, "Cannot open file \"" + filename + "\"", Toast.LENGTH_LONG).show();
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            Toast.makeText(this, "Unable to save file.", Toast.LENGTH_LONG).show();
            ioe.printStackTrace();
        }
    }

    private void loadAutograph() {
        String filename = "autograph.svg";
        String svg = readFile(filename);
        if (svg == null) {
            return;
        }

        Toast.makeText(this, svg, Toast.LENGTH_LONG).show();
        Log.i(TAG, svg);
        int pointsAttributeIdx = svg.indexOf("points=\"");
        int pointsListStartIdx = pointsAttributeIdx + "points=\"".length();
        int pointsListEndIdx = svg.indexOf("\"", pointsListStartIdx);
        Log.i(TAG, pointsAttributeIdx +  " " + pointsListStartIdx + " " + pointsListEndIdx);
        String pointsList = svg.substring(pointsListStartIdx, pointsListEndIdx);

        //TODO!- FOR DEBUGGING
        Toast.makeText(this, pointsList, Toast.LENGTH_LONG).show();

        // "pointsList" now consists of a list of coordinates.
        // Separated by whitespace, each pair of coordinates is of the form "x,y", where x and y are integers.
        String[] arrayOfCoordinates = pointsList.split(" ");
        for (String currentCoordinates : arrayOfCoordinates) {
            // It is possible that some of the elements in the array are just whitespace. Let's avoid that.
            Log.i(TAG, "coordinates: [" + currentCoordinates +"]");
            String trimmedCoordinates = currentCoordinates.trim();
            if (trimmedCoordinates.length() > 0)
            {
                String[] coordinatePair = trimmedCoordinates.split(",");
                //TODO!+ Handle the case that coordinatePair.length != 2
                int x = Integer.parseInt(coordinatePair[0]); //TODO!+ Handle parser failure
                int y = Integer.parseInt(coordinatePair[1]); //TODO!+ Handle parser failure
                addPoint(new Point(x,y)); //TODO?~ Make a list first, and only if everything goes right, display it?
                //TODO!+ After all points are added, force a refresh ?
            }
        }

    }

    //TODO?~ Use an Optional?
    /**
     * Read the text file with the given name into a String.
     * @param filename Name of the text file.
     * @return The contents of the text file, or <code>null</code> if something went wrong.
     */
    private String readFile(String filename) {
        StringBuilder res = new StringBuilder();
        try (FileInputStream fis = this.openFileInput(filename)) {
            int ch;
            while (true){
                ch = fis.read();
                if (ch == -1)
                    break;
                res.append((char) ch);
            }

            //TODO!- FOR TESTING
            Toast.makeText(this, res, Toast.LENGTH_LONG).show();

            return res.toString();
        } catch (FileNotFoundException fnfe) {
            Toast.makeText(this, "Cannot open file \"" + filename + "\"", Toast.LENGTH_LONG).show();
            fnfe.printStackTrace();
            return null;
        } catch (IOException ioe) {
            Toast.makeText(this, "Unable to load file \"" + filename + "\".", Toast.LENGTH_LONG).show();
            ioe.printStackTrace();
            return null;
        }
    }


    private String buildSvg( ) {
        List<Point> points = getPoints();

        Point leftBottom = this.maxPoint(points);
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
    private StringBuilder determinePointsAttribute(List<Point> points) {
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
    private Point maxPoint(List<Point> list) {
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point point : list) {
            if (point.x > maxX) { maxX = point.x; }
            if (point.y > maxY) { maxY = point.y; }
        }
        return new Point(maxX, maxY);
    }
}

interface ListOfPoints {
    void addPoint(Point p);
    List<Point> getPoints();
}