package com.cormontia.android.simpleautographingtool;

import android.graphics.Point;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class AutographViewModel extends ViewModel {
    private MutableLiveData<List<Point>> points = new MutableLiveData<List<Point>>();

    public AutographViewModel() {
        List<Point> data = new ArrayList<>();
        points.setValue(data);
    }

    MutableLiveData<List<Point>> getPoints( ) {
        return points;
    }

    void clearPoints() {
        points.setValue(new ArrayList<Point>());
    }
}
