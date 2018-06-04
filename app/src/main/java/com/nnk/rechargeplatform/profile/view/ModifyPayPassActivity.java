package com.nnk.rechargeplatform.profile.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.profile.presenter.ModifyPayPassPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifyPayPassActivity extends BaseActivity implements TextWatcher {
    @BindView(R.id.origin_pass)
    EditText originPass;
    @BindView(R.id.new_pass)
    EditText newPass;
    @BindView(R.id.confirm_pass)
    EditText confirmPass;
    @BindView(R.id.submit)
    Button submit;
    ModifyPayPassPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_pay_pass;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_payment_modify_pass);
        originPass.addTextChangedListener(this);
        newPass.addTextChangedListener(this);
        confirmPass.addTextChangedListener(this);
        presenter = new ModifyPayPassPresenter(this);
    }

    @Override
    @OnClick(R.id.submit)
    protected void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.submit) {
            String origin = originPass.getText().toString().trim();
            String newPassStr = newPass.getText().toString().trim();
            String newPassComfirm = confirmPass.getText().toString().trim();
            presenter.submit(origin,newPassStr,newPassComfirm);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String origin = originPass.getText().toString().trim();
        String newPassStr = newPass.getText().toString().trim();
        String newPassComfirm = confirmPass.getText().toString().trim();
        submit.setEnabled(!TextUtils.isEmpty(origin) && !TextUtils.isEmpty(newPassStr) && !TextUtils.isEmpty(newPassComfirm));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
