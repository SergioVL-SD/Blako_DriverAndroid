package com.blako.mensajero.Custom;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by franciscotrinidad on 1/6/16.
 */
public class BkoSeekBar extends android.support.v7.widget.AppCompatSeekBar {

    public BkoSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BkoSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BkoSeekBar(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                final int width = getWidth();
                final int available = width - getPaddingLeft() - getPaddingRight();
                int x = (int) event.getX();
                float scale;
                float progress = 0;
                if (x < getPaddingLeft()) {
                    scale = 0.0f;
                } else if (x > width - getPaddingRight()) {
                    scale = 1.0f;
                } else {
                    scale = (float) (x - getPaddingLeft()) / (float) available;
                }
                final int max = getMax();
                progress += scale * max;
                if (progress < 0) {
                    progress = 0;
                } else if (progress > max) {
                    progress = max;
                }

                return Math.abs(progress - getProgress()) < 10 && super.onTouchEvent(event);

            case MotionEvent.ACTION_UP:
                return false;
            default:
                return super.onTouchEvent(event);
        }
    }
}
