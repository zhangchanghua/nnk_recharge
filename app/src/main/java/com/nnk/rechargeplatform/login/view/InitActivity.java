package com.nnk.rechargeplatform.login.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.login.presenter.InitPresenter;

import butterknife.BindView;

public class InitActivity extends BaseActivity {
    @BindView(R.id.img)
    ImageView img;
    InitPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_init;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        presenter = new InitPresenter(this, new InitPresenter.CallBack() {
            @Override
            public void onInited(String initACK, long beginTime) {
                if ("1005".equals(initACK)) {//需要上传手机硬件信息
                    presenter.uploadMobileInfo(beginTime);
                } else {
                    presenter.goToNext(beginTime);
                }
            }
        });
        try {
            presenter.init();
        } catch (Exception e) {
            e.printStackTrace();
            presenter.error();
        }
    }

}
