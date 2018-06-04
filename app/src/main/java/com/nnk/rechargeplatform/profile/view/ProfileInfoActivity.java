package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.utils.SharedPreUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileInfoActivity extends BaseActivity {
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.qq)
    TextView qq;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_info;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_info);
    }

    @OnClick({R.id.modify_phone, R.id.modify_qq, R.id.modify_pass})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.modify_phone) {
            startActivity(new Intent(this, ComfirmPhoneActivity.class));
        } else if (id == R.id.modify_pass) {
            startActivity(new Intent(this, ModifyPassActivity.class));
        } else if (id == R.id.modify_qq) {
            startActivity(new Intent(this, ModifyQQActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        phone.setText(SharedPreUtils.get(this, SharedPreUtils.KEY_PHONE));
        qq.setText(SharedPreUtils.get(this, SharedPreUtils.KEY_QQ));
    }
}
