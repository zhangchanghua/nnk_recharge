package com.nnk.rechargeplatform.login.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.login.presenter.FindPassPresenter;
import com.nnk.rechargeplatform.utils.TextWatcherAdater;
import com.nnk.rechargeplatform.widget.TimerView;

import butterknife.BindView;
import butterknife.OnClick;

public class FindPassActivity extends BaseActivity {
    @BindView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.sms_code)
    EditText smsCode;
    @BindView(R.id.new_pass)
    EditText newPass;
    @BindView(R.id.confirm_pass)
    EditText confirmPass;
    @BindView(R.id.phone_clear)
    ImageButton clearBtn;
    @BindView(R.id.get_sms)
    TimerView getSms;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.pass_tip)
    TextView passTip;
    FindPassPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pass;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.find_password);
        viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        phone.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                clearBtn.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                getSms.setEnabled(s.length() > 0);
                checkSubmitEnable();
            }
        });
        smsCode.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkSubmitEnable();
            }
        });
        newPass.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkSubmitEnable();
            }
        });
        confirmPass.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkSubmitEnable();
            }
        });
        presenter = new FindPassPresenter(this);
    }

    private void checkSubmitEnable() {
        if (viewSwitcher.getDisplayedChild() == 0) {
            String phoneStr = phone.getText().toString().trim();
            String code = smsCode.getText().toString().trim();
            submit.setEnabled(!TextUtils.isEmpty(phoneStr) && !TextUtils.isEmpty(code));
        } else {
            String pass = newPass.getText().toString().trim();
            String confirmPassStr = confirmPass.getText().toString().trim();
            submit.setEnabled(!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirmPassStr));
        }
    }

    public void next() {
        viewSwitcher.setDisplayedChild(1);
        submit.setText(R.string.confirm);
        passTip.setVisibility(View.VISIBLE);
        checkSubmitEnable();
    }

    @OnClick({R.id.get_sms, R.id.phone_clear, R.id.submit})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.get_sms) {
            presenter.getSmsCode(phone.getText().toString().trim(), getSms);
        } else if (id == R.id.phone_clear) {
            phone.setText("");
        } else if (id == R.id.submit) {
            int index = viewSwitcher.getDisplayedChild();
            String phoneStr = phone.getText().toString().trim();
            if (index == 0) {
                String code = smsCode.getText().toString().trim();
                presenter.checkCode(phoneStr, code);
            } else {
                String pass = newPass.getText().toString().trim();
                String confirmPassStr = confirmPass.getText().toString().trim();
                presenter.resetPass(phoneStr, pass, confirmPassStr);
            }
        }
    }
}
