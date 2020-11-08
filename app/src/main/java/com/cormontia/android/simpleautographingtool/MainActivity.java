package com.cormontia.android.simpleautographingtool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListOfPoints {

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
            //TODO!+
        } else if (itemId == R.id.save) {
            //TODO!+
        } else if (itemId == R.id.share) {
            String svg = buildSvg();

            //TODO!- for debugging
            Toast.makeText(this, svg, Toast.LENGTH_LONG).show();

            Intent share = new Intent(Intent.ACTION_SEND);
            //TODO?~ Is this the right type for the Intent...?
            share.setType("image/svg");
            //TODO?~ Is this the right name for the Intent...?
            share.putExtra(Intent.EXTRA_STREAM, svg);

            startActivity(share);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAutograph()
    {
        File file = new File(this.getFilesDir(), "autograph.svg");
        String svg = buildSvg();
        //TODO!+ Open file, get data from a ViewModel, write it as SVG, close file.
    }

    private String buildSvg( ) {
        //TODO!+ Get the proper width and height...
        String res = "<svg width=\"200\" height=\"200\">";
        List<Point> points = getPoints();

        StringBuilder pointsList = new StringBuilder();
        if (points != null) {
            for (Point point : points) {
                pointsList.append(" " + point.x + "," + point.y);
            }
        }

        res += "<polyline";
        res += " points=\""+pointsList+"\"";
        res += " style=\"fill:none;stroke:black;stroke-width:1\"";
        res += "/>";

        res += "</svg>";

        return res;
    }
}

interface ListOfPoints {
    void addPoint(Point p);
    List<Point> getPoints();
}