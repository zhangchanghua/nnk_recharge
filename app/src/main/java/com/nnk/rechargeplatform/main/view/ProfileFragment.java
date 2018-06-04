package com.nnk.rechargeplatform.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseFragment;
import com.nnk.rechargeplatform.main.presenter.ProfilePresenter;
import com.nnk.rechargeplatform.profile.view.AuthActivity;
import com.nnk.rechargeplatform.profile.view.BalanceActivity;
import com.nnk.rechargeplatform.profile.view.PayManageActivity;
import com.nnk.rechargeplatform.profile.view.ProfileInfoActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.proxy_grade)
    TextView proxyGrade;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.commission)
    TextView commission;
    @BindView(R.id.extract)
    TextView extract;
    ProfilePresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new ProfilePresenter(this);
    }

    @Override
    @OnClick({R.id.balance_wrap, R.id.commission_wrap, R.id.extract_wrap,
            R.id.info_wrap, R.id.auth_wrap, R.id.pay_wrap, R.id.service_wrap, R.id.logout})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.info_wrap) {
            startActivity(new Intent(getContext(), ProfileInfoActivity.class));
        } else if (id == R.id.auth_wrap) {
            startActivity(new Intent(getContext(), AuthActivity.class));
        } else if (id == R.id.pay_wrap) {
            startActivity(new Intent(getContext(), PayManageActivity.class));
        } else if (id == R.id.service_wrap) {
            WebActivity.openCustomService(this.getActivity());
        } else if (id == R.id.balance_wrap) {
            startActivity(new Intent(getContext(), BalanceActivity.class));
        } else if (id == R.id.commission_wrap) {

        } else if (id == R.id.extract_wrap) {

        } else if (id == R.id.logout) {
            presenter.logout();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.updateInfo(icon, phone, proxyGrade, money, commission, extract);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            presenter.updateInfo(icon, phone, proxyGrade, money, commission, extract);
        }
    }

}