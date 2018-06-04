package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.profile.presenter.AddCardPresenter;
import com.nnk.rechargeplatform.utils.TextWatcherAdater;
import com.nnk.rechargeplatform.widget.BandCardEditText;
import com.nnk.rechargeplatform.widget.TimerView;

import butterknife.BindView;
import butterknife.OnClick;

public class AddCardActivity extends BaseActivity {
    @BindView(R.id.people)
    EditText people;
    @BindView(R.id.id_no)
    EditText idNo;
    @BindView(R.id.bank)
    TextView bank;
    @BindView(R.id.no)
    BandCardEditText no;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.id_no_clear)
    ImageButton idNoClear;
    @BindView(R.id.car_no_clear)
    ImageButton cardNoClear;
    @BindView(R.id.clear)
    ImageButton clear;
    @BindView(R.id.get_sms)
    TimerView getSms;
    @BindView(R.id.sms_code)
    EditText smsCode;
    AddCardPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_card;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_bank_cards_add);
        people.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkSmsEnable();
                checkSubmitEnable();
            }
        });
        phone.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                clear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                checkSmsEnable();
                checkSubmitEnable();
            }
        });
        no.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                cardNoClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                checkSmsEnable();
                checkSubmitEnable();
            }
        });
        idNo.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                idNoClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                checkSmsEnable();
                checkSubmitEnable();
            }
        });
        smsCode.addTextChangedListener(new TextWatcherAdater() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkSmsEnable();
                checkSubmitEnable();
            }
        });
        presenter = new AddCardPresenter(this);
    }

    public void checkSmsEnable() {
        String name = people.getText().toString().trim();
        String id_no = idNo.getText().toString().trim();
        String car_no = no.getBankCardText();
        String mobile = phone.getText().toString().trim();
        getSms.setEnabled(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(id_no) && !TextUtils.isEmpty(car_no) && !TextUtils.isEmpty(mobile));

    }

    public void checkSubmitEnable() {
        String name = people.getText().toString().trim();
        String id_no = idNo.getText().toString().trim();
        String car_no = no.getBankCardText();
        String mobile = phone.getText().toString().trim();
        String code = smsCode.getText().toString().trim();
        submit.setEnabled(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(id_no) && !TextUtils.isEmpty(car_no) && !TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(code));

    }

    public void setCardScanNumber(String number) {
        no.setText(number);
    }

    public void setCardScanName(String bankName) {
        bank.setText(bankName);
    }

    @Override
    @OnClick({R.id.car_no_clear, R.id.id_no_clear, R.id.clear, R.id.bank, R.id.scan_card, R.id.get_sms, R.id.submit})
    protected void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.clear) {
            phone.setText("");
        } else if (id == R.id.car_no_clear) {
            no.setText("");
        } else if (id == R.id.id_no_clear) {
            idNo.setText("");
        } else if (id == R.id.bank) {
            presenter.pickBank(bank);
        } else if (id == R.id.scan_card) {
            presenter.scanCard();
        } else if (id == R.id.get_sms) {
            String name = people.getText().toString().trim();
            String id_no = idNo.getText().toString().trim();
            String car_no = no.getBankCardText();
            String mobile = phone.getText().toString().trim();
            presenter.getCardType(name, id_no, car_no, mobile, getSms);
        } else if (id == R.id.submit) {
            String name = people.getText().toString().trim();
            String id_no = idNo.getText().toString().trim();
            String mobile = phone.getText().toString().trim();
            String code = smsCode.getText().toString().trim();
            presenter.addCard(name, id_no, mobile, code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }
}
