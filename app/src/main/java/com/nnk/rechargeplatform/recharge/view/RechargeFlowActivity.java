package com.nnk.rechargeplatform.recharge.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.recharge.presenter.RechargeFlowPresenter;

import butterknife.BindView;

public class RechargeFlowActivity extends BaseActivity {
    @BindView(R.id.clear)
    ImageView clear;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private RechargeFlowPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge_phone;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.recharge_flow);
        presenter = new RechargeFlowPresenter(this);
        presenter.init(recyclerView);
    }

    @Override
    protected void onClick(View v) {
        super.onClick(v);
    }
}
