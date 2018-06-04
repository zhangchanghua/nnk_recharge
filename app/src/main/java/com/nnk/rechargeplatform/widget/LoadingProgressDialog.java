package com.nnk.rechargeplatform.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.nnk.rechargeplatform.R;

public class LoadingProgressDialog extends android.app.ProgressDialog {
    public LoadingProgressDialog(Context context) {
        super(context,R.style.LoadingDialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context) {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_progress);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
    }
}
