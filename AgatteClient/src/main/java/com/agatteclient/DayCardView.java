package com.agatteclient;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by remi on 04/10/13.
 */
public class DayCardView extends View {

    private float text_width = 0;
    private final float text_height;
    private final int line_color;
    private final int text_duration_color;
    private final int text_event_color;
    private final int duration_color;
    private final int min;
    private final int max;
    private final String date_fmt;

    private DayCard card;
    private Paint duration_paint;
    private Paint line_paint;
    private RectF bounds;
    private Paint event_text_paint;
    private SimpleDateFormat fmt;
    private Calendar cal = Calendar.getInstance();

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
            text_duration_color = a.getColor(R.styleable.DayCardView_textDurationColor, Color.WHITE);
            text_event_color = a.getColor(R.styleable.DayCardView_textEventTimeColor, Color.BLACK);
            duration_color = a.getColor(R.styleable.DayCardView_durationColor, Color.BLUE);
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

        duration_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        duration_paint.setColor(duration_color);
        duration_paint.setStyle(Paint.Style.FILL);

        line_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        line_paint.setColor(line_color);
        line_paint.setStrokeWidth(5f);//TODO: add as styleable
        line_paint.setStyle(Paint.Style.STROKE);
        line_paint.setStrokeJoin(Paint.Join.ROUND);
        line_paint.setTextSize(text_height);

        event_text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        event_text_paint.setTextSize(text_height);
        event_text_paint.setColor(text_event_color);


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
        bounds = new RectF(0, 0, ww, hh);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //TODO: Draw Card event on the canvas

        //Draw hour lines
        float block = bounds.height() / (max - min + 1);
        for (int i = 0; i <= max - min; i++) {
            float y = block * (i + 1 / 2) + bounds.top;
            canvas.drawLine(bounds.left + text_width + 10f, y, bounds.right, y, line_paint);
        }
        //canvas.drawText();
        cal.set(Calendar.HOUR_OF_DAY, min);
        cal.set(Calendar.MINUTE, 0);
        canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (bounds.top + 0.5 * block), line_paint);
        cal.set(Calendar.HOUR_OF_DAY, max);
        cal.set(Calendar.MINUTE, 0);
        canvas.drawText(fmt.format(cal.getTime()), bounds.left, (float) (bounds.bottom - 0.5 * block), line_paint);
    }

    public void setCard(DayCard card) {
        this.card = card;
        invalidate();
        requestLayout();
    }

    public DayCard getCard() {
        return card;
    }
}
