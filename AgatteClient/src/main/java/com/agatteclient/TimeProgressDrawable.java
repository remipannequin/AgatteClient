package com.agatteclient;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;

/**
 * Created by remi on 14/10/13.
 */
public class TimeProgressDrawable extends Drawable {


    private final Paint paint_empty, paint_goal, paint_done;
    private final int max;
    private final double goal;
    private final int step;
    private final int fact;
    private int level;

    public TimeProgressDrawable(int max, double goal, int fact) {
        this.max = max;
        this.goal = goal;
        this.fact = fact;
        this.step = 10000 / (max/100);


        paint_empty = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_empty.setStyle(Paint.Style.STROKE);
        paint_empty.setStrokeWidth(10);
        paint_empty.setColor(Color.LTGRAY);

        paint_goal = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_goal.setStyle(Paint.Style.STROKE);
        paint_goal.setStrokeWidth(10);
        paint_goal.setColor(Color.parseColor("#AA66CC"));

        paint_done = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_done.setStyle(Paint.Style.STROKE);
        paint_done.setStrokeWidth(10);
        paint_done.setColor(Color.parseColor("#33B5E5"));


    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }



    @Override
    protected boolean onStateChange(int[] state) {
        return super.onStateChange(state);
    }

    @Override
    protected boolean onLevelChange(int level) {
        return super.onLevelChange(level);
    }



    @Override
    public void draw(Canvas canvas) {
        int level = getLevel();

        Rect bound = getBounds();
        RectF oval_bound = new RectF(bound.left + 10, bound.top + 10, bound.right - 10, bound.bottom - 10);
        //canvas.drawRect(bound.top, bound.left, bound.bottom, bound.right, paint);
        //canvas.drawArc(oval_bound, 135, 270, false, paint_bg);

        for (int i = 0; i < 12; i++) {
            canvas.drawArc(oval_bound, 132 + i * 23, 21, false, paint_empty);
        }


        int h2 = (int)Math.floor(goal);
        for (int i = 0; i < h2; i++) {
            canvas.drawArc(oval_bound, 132 + i * 23, 21, false, paint_goal);
        }
        canvas.drawArc(oval_bound, 132 + h2 * 23, (int)(21 * (goal - h2)), false, paint_goal);


        int h = level / (step);
        for (int i = 0; i < h; i++) {
            canvas.drawArc(oval_bound, 132 + i * 23, 21, false, paint_done);
        }
        canvas.drawArc(oval_bound, 132 + h * 23, (21 * (level % (step)))/(step), false, paint_done);


    }
}
