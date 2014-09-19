/*
 * This file is part of AgatteClient.
 *
 * AgatteClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AgatteClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2014 Rémi Pannequin (remi.pannequin@gmail.com).
 */

/*This file is part of AgatteClient.

    AgatteClient is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AgatteClient is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.*/

package com.agatteclient.card;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;

import com.agatteclient.R;
import com.agatteclient.alarm.AlarmRegistry;
import com.agatteclient.alarm.db.AlarmContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Display a DayCard.
 * <p/>
 * Created by Rémi Pannequin on 04/10/13.
 */
public class DayCardView extends View {

    private static final float ODD_H = 7.5f;
    private static final float MAX_SCALE = 2.0f;
    private static final float MIN_SCALE = 0.5f;
    private final float line_width;
    private final int hourIncrement;
    private final float hourHeight;
    private final float min;
    private final int duration_virtual_color;
    private final int hatchingColor;
    private final float text_height;
    private final int line_color;
    private final int text_duration_color;
    private final int text_event_color;
    private final int duration_color;
    private final int mandatory_color;
    private final int alarm_color;
    private final String date_fmt;
    private final Calendar cal = Calendar.getInstance();
    private float text_width = 0;
    private DayCard card;
    private Paint line_paint;
    private RectF bounds;
    private Paint event_text_paint;
    private SimpleDateFormat fmt;
    private Paint line_text_paint;
    private Paint mandatory_paint;
    private Paint event_paint;
    private Paint event_virtual_paint;
    private Paint duration_text_paint;
    private Paint duration_text_bold_paint;
    private Path odd_path;
    private Path alarm_path;
    private float rect_width;
    private float margin;
    private float block;
    private float scale;
    private Paint hatching_paint;
    private Paint alarm_paint;
    private Paint alarm_text_paint;
    private Paint alarm_fill_paint;
    private Iterable<AlarmRegistry.RecordedAlarm> alarms;


    public DayCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources.Theme theme = context.getTheme();
        assert theme != null;
        TypedArray a = theme.obtainStyledAttributes(
                attrs,
                R.styleable.DayCardView,
                0, 0);
        assert a != null;
        try {

            text_height = a.getDimension(R.styleable.DayCardView_textHeight, 20);
            String fmt = a.getString(R.styleable.DayCardView_timeFormat);//, "hh:mm");
            if (fmt == null) {
                date_fmt = context.getString(R.string.hour_format);
            } else {
                date_fmt = fmt;
            }
            line_color = a.getColor(R.styleable.DayCardView_hourLineColor, Color.LTGRAY);
            line_width = a.getDimension(R.styleable.DayCardView_hourLineWidth, 2f);
            text_duration_color = a.getColor(R.styleable.DayCardView_textDurationColor, Color.WHITE);
            text_event_color = a.getColor(R.styleable.DayCardView_textEventTimeColor, Color.BLACK);
            duration_color = a.getColor(R.styleable.DayCardView_durationColor, Color.BLUE);
            duration_virtual_color = a.getColor(R.styleable.DayCardView_durationVirtualColor, Color.BLUE);
            mandatory_color = a.getColor(R.styleable.DayCardView_mandatoryColor, Color.RED);
            alarm_color = a.getColor(R.styleable.DayCardView_alarmColor, Color.BLACK);
            min = a.getInteger(R.styleable.DayCardView_dayStartHour, 8);
            hourIncrement = a.getInteger(R.styleable.DayCardView_hourIncrement, 3);
            hourHeight = a.getDimension(R.styleable.DayCardView_hourHeight, 25);
            hatchingColor = a.getColor(R.styleable.DayCardView_hatchingColor, Color.WHITE);

        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        fmt = new SimpleDateFormat(date_fmt);

        line_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        line_paint.setColor(line_color);
        line_paint.setStrokeWidth(pxToDp(line_width));
        line_paint.setStyle(Paint.Style.STROKE);
        line_paint.setStrokeJoin(Paint.Join.ROUND);
        line_paint.setTextSize(text_height);

        line_text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        line_text_paint.setColor(line_color);
        line_text_paint.setStyle(Paint.Style.STROKE);
        line_text_paint.setStrokeJoin(Paint.Join.ROUND);
        line_text_paint.setTextSize(text_height);

        event_text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        event_text_paint.setTextSize(text_height);
        event_text_paint.setColor(text_event_color);

        event_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        event_paint.setStyle(Paint.Style.FILL);
        event_paint.setColor(duration_color);

        event_virtual_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        event_virtual_paint.setStyle(Paint.Style.FILL);
        event_virtual_paint.setColor(duration_virtual_color);

        duration_text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        duration_text_paint.setTextSize(text_height);
        duration_text_paint.setColor(text_duration_color);


        duration_text_bold_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        duration_text_bold_paint.setTextSize(text_height);
        duration_text_bold_paint.setColor(text_duration_color);
        duration_text_bold_paint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);


        mandatory_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mandatory_paint.setStyle(Paint.Style.FILL);
        mandatory_paint.setColor(mandatory_color);

        alarm_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        alarm_paint.setStyle(Paint.Style.STROKE);
        alarm_paint.setColor(alarm_color);
        alarm_paint.setStrokeWidth(pxToDp(line_width/2));
        alarm_paint.setStrokeJoin(Paint.Join.ROUND);

        alarm_text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        alarm_text_paint.setTextSize(text_height);
        alarm_text_paint.setColor(alarm_color);
        alarm_text_paint.setStyle(Paint.Style.FILL);

        alarm_fill_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        alarm_fill_paint.setStyle(Paint.Style.FILL);
        alarm_fill_paint.setColor(Color.WHITE);


        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 22);
        text_width = event_text_paint.measureText(fmt.format(cal.getTime()));

        scale = 1.0f;

        BitmapShader hatchingShader = new BitmapShader(makeHatchingBitmap(),
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        hatching_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hatching_paint.setShader(hatchingShader);


    }

    private Bitmap makeHatchingBitmap() {

        float density = getResources().getDisplayMetrics().density;
        int size = (int) ((24 * density) + 0.5f);
        int stroke = size / 8;
        Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(this.hatchingColor);

        p.setStrokeWidth(stroke * 2);
        float[] pts = new float[4];
        pts[0] = 0;
        pts[1] = 0;
        pts[2] = size;
        pts[3] = size;
        c.drawLines(pts, p);
        pts[0] = -stroke;
        pts[1] = size - stroke;
        pts[2] = stroke;
        pts[3] = size + stroke;
        c.drawLines(pts, p);
        pts[0] = size - stroke;
        pts[1] = -stroke;
        pts[2] = size + stroke;
        pts[3] = stroke;
        c.drawLines(pts, p);
        c.clipRect(0, 0, size, size);
        return bm;
    }

    private int dpToPx(float dp) {
        Resources r = getResources();
        assert r != null;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private float pxToDp(float px) {
        Resources r = getResources();
        assert r != null;
        float scale = r.getDisplayMetrics().density;
        return (px * scale);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float desiredHeight_dip = hourHeight * 25 * MAX_SCALE;//take max scaling into account
        int desiredWidth = 200;
        int desiredHeight = dpToPx(desiredHeight_dip);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        // Account for padding
        float xPad = (float) (getPaddingLeft() + getPaddingRight());
        float yPad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xPad;
        float hh = (float) h - yPad;
        bounds = new RectF(xPad, yPad, ww, hh);

        margin = bounds.left + text_width + 10f;
        rect_width = bounds.width() - text_width - 10f;

        odd_path = new Path();
        alarm_path = new Path();
        updateBlock();
    }

    /**
     * @return the Y coordinate (in pixel) of the first punch or now
     */
    public int getFirstPunchY() {
        if (card != null) {
            float y_dp;
            Date min_d = card.getFirstPunch();

            if (min_d == null) {
                y_dp = getYFromHour(min);
            } else {
                y_dp = getYFromHour(min_d);
            }
            return (int) (y_dp - block / 2);

        } else {
            return 0;
        }
    }

    public void setAlarms(Iterable<AlarmRegistry.RecordedAlarm> alarms) {
        this.alarms = alarms;
        invalidate();
        requestLayout();
    }

    /**
     * Draw the view
     *
     * @param canvas where to draw
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw Card event on the canvas
        float p_min = 100;
        float p_max = -100;
        if (card != null) {
            for (Date punch : card.getPunches()) {
                cal.setTime(punch);
                float h = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60f;
                if (h < p_min) {
                    p_min = h;
                }
                if (h > p_max) {
                    p_max = h;
                }
            }
        }


        //Draw mandatory periods
        canvas.drawRect(bounds.left + text_width + 10f, getYFromHour(DayCard.MORNING_START), bounds.right, getYFromHour(DayCard.MORNING_END), mandatory_paint);
        canvas.drawRect(bounds.left + text_width + 10f, getYFromHour(DayCard.AFTERNOON_START), bounds.right, getYFromHour(DayCard.AFTERNOON_END), mandatory_paint);

        //Draw hour lines
        for (int i = 0; i <= 24; i++) {
            float y = block * (i + 0.5f) + bounds.top;
            canvas.drawLine(bounds.left + text_width + 10f, y, bounds.right, y, line_paint);
        }

        //Draw hour every 3h
        //Manage "collisions" i.e. don't draw hour if a punch is near
        for (int h = 0; h <= 24; h += hourIncrement) {
            float min_diff = 25;
            //Get the punch date nearest from h
            if (card != null) {
                //using this method, if odd, now is added the list of punches
                for (Date punch_d : card.getAllPunches()) {
                    cal.setTime(punch_d);
                    float d_h = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60f;
                    float diff = Math.abs(d_h - h);
                    if (diff < min_diff) {
                        min_diff = diff;
                    }
                }
            }
            //Draw only if there is more than 30 min between
            if (min_diff > 0.5) {
                cal.set(Calendar.HOUR_OF_DAY, h);
                cal.set(Calendar.MINUTE, 0);
                float y = getYFromHour(h);
                canvas.drawText(fmt.format(cal.getTime()), bounds.left, y + (text_height) / 2f - line_width, line_text_paint);
            }
        }

        //Draw punches
        if (card != null) {

            drawPeriods(canvas, card.getVirtualPunches(), true);
            float now = drawPeriods(canvas, card.getPunches(), false);

            //draw hatching over corrected periods
            Pair<Date, Date>[] corrected_punches = card.getCorrectedPunches();
            Date di, df;
            float top, bottom;
            for (Pair<Date, Date> p : corrected_punches) {
                di = p.first;
                df = p.second;
                top = getYFromHour(di);
                bottom = getYFromHour(df);
                canvas.drawRect(margin, top, bounds.right, bottom, hatching_paint);
            }

            //change drawing if odd to indicate that the time is running
            if (card.isOdd()) {

                odd_path.reset();
                float required_h = pxToDp(ODD_H);
                int num = (int) Math.floor(rect_width / (2 * required_h));
                float real_h = rect_width / (2 * num);
                odd_path.rLineTo(0, real_h);
                for (int i = 0; i < num; i++) {
                    odd_path.rLineTo(real_h, -real_h);
                    odd_path.rLineTo(real_h, real_h);
                }
                odd_path.rLineTo(0, -real_h);
                odd_path.close();
                odd_path.offset(margin, now);
                canvas.drawPath(odd_path, event_paint);
            }
        }

        //Draw alarms
        if (alarms != null) {
            /* done & failed alarms */
            for (AlarmRegistry.RecordedAlarm a : alarms) {
                drawAlarm(canvas, a.date_executed, a.constraint, a.status);
            }
        }
    }

    /**
     * @param canvas  the canvas to draw into
     * @param punches an array of Dates
     * @param virtual if true, the event_virtual_paint is used
     * @return the bottom of the last period
     */
    private float drawPeriods(Canvas canvas, Date[] punches, boolean virtual) {
        Date di, df;
        float top, bottom = 0;
        Paint p = (virtual ? event_virtual_paint : event_paint);

        for (int i = 0; i < (punches.length + 1) / 2; i++) {
            di = punches[2 * i];
            if ((2 * i + 1) < punches.length) {
                df = punches[2 * i + 1];//even
            } else {
                df = card.now();//odd case
            }
            top = getYFromHour(di);
            bottom = getYFromHour(df);

            cal.setTime(di);
            canvas.drawText(fmt.format(cal.getTime()), bounds.left, (top + (text_height) / 2f - line_width), event_text_paint);
            cal.setTime(df);
            if (Math.abs(di.getTime() - df.getTime()) > (1000 * 60 * 15)) {//15min
                canvas.drawText(fmt.format(cal.getTime()), bounds.left, (bottom + (text_height) / 2f - line_width), event_text_paint);
            }
            canvas.drawRect(margin, top, bounds.right, bottom, p);
            //only display duration text if there is enough space
            // i.e. (bottom - top) > text_height + 4
            if ((bottom - top) > (text_height + 4)) {
                //compute hours and minute difference
                int h = (int) (df.getTime() - di.getTime()) / (1000 * 60 * 60);
                int m = (int) ((df.getTime() - di.getTime()) / (1000 * 60)) % 60;
                float w1 = duration_text_paint.measureText(String.format(
                        getContext().getString(R.string.duration_hour),
                        h));
                float w2 = duration_text_paint.measureText(String.format(
                        getContext().getString(R.string.duration_minute),
                        m));
                canvas.drawText(
                        String.format(getContext().getString(R.string.duration_hour), h),
                        (rect_width - w1 - w2) / 2f + margin, (bottom - top + text_height) / 2f + top,
                        duration_text_bold_paint);
                if (m != 0) {
                    canvas.drawText(
                            String.format(getContext().getString(R.string.duration_minute), m),
                            (rect_width - w1 - w2) / 2f + margin + w1, (bottom - top + text_height) / 2f + top,
                            duration_text_paint);
                }
            }
        }
        return bottom;
    }

    /**
     * Draw an ScheduledAlarm on the View
     * @param canvas the canvas where to draw
     * @param alarm
     * @param type
     */
    private void drawAlarm(Canvas canvas, Date alarm, AlarmContract.Constraint type, AlarmContract.ExecStatus status) {
        //get the y coordinate where to draw
        float y = getYFromHour(alarm);
        String t = fmt.format(alarm);
        //the width of the tag
        //measure text, take min
        float t_width = duration_text_paint.measureText(t);
        float h = text_height + 10;
        float w = t_width + h + 5;
        float pad = dpToPx(5);

        alarm_path = new Path();
        alarm_path.reset();
        alarm_path.rLineTo(0,                         0);
        alarm_path.rLineTo(rect_width - w - (h / 2) - pad,  0);
        alarm_path.rLineTo(h / 2,                     h/2);
        alarm_path.rLineTo(w,                         0);
        alarm_path.rLineTo(0,                         -h);
        alarm_path.rLineTo(- w,                       0);
        alarm_path.rLineTo(-(h / 2),                  h/2);
        alarm_path.rLineTo(-rect_width + w + (h / 2) + pad, 0);
        alarm_path.close();
        alarm_path.offset(margin, y);
        canvas.drawPath(alarm_path, alarm_fill_paint);
        canvas.drawPath(alarm_path, alarm_paint);

        Rect bounds = new Rect();
        duration_text_paint.getTextBounds(t, 0, t.length(), bounds);
        canvas.drawText(t, rect_width - pad, y+(bounds.bottom-bounds.top)/2, alarm_text_paint);

        int ic;
        switch(status) {
            case SUCCESS:
                ic = R.drawable.ic_navigation_accept;
                break;
            case FAILURE:
                   ic = R.drawable.ic_alerts_and_states_warning;
                break;
            case SCHEDULED:
               ic = R.drawable.ic_device_access_alarms;
                break;
            default:
                ic = R.drawable.ic_alerts_and_states_warning;
        }


        Bitmap b = BitmapFactory.decodeResource(getResources(), ic);

        canvas.drawBitmap(Bitmap.createScaledBitmap(b, (int)text_height, (int)text_height, false),
                          rect_width - w + margin - pad,
                          y - (h/2) + 5,
                          alarm_paint);

        //display a jagged line to show arrival/leaving constraints
        if (type == AlarmContract.Constraint.arrival || type == AlarmContract.Constraint.leaving) {
            Path alarm_constr_path = new Path();
            alarm_constr_path.reset();
            float required_h = pxToDp(ODD_H / 2);
            int num = (int) Math.floor((rect_width - w - (h / 2) - pad) / (2 * required_h));
            float real_h = (rect_width - w - (h / 2) - pad) / (2 * num);
            int f = (type == AlarmContract.Constraint.leaving ? -1 : 1);
            for (int i = 0; i < num; i++) {
                alarm_constr_path.rLineTo(real_h, -f * real_h);
                alarm_constr_path.rLineTo(real_h, f * real_h);
            }

            alarm_constr_path.close();
            alarm_constr_path.offset(margin, y);
            canvas.drawPath(alarm_constr_path, alarm_text_paint);
        }
    }

    /**
     * Return the Y coordinate corresponding to a hour
     *
     * @param d a Date instance
     * @return the Y coordinate corresponding to the date
     */
    private float getYFromHour(Date d) {
        cal.setTime(d);
        float h = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60f;
        return getYFromHour(h);
    }

    /**
     * Return the Y coordinate corresponding to a hour
     *
     * @param h the hour
     * @return the Y coordinate corresponding to the date
     */
    private float getYFromHour(float h) {

        if (h <= -0.5) {
            return 0f;
        }
        if (h >= (24 + 0.5)) {
            return 25 * block;
        }
        return (h + 0.5f) * block;
    }

    /**
     * Set the day card to display. invalidate the view if the card has changed
     *
     * @param card the new DayCard
     */
    public void setCard(DayCard card) {
        if (card != this.card) {
            this.card = card;
            invalidate();
            requestLayout();
        }
    }

    /**
     * Change the scale of the view according to factor
     *
     * @param mScaleFactor the scaling factor to apply
     */
    public void applyScale(float mScaleFactor) {
        if ((mScaleFactor > 1 && scale <= MAX_SCALE) || (mScaleFactor < 1 && scale >= MIN_SCALE)) {
            scale *= mScaleFactor;
        }
        updateBlock();
    }

    private void updateBlock() {
        block = bounds.height() * scale / (MAX_SCALE * 25f);
    }

    /**
     * Reset the scale of the view
     */
    public void resetScale() {
        scale = 1.0f;
        updateBlock();
    }
}
