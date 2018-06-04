package com.nnk.rechargeplatform.recharge.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.recharge.presenter.RechargeVideoCatePresenter;

import butterknife.BindView;

public class RechargeVideoCateActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private RechargeVideoCatePresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge_video_vip;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.recharge_video_vip);
        presenter = new RechargeVideoCatePresenter(this);
        presenter.init(recyclerView);
    }

    @Override
    protected void onClick(View v) {
        super.onClick(v);
    }
}
