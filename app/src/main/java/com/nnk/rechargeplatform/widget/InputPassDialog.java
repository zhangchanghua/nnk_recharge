package com.nnk.rechargeplatform.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.utils.TextWatcherAdater;

public class InputPassDialog extends android.app.ProgressDialog implements View.OnClickListener {
    private EditText pass;
    private CallBack l;

    public InputPassDialog(Context context, CallBack l) {
        super(context, R.style.BottomDialog);
        this.l = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_input_pass, null);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width=context.getResources().getDisplayMetrics().widthPixels;
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        pass = view.findViewById(R.id.pass);
        final View submitView = view.findViewById(R.id.submit);
        pass.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                submitView.setEnabled(s.length() > 0);
            }
        });
        view.findViewById(R.id.back).setOnClickListener(this);
        submitView.setOnClickListener(this);
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            dismiss();
        } else if (id == R.id.submit) {
            if (l != null) {
                l.onInput(pass.getText().toString());
            }
            dismiss();
        }
    }


    public interface CallBack {
        public void onInput(String pass);

    }
}
