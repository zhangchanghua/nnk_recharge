package com.nnk.rechargeplatform.recharge.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseFragment;
import com.nnk.rechargeplatform.recharge.presenter.RechargeQQmonthPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class RechargeQQmonthFragment extends BaseFragment {

    @BindView(R.id.qq)
    EditText qq;
    @BindView(R.id.clear_phone)
    ImageView clearPhone;
    @BindView(R.id.pay)
    Button pay;
    @BindView(R.id.month_type_value)
    TextView monthTypeValue;
    @BindView(R.id.month_count_value)
    TextView monthCountValue;
    private RechargeQQmonthPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recharge_qq_month;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new RechargeQQmonthPresenter(this);
        presenter.init();
    }


    @Override
    @OnClick({R.id.month_type_value, R.id.month_count_value})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.month_type_value) {
            presenter.selectMonthType(monthTypeValue);
        } else if (id == R.id.month_count_value) {
            presenter.selectMonthCount(monthCountValue);
        }

    }

}
