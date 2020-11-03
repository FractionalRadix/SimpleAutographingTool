package com.cormontia.android.simpleautographingtool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the Canvas on which the user can draw his/her signature.
 */
public class AutographingView extends View {

    private List<Point> points = new ArrayList<>();

    public AutographingView(Context context) {
        super(context);
        init(null, 0);
    }

    public AutographingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AutographingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        Paint paint = new Paint(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        if (points.size() > 0) {
            Point prev = points.get(0);
            for (int i = 1; i < points.size(); i++) {
                Point cur = points.get(i);
                canvas.drawLine(prev.x, prev.y, cur.x, cur.y, paint);
                prev = cur;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        float px = evt.getX();
        float py = evt.getY();

        points.add(new Point((int) px, (int) py));
        invalidate();
        return true;
    }
}