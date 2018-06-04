package com.nnk.rechargeplatform.login.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.login.presenter.InitPresenter;
import com.nnk.rechargeplatform.login.presenter.LoginPresenter;
import com.nnk.rechargeplatform.main.view.WebActivity;
import com.nnk.rechargeplatform.utils.TextWatcherAdater;
import com.nnk.rechargeplatform.widget.TimerView;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;
    @BindView(R.id.swith_login_style)
    TextView loginType;
    @BindView(R.id.get_sms)
    TimerView getSmsView;
    @BindView(R.id.phone_login_phone)
    EditText phoneLoginPhone;
    @BindView(R.id.phone_login_pass)
    EditText phoneLoginPass;
    @BindView(R.id.sms_login_phone)
    EditText smsLoginPhone;
    @BindView(R.id.sms_login_sms)
    EditText smseLoginSms;
    @BindView(R.id.phone_login_clear)
    ImageButton phoneLoginClear;
    @BindView(R.id.sms_login_clear)
    ImageButton smsLoginClear;
    @BindView(R.id.phone_login_eye)
    CheckBox phoneLoginEye;
    @BindView(R.id.login)
    Button login;

    private LoginPresenter presenter;
    public static final String KEY_SESSION_INVAILD = "sessionInvalied";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.login);
        hideBackView();
        presenter = new LoginPresenter(this);
        viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        phoneLoginPhone.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                phoneLoginClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                checkLoginEnable();
            }
        });
        phoneLoginPass.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkLoginEnable();
            }
        });
        smsLoginPhone.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                smsLoginClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                getSmsView.setEnabled(s.length() > 0);
                checkLoginEnable();
            }
        });
        smseLoginSms.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkLoginEnable();
            }
        });
        phoneLoginEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    phoneLoginPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    phoneLoginPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                phoneLoginPass.setSelection(phoneLoginPass.getText().length());
            }
        });
        if (getIntent().getBooleanExtra(KEY_SESSION_INVAILD, false)) {
            new InitPresenter(this, null).init();
        }
    }

    private void checkLoginEnable() {
        if (viewSwitcher.getDisplayedChild() == 0) {//密码登录
            String phone = phoneLoginPhone.getText().toString().trim();
            String pass = phoneLoginPass.getText().toString().trim();
            login.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pass));
        } else {
            String phone = smsLoginPhone.getText().toString().trim();
            String sms = smseLoginSms.getText().toString().trim();
            login.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(sms));
        }
    }

    @OnClick({R.id.login, R.id.swith_login_style, R.id.find_pass, R.id.get_sms, R.id.register, R.id.contact_service, R.id.phone_login_clear, R.id.sms_login_clear})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.login) {//登录
            if (viewSwitcher.getDisplayedChild() == 0) {//密码登录
                String phone = phoneLoginPhone.getText().toString().trim();
                String pass = phoneLoginPass.getText().toString().trim();
                presenter.loginWithPass(phone, pass);
            } else {//验证码登录
                String phone = smsLoginPhone.getText().toString().trim();
                String sms = smseLoginSms.getText().toString().trim();
                presenter.loginWithSMS(phone, sms);
            }
        } else if (id == R.id.swith_login_style) {//切换登录方式
            if (viewSwitcher.getDisplayedChild() == 0) {
                viewSwitcher.setDisplayedChild(1);
                titleView.setText(R.string.login_sms);
                loginType.setText(R.string.login_pass);
                viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
            } else {
                viewSwitcher.setDisplayedChild(0);
                titleView.setText(R.string.login);
                loginType.setText(R.string.login_sms);
                viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
                viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
            }
            checkLoginEnable();
        } else if (id == R.id.find_pass) {//找回密码
            startActivity(new Intent(this, FindPassActivity.class));
        } else if (id == R.id.get_sms) {//获取验证码
            String phone = smsLoginPhone.getText().toString().trim();
            presenter.getSms(phone, getSmsView);
        } else if (id == R.id.register) {//注册
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (id == R.id.contact_service) {//客服
            WebActivity.openCustomService(this);
        } else if (id == R.id.phone_login_clear) {//清除手机号
            phoneLoginPhone.setText("");
        } else if (id == R.id.sms_login_clear) {//清除sms手机号
            smsLoginPhone.setText("");
        }
    }

}
