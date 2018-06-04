package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class BalanceActivity extends BaseActivity {

    @BindView(R.id.balance)
    TextView balance;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_balance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.header).setBackgroundColor(getResources().getColor(R.color.colorAccent));
        titleView.setText(R.string.profile_balance);
    }

    @OnClick({R.id.submit})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.submit) {
            startActivity(new Intent(this, BalanceRechargeActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
