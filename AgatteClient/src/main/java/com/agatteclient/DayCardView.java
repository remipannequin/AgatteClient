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
    private final float line_width;
    private float text_width = 0;
    private final float text_height;
    private final int line_color;
    private final int text_duration_color;
    private final int text_event_color;
    private final int duration_color;
    private final int mandatory_color;
    private final int min;
    private final int max;
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
            min = a.getInteger(R.styleable.DayCardView_dayStartHour, 7);
            max = a.getInteger(R.styleable.DayCardView_dayEndHour, 19);

            //TODO: add all style attributes
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
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 100;
        int desiredHeight = 900;

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


    }

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
        float block = bounds.height() / (max - min + 1);

        //Draw mandatory periods
        canvas.drawRect(bounds.left + text_width + 10f, getYFromHour(9f, block), bounds.right, getYFromHour(11f, block), mandatory_paint);
        canvas.drawRect(bounds.left + text_width + 10f, getYFromHour(14f, block), bounds.right, getYFromHour(16f, block), mandatory_paint);

        //Draw hour lines

        for (int i = 0; i <= max - min; i++) {
            float y = block * (i + 0.5f) + bounds.top;
            canvas.drawLine(bounds.left + text_width + 10f, y, bounds.right, y, line_paint);
        }
        //Draw min and max
        if (Math.abs(min - p_min) > 0.5) {
            cal.set(Calendar.HOUR_OF_DAY, min);
            cal.set(Calendar.MINUTE, 0);
            canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (bounds.top + 0.5 * block + (text_height) / 2f - line_width), line_text_paint);
        }
        if (Math.abs(max - p_max) > 0.5) {
            cal.set(Calendar.HOUR_OF_DAY, max);
            cal.set(Calendar.MINUTE, 0);
            canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (bounds.top + (max - min + 0.5) * block + (text_height) / 2f - line_width), line_text_paint);
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
                top = getYFromHour(di, block);
                bottom = getYFromHour(df, block);
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

    private float getYFromHour(Date d, float block) {
        cal.setTime(d);
        float h = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60f;
        return getYFromHour(h, block);
    }

    private float getYFromHour(float h, float block) {

        if (h <= (min - 0.5)) {
            return 0f;
        }
        if (h >= (max + 0.5)) {
            return (max - min + 1) * block;
        }
        return ((h - min) + 0.5f) * block;
    }


    public void setCard(DayCard card) {
        if (card != this.card) {
            this.card = card;
            invalidate();
            requestLayout();
        }
    }
}
