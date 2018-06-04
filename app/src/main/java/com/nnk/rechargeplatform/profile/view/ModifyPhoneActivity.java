package com.nnk.rechargeplatform.profile.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.profile.presenter.ModifyPhonePresenter;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.TextWatcherAdater;
import com.nnk.rechargeplatform.widget.TimerView;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifyPhoneActivity extends BaseActivity {

    ModifyPhonePresenter presenter;
    @BindView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;
    @BindView(R.id.origin_phone)
    EditText originPhone;
    @BindView(R.id.new_phone)
    EditText newPhone;
    @BindView(R.id.origin_phone_clear)
    ImageButton originPhoneClear;
    @BindView(R.id.new_phone_clear)
    ImageButton newPhoneClear;
    @BindView(R.id.origin_get_sms)
    TimerView originGetSms;
    @BindView(R.id.new_get_sms)
    TimerView newGetSms;
    @BindView(R.id.origin_sms_code)
    EditText originSmsCode;
    @BindView(R.id.new_sms_code)
    EditText newSmsCode;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_phone;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_modify_phone);
        originPhone.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                originPhoneClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                originGetSms.setEnabled(s.length() > 0);
                checkSubmitEnable();
            }
        });
        originPhone.setText(SharedPreUtils.get(this, SharedPreUtils.KEY_PHONE));
        newPhone.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                newPhoneClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                newGetSms.setEnabled(s.length() > 0);
                checkSubmitEnable();
            }
        });
        originSmsCode.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkSubmitEnable();
            }
        });
        newSmsCode.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkSubmitEnable();
            }
        });
        presenter = new ModifyPhonePresenter(this);
    }

    public void checkSubmitEnable() {
        if (viewSwitcher.getDisplayedChild() == 0) {
            String phoneStr = originPhone.getText().toString().trim();
            String code = originSmsCode.getText().toString().trim();
            submit.setEnabled(!TextUtils.isEmpty(phoneStr) && !TextUtils.isEmpty(code));
        } else {
            String phoneStr = newPhone.getText().toString().trim();
            String code = newSmsCode.getText().toString().trim();
            submit.setEnabled(!TextUtils.isEmpty(phoneStr) && !TextUtils.isEmpty(code));
        }

    }

    @OnClick({R.id.submit,R.id.origin_get_sms,R.id.new_get_sms,R.id.origin_phone_clear,R.id.new_phone_clear})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.origin_get_sms) {
            String phoneStr = originPhone.getText().toString().trim();
            presenter.getSms(phoneStr, originGetSms, false);
        } else if (id == R.id.new_get_sms) {
            String phoneStr = newPhone.getText().toString().trim();
            presenter.getSms(phoneStr, newGetSms, true);
        } else if (id == R.id.submit) {
            String phoneStr;
            String code;
            if (viewSwitcher.getDisplayedChild() == 0) {
                phoneStr = originPhone.getText().toString().trim();
                code = originSmsCode.getText().toString().trim();
                presenter.checkCode(phoneStr, code);
            } else {
                String originPhoneStr = originPhone.getText().toString().trim();
                phoneStr = newPhone.getText().toString().trim();
                code = newSmsCode.getText().toString().trim();
                presenter.submit(originPhoneStr, phoneStr, code);
            }
        }else if(id==R.id.origin_phone_clear){
            originPhone.setText("");
        }else if(id==R.id.new_phone_clear){
            newPhone.setText("");
        }
    }

    public void next() {
        viewSwitcher.setDisplayedChild(1);
    }
}
