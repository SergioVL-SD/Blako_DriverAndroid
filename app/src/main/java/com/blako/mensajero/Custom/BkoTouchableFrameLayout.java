package com.blako.mensajero.Custom;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class BkoTouchableFrameLayout extends FrameLayout {
    private OnTouchListener onTouchListener;
    public boolean isTouched;

    public BkoTouchableFrameLayout(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouched = true;
                if (onTouchListener != null)
                    onTouchListener.onTouch();
                break;
            case MotionEvent.ACTION_UP:
                isTouched = false;
                if (onTouchListener != null)
                    onTouchListener.onRelease();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public interface OnTouchListener {
        void onTouch();

        void onRelease();
    }
}
