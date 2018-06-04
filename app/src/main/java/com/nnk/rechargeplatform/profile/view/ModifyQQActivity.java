package com.nnk.rechargeplatform.profile.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.profile.presenter.ModifyQQPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifyQQActivity extends BaseActivity {

    @BindView(R.id.qq)
    EditText qqEdit;
    ModifyQQPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_qq;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setText(R.string.profile_qq);
        presenter = new ModifyQQPresenter(this);
    }

    @OnClick({R.id.submit})
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.submit) {
            String qq = qqEdit.getText().toString().toString();
            presenter.submit(qq);
        }
    }


}
