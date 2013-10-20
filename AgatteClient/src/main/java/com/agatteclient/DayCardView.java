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

package com.agatteclient;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Display a DayCard.
 * <p/>
 * Created by Rémi Pannequin on 04/10/13.
 */
class DayCardView extends View {

    private static final float ODD_H = 15;
    private static final float MAX_SCALE = 2.0f;
    private static final float MIN_SCALE = 0.5f;
    private final float line_width;
    private final int hourIncrement;
    private final float hourHeight;
    private final float min;
    private float text_width = 0;
    private final float text_height;
    private final int line_color;
    private final int text_duration_color;
    private final int text_event_color;
    private final int duration_color;
    private final int mandatory_color;


    private final String date_fmt;

    private DayCard card;
    private Paint line_paint;
    private RectF bounds;
    private Paint event_text_paint;
    private SimpleDateFormat fmt;
    private Calendar cal = Calendar.getInstance();
    private Paint line_text_paint;
    private Paint mandatory_paint;
    private Paint event_paint;
    private Paint duration_text_paint;
    private Paint duration_text_bold_paint;
    private Path odd_path;
    private float rect_width;
    private float margin;
    private float block;
    private float scale;


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
                date_fmt = "HH:mm";
            } else {
                date_fmt = fmt;
            }
            line_color = a.getColor(R.styleable.DayCardView_hourLineColor, Color.LTGRAY);
            line_width = a.getFloat(R.styleable.DayCardView_hourLineWidth, 5f);
            text_duration_color = a.getColor(R.styleable.DayCardView_textDurationColor, Color.WHITE);
            text_event_color = a.getColor(R.styleable.DayCardView_textEventTimeColor, Color.BLACK);
            duration_color = a.getColor(R.styleable.DayCardView_durationColor, Color.BLUE);
            mandatory_color = a.getColor(R.styleable.DayCardView_mandatoryColor, Color.RED);
            min = a.getInteger(R.styleable.DayCardView_dayStartHour, 8);
            hourIncrement = a.getInteger(R.styleable.DayCardView_hourIncrement, 3);
            hourHeight = a.getDimension(R.styleable.DayCardView_hourHeight, 25);

        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        fmt = new SimpleDateFormat(date_fmt);

        line_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        line_paint.setColor(line_color);
        line_paint.setStrokeWidth(line_width);
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

        cal.set(Calendar.HOUR, 12);
        cal.set(Calendar.MINUTE, 22);
        text_width = event_text_paint.measureText(fmt.format(cal.getTime()));

        scale = 1.0f;
    }

    private int dpToPx(float dp) {
        Resources r = getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float desiredHeight_dip = hourHeight * 25 * MAX_SCALE;//take max scaling into account
        Resources r = getResources();
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;
        bounds = new RectF(xpad, ypad, ww, hh);

        margin = bounds.left + text_width + 10f;
        rect_width = bounds.width() - text_width - 10f;

        odd_path = new Path();
        updateBlock();
    }

    /**
     * Return the Y coordinate (in pixel) of the first punch or now
     * @return
     */
    int getFirstPunchY() {
        if (card != null) {
            float y_dp;
            if (card.getNumberOfPunches() == 0) {
                y_dp = getYFromHour(min);
            } else {
                y_dp = getYFromHour(card.getPunches()[0]);
            }
            return (int)(y_dp - block /2);

        } else {
            return 0;
        }
    }

    float getRequestedHeight() {
        return hourHeight * 25;
    }

    /**
     * Draw the view
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
        canvas.drawRect(bounds.left + text_width + 10f, getYFromHour(9f), bounds.right, getYFromHour(11f), mandatory_paint);
        canvas.drawRect(bounds.left + text_width + 10f, getYFromHour(14f), bounds.right, getYFromHour(16f), mandatory_paint);

        //Draw hour lines
        for (int i = 0; i <= 24; i++) {
            float y = block * (i + 0.5f) + bounds.top;
            canvas.drawLine(bounds.left + text_width + 10f, y, bounds.right, y, line_paint);
        }

        //Draw hour every 3h
        //Manage "collisions" i.e. don't draw hour if a punch is near
        for (int h = 0; h <= 24; h+=hourIncrement) {
            float min_diff = 25;
            //Get the punch date nearest from h
            if (card != null) {
                //using this method, if odd, now is added the list of punches
                for (Date punch_d : card.getPunchesWithNow()) {
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
                canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (y + (text_height) / 2f - line_width), line_text_paint);
            }
        }

        //Draw tops
        if (card != null) {
            Date di, df;
            boolean odd = false;

            Date[] punches = card.getPunches();
            Date[] corrected_punches = card.getCorrectedPunches();

            float top, bottom = 0;

            for (int i = 0; i < (punches.length + 1) / 2; i++) {
                di = punches[2 * i];
                if ((2 * i + 1) < punches.length) {
                    df = punches[2 * i + 1];//even
                } else {
                    df = card.now();//odd case
                    odd = true;
                }
                top = getYFromHour(di);
                bottom = getYFromHour(df);

                cal.setTime(di);
                canvas.drawText(fmt.format(cal.getTime()), bounds.left, (top + (text_height) / 2f - line_width), event_text_paint);
                cal.setTime(df);
                if (Math.abs(di.getTime() - df.getTime()) > (1000 * 60 * 15)) {//15min
                    canvas.drawText(fmt.format(cal.getTime()), bounds.left, (bottom + (text_height) / 2f - line_width), event_text_paint);
                }
                canvas.drawRect(margin, top, bounds.right, bottom, event_paint);
                //only display duration text if there is enough space
                // i.e. (bottom - top) > text_height + 4
                if ((bottom - top) > (text_height + 4)) {
                    //compute hours and minute difference
                    long h = (df.getTime() - di.getTime()) / (1000 * 60 * 60);
                    long m = ((df.getTime() - di.getTime()) / (1000 * 60)) % 60;
                    float w1 = duration_text_paint.measureText(String.format("%dh", h));
                    float w2 = duration_text_paint.measureText(String.format("%02d", m));
                    canvas.drawText(String.format("%dh", h), (rect_width - w1 - w2) / 2f + margin, (bottom - top + text_height) / 2f + top, duration_text_bold_paint);
                    if (m != 0) {
                        canvas.drawText(String.format("%02d", m), (rect_width - w1 - w2) / 2f + margin + w1, (bottom - top + text_height) / 2f + top, duration_text_paint);
                    }
                }
            }
            //change drawing if odd to indicate that the time is running
            if (odd) {

                odd_path.reset();
                float required_h = ODD_H;
                int num = (int) Math.floor(rect_width / (2 * required_h));
                float real_h = rect_width / (2 * num);
                odd_path.rLineTo(0, real_h);
                for (int i = 0; i < num; i++) {
                    odd_path.rLineTo(real_h, -real_h);
                    odd_path.rLineTo(real_h, real_h);
                }
                odd_path.rLineTo(0, -real_h);
                odd_path.close();
                odd_path.offset(margin, bottom);
                canvas.drawPath(odd_path, event_paint);
            }
        }
    }

    /**
     * Return the Y coordinate corresponding to a hour
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
     * @param h the hour
     * @returnthe Y coordinate corresponding to the date
     */
    private float getYFromHour(float h) {

        if (h <= - 0.5) {
            return 0f;
        }
        if (h >= (24 + 0.5)) {
            return 25 * block;
        }
        return (h + 0.5f) * block;
    }

    /**
     * Set the day card to display. invalidate the view if the card has changed
     * @param card
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
     * @param mScaleFactor
     */
    public void applyScale(float mScaleFactor) {
        if ((mScaleFactor > 1 && scale <= MAX_SCALE) || (mScaleFactor < 1 && scale >= MIN_SCALE)) {
            scale *= mScaleFactor;
        }
        updateBlock();
    }

    private void updateBlock() {
        block = bounds.height() * scale / (MAX_SCALE* 25f);
    }

    /**
     * Reset the scale of the view
     */
    public void resetScale() {
        scale = 1.0f;
        updateBlock();
    }
}
