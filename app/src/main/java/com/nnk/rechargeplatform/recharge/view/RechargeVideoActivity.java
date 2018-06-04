package com.nnk.rechargeplatform.recharge.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.recharge.presenter.RechargeVideoPresenter;

import butterknife.BindView;

public class RechargeVideoActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private RechargeVideoPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge_video;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        titleView.setText(title);
        presenter = new RechargeVideoPresenter(this);
        presenter.init(recyclerView);
    }
}
