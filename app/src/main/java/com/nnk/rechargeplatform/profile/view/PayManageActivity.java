package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;

import butterknife.OnClick;

public class PayManageActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_manage;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_payment);
    }

    @OnClick({R.id.card_wrap, R.id.modify_pass})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.card_wrap) {
            startActivity(new Intent(this, BankCardsActivity.class));
        } else if (id == R.id.modify_pass) {
            startActivity(new Intent(this, ModifyPayPassActivity.class));
        }
    }

}
