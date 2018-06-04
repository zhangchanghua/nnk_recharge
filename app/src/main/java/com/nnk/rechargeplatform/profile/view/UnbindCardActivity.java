package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.profile.presenter.UnbindCardPresenter;
import com.nnk.rechargeplatform.utils.BankListManager;
import com.nnk.rechargeplatform.widget.GlideCircleTransform;
import com.nnk.rechargeplatform.widget.InputPassDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class UnbindCardActivity extends BaseActivity {

    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.bank_name)
    TextView bankName;
    @BindView(R.id.bank_detail)
    TextView bankDetail;

    String bankId;
    String accountName;
    UnbindCardPresenter presenter;
    @BindView(R.id.icon)
    ImageView icon;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_unbind_card;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_bank_unbind_card);

        Intent intent = getIntent();
        bankId = intent.getStringExtra("bankId");
        accountName = intent.getStringExtra("accountName");
        bankName.setText(intent.getStringExtra("bankName"));
        bankDetail.setText(BankListManager.get().getTypeNameById(this, bankId) + " **** **** **** " + intent.getStringExtra("idNum"));
        //icon.setImageResource(BankListManager.get().getBankIconById(this,bankId));
        Glide.with(this).load(BankListManager.get().getBankIconById(this, bankId))
                .apply(new RequestOptions().transform(GlideCircleTransform.get())).into(icon);
        presenter = new UnbindCardPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    @OnClick(R.id.submit)
    protected void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.submit) {
            new InputPassDialog(this, new InputPassDialog.CallBack() {
                @Override
                public void onInput(String pass) {
                    presenter.unbind(accountName, pass);
                }
            }).show();
        }

    }

}
