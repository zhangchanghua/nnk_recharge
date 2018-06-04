package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.utils.SharedPreUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ComfirmPhoneActivity extends BaseActivity {

    @BindView(R.id.phone)
    TextView phone;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_phone;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_phone);
    }

    @OnClick({R.id.submit})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.submit) {
            startActivity(new Intent(this, ModifyPhoneActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        phone.setText(SharedPreUtils.get(this, SharedPreUtils.KEY_PHONE));
    }
}
