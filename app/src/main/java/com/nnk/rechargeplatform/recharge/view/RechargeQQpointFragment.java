package com.nnk.rechargeplatform.recharge.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseFragment;
import com.nnk.rechargeplatform.recharge.presenter.RechargeQQpointPresenter;

import butterknife.BindView;

public class RechargeQQpointFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.input_wrap)
    RelativeLayout inputWrap;
    private RechargeQQpointPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recharge_qq_point;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new RechargeQQpointPresenter(this);
        presenter.init(recyclerView);
    }

    public void togglePriceInput(int visibility) {
        inputWrap.setVisibility(visibility);
    }

    @Override
    protected void onClick(View v) {
        super.onClick(v);
    }
}
