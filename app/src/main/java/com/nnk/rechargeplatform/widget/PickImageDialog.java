package com.nnk.rechargeplatform.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;

public class PickImageDialog extends android.app.ProgressDialog implements View.OnClickListener {
    private TextView takePhoto, pickBlbum;
    private CallBack l;

    public PickImageDialog(Context context, CallBack l) {
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
        View view = inflater.inflate(R.layout.dialog_pick_image, null);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setAttributes(lp);
        }
        takePhoto = view.findViewById(R.id.take_photo);
        pickBlbum = view.findViewById(R.id.pick_album);
        takePhoto.setOnClickListener(this);
        pickBlbum.setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        setContentView(view);

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        int id = v.getId();
        if (id == R.id.take_photo) {
            if (l != null) {
                l.onTakePhoto();
            }
        } else if (id == R.id.pick_album) {
            if (l != null) {
                l.onPickBlbum();
            }
        }
    }


    public interface CallBack {
        public void onTakePhoto();

        public void onPickBlbum();
    }
}
