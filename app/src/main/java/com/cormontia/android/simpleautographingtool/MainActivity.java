package com.cormontia.android.simpleautographingtool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
        int itemId = item.getItemId();
        switch (itemId)
        {
            case R.id.save:
                //TODO!+
                break;
            case R.id.share:
                //TODO!+
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAutograph()
    {
        File file = new File(this.getFilesDir(), "autograph.svg");
        //TODO!+ Open file, get data from a ViewModel, write it as SVG, close file.
    }
}

interface ListOfPoints {
    void addPoint(Point p);
    List<Point> getPoints();
}