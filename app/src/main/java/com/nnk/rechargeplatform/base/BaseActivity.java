package com.nnk.rechargeplatform.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nnk.rechargeplatform.App;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;
    @Nullable
    @BindView(R.id.title)
    public TextView titleView;
    @Nullable
    @BindView(R.id.right)
    public TextView rightView;
    @Nullable
    @BindView(R.id.back)
    public ImageButton backView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        App.getInstance().addActivity(this);
        if (backView != null) {
            backView.setOnClickListener(new BaseOnClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                   finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        App.getInstance().removeActivity(this);
    }

    protected void hideBackView() {
        if (backView != null) {
            backView.setVisibility(View.GONE);
        }
    }

    protected void setRightText(int resId) {
        if (rightView != null) {
            rightView.setVisibility(View.VISIBLE);
            rightView.setText(resId);
        }
    }

    protected void setRightClickLister(BaseOnClickListener l) {
        if (rightView != null) {
            rightView.setOnClickListener(l);
        }
    }

    protected abstract int getLayoutId();

    //点击效果
    protected void onClick(final View v) {
        ViewUtils.bindClickAnim(v);
    }

    //显示dialog
    public void alertDialog(int resId, DialogInterface.OnClickListener l) {
        alertDialog(getString(resId), l);
    }

    //显示dialog
    public void alertDialog(String msg, DialogInterface.OnClickListener l) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tip)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, l).create().show();
    }
}
