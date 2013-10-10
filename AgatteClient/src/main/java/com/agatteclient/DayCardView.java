package com.agatteclient;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by remi on 04/10/13.
 */
class DayCardView extends View {

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
    private StringBuilder sb1;
    private StringBuilder sb2;


    public DayCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DayCardView,
                0, 0);


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

        sb1 = new StringBuilder();
        sb2 = new StringBuilder();

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

        //TODO: create all the other paints
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
                float h = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE)/60f;
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
        canvas.drawRect(bounds.left + text_width + 10f, getYFromHour(9f,block), bounds.right, getYFromHour(11f, block), mandatory_paint);
        canvas.drawRect(bounds.left + text_width + 10f, getYFromHour(14f,block), bounds.right, getYFromHour(16f, block), mandatory_paint);

        //Draw hour lines

        for (int i = 0; i <= max - min; i++) {
            float y = block * (i + 0.5f) + bounds.top;
            canvas.drawLine(bounds.left + text_width + 10f, y, bounds.right, y, line_paint);
        }
        //Draw min and max
        if (Math.abs(min - p_min) > 0.5) {
            cal.set(Calendar.HOUR_OF_DAY, min);
            cal.set(Calendar.MINUTE, 0);
            canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (bounds.top + 0.5 * block + (text_height)/2f - line_width), line_text_paint);
        }
        if (Math.abs(max - p_max) > 0.5) {
            cal.set(Calendar.HOUR_OF_DAY, max);
            cal.set(Calendar.MINUTE, 0);
            canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (bounds.top + (max - min +0.5) * block + (text_height)/2f - line_width), line_text_paint);
        }
        //Draw tops
        if (card != null) {
            Date di, df;
            boolean odd;

            Iterator<Date> punches = card.getPunches().iterator();

            while (punches.hasNext()) {
                di = punches.next();
                //TODO : only if even
                if (punches.hasNext()) {
                    df = punches.next();//even
                    odd = false;
                } else {
                    df = card.now();//odd case
                    odd = true;
                }
                //If
                float top = getYFromHour(di, block);
                float bottom = getYFromHour(df, block);
                cal.setTime(di);
                canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (top + (text_height) / 2f - line_width), event_text_paint);
                //TODO: change drawing if odd to indicate that the time is running
                cal.setTime(df);
                canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (bottom + (text_height)/2f - line_width), event_text_paint);

                canvas.drawRect(bounds.left + text_width + 10f, top, bounds.right, bottom, event_paint);
                //compute hours and minute difference
                long h = (df.getTime() - di.getTime()) / (1000 * 60 * 60);
                long m = ((df.getTime() - di.getTime()) / (1000 * 60)) % 60;
                sb1.delete(0, sb1.length());
                sb1.append(h).append("h");
                float w1 = duration_text_paint.measureText(sb1.toString());
                sb2.delete(0, sb2.length());
                sb2.append(m);
                float w2 = duration_text_paint.measureText(sb2.toString());
                canvas.drawText(sb1.toString(), (bounds.width() - text_width - 10f - w1 - w2) / 2f + text_width + 10f, (bottom - top + text_height) / 2f + top , duration_text_bold_paint);
                canvas.drawText(sb2.toString(), (bounds.width() - text_width - 10f - w1 - w2) / 2f + text_width + 10f + w1, (bottom - top + text_height) / 2f + top , duration_text_paint);
            }
        }



    }

    private float getYFromHour(Date d, float block) {
        cal.setTime(d);
        float h = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE)/60f;
        return getYFromHour(h, block);
    }

    private float getYFromHour(float h, float block) {

        if (h <= (min - 0.5)) {
            return 0f;
        }
        if (h >= (max +0.5)) {
            return  (max - min + 1) * block;
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

    public DayCard getCard() {
        return card;
    }
}
