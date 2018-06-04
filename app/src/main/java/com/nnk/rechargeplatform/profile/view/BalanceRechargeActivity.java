package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.base.BaseOnClickListener;
import com.nnk.rechargeplatform.profile.model.BankM;
import com.nnk.rechargeplatform.profile.presenter.BalanceRechargePresenter;
import com.nnk.rechargeplatform.utils.BankListManager;
import com.nnk.rechargeplatform.utils.TextWatcherAdater;
import com.nnk.rechargeplatform.widget.GlideCircleTransform;

import butterknife.BindView;
import butterknife.OnClick;

public class BalanceRechargeActivity extends BaseActivity {

    @BindView(R.id.money)
    EditText money;
    @BindView(R.id.pass)
    EditText pass;
    BalanceRechargePresenter presenter;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.bank_title)
    TextView bankTitle;
    @BindView(R.id.bank_subtitle)
    TextView bankSubtitle;
    @BindView(R.id.add_bank_wrap)
    RelativeLayout addBankWrap;
    @BindView(R.id.bank_wrap)
    RelativeLayout bankWrap;
    @BindView(R.id.submit)
    Button submit;
    String bankId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_balance_recharge;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_recharge);
        setRightText(R.string.profile_cost_stander);
        setRightClickLister(new BaseOnClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
            }
        });
        money.addTextChangedListener(tw);
        pass.addTextChangedListener(tw);
        bankTitle.addTextChangedListener(tw);
        presenter = new BalanceRechargePresenter(this);

    }

    TextWatcher tw = new TextWatcher();

    class TextWatcher extends TextWatcherAdater {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            String bank = bankTitle.getText().toString();
            String moneyStr = money.getText().toString();
            String passStr = pass.getText().toString();
            submit.setEnabled(!TextUtils.isEmpty(bank) && !TextUtils.isEmpty(moneyStr) && !TextUtils.isEmpty(passStr));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getBankList();
    }

    public void setCurentBank(BankM bank) {
        bankId = bank.bankId;
        bankTitle.setText(bank.bankName);
        bankSubtitle.setText(getString(R.string.profile_bank_card_end) + bank.idNum);
        Glide.with(this).load(BankListManager.get().getBankIconById(this, bank.bankId))
                .apply(new RequestOptions().transform(GlideCircleTransform.get())).into(icon);
    }

    public void setBankStatus(boolean hasBank) {
        addBankWrap.setVisibility(hasBank ? View.GONE : View.VISIBLE);
        bankWrap.setVisibility(!hasBank ? View.GONE : View.VISIBLE);
    }

    @OnClick({R.id.bank_wrap, R.id.submit, R.id.add_bank_wrap})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.submit) {
            presenter.submit();
        } else if (id == R.id.bank_wrap) {
            presenter.selectBank();
        } else if (id == R.id.add_bank_wrap) {
            startActivity(new Intent(this, AddCardActivity.class));
        }
    }

}
