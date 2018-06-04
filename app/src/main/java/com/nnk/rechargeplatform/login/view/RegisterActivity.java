package com.nnk.rechargeplatform.login.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.login.presenter.RegisterPresenter;
import com.nnk.rechargeplatform.utils.TextWatcherAdater;
import com.nnk.rechargeplatform.widget.TimerView;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.phone)
    EditText phoneEdit;
    @BindView(R.id.sms_code)
    EditText smsEdit;
    @BindView(R.id.phone_clear)
    ImageButton phoneClear;
    @BindView(R.id.pass)
    EditText passEdit;
    @BindView(R.id.pass_eye)
    CheckBox passEye;
    @BindView(R.id.get_sms)
    TimerView timerView;
    @BindView(R.id.register)
    Button register;
    private RegisterPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.register);

        phoneEdit.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                phoneClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                timerView.setEnabled(s.length() > 0);
                checkRegisterEnable();
            }
        });
        smsEdit.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkRegisterEnable();
            }
        });
        passEdit.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkRegisterEnable();
            }
        });
        passEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    passEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                passEdit.setSelection(passEdit.getText().length());
            }
        });
        presenter = new RegisterPresenter(this);
    }

    private void checkRegisterEnable() {
        String phone = phoneEdit.getText().toString().trim();
        String code = smsEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();
        register.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(pass));

    }

    @OnClick({R.id.phone_clear, R.id.get_sms, R.id.register})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.phone_clear) {
            phoneEdit.setText("");
        } else if (id == R.id.get_sms) {
            String phone = phoneEdit.getText().toString().trim();
            presenter.getSms(phone, timerView);
        } else if (id == R.id.register) {
            String phone = phoneEdit.getText().toString().trim();
            String code = smsEdit.getText().toString().trim();
            String pass = passEdit.getText().toString().trim();
            presenter.register(phone, code, pass);
        }
    }

}
