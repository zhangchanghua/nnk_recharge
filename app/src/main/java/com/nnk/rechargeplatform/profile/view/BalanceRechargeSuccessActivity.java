package com.nnk.rechargeplatform.profile.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;

import butterknife.OnClick;

public class BalanceRechargeSuccessActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_balance_recharge_success;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_recharge);
    }

    @OnClick(R.id.submit)
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.submit) {
            finish();
        }
    }

}
