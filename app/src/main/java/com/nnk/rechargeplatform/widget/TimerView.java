package com.nnk.rechargeplatform.widget;


import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.nnk.rechargeplatform.R;

public class TimerView extends AppCompatTextView {
    private int times = 60;//默认60s

    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void start() {
        this.setEnabled(false);
        postDelayed(task, 0);
    }

    public void stop() {
        setEnabled(true);
        removeCallbacks(task);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(task);
    }

    Runnable task = new Runnable() {
        @Override
        public void run() {
            if (times == 0) {
                TimerView.this.setEnabled(true);
                setText(R.string.get_sms);
                setTextColor(getResources().getColor(R.color.colorAccent));
                times = 60;
            } else {
                times--;
                setText(getResources().getString(R.string.time_count, times));
                postDelayed(this, 1000);
                setTextColor(getResources().getColor(R.color.gray));
            }
        }
    };
}
