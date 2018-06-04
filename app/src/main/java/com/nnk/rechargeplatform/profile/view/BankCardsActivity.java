package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.profile.presenter.BankCardsPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class BankCardsActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.null_wrap)
    LinearLayout nullWrap;
    @BindView(R.id.submit)
    Button submit;
    BankCardsPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bank_cards;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_payment_card);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        presenter = new BankCardsPresenter(this);
        presenter.init(recyclerView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getBankList();
    }

    @Override
    @OnClick(R.id.submit)
    protected void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.submit) {
            startActivity(new Intent(this, AddCardActivity.class));
        }

    }

    public void setNullViewState(int visiable) {
        nullWrap.setVisibility(visiable);
    }
}
