package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseFragment;
import com.nnk.rechargeplatform.profile.presenter.AuthPersonPresenter;
import com.nnk.rechargeplatform.utils.SharedPreUtils;

import butterknife.BindView;
import butterknife.OnClick;

@Deprecated
public class AuthPersonFragment extends BaseFragment {
    AuthPersonPresenter presenter;
    @BindView(R.id.add_id_front_cover)
    LinearLayout addIdFrontCover;
    @BindView(R.id.add_id_front_img)
    ImageView addIdFrontImg;
    @BindView(R.id.add_id_front_wrap)
    FrameLayout addIdFrontWrap;
    @BindView(R.id.add_id_back_cover)
    LinearLayout addIdBackCover;
    @BindView(R.id.add_id_back_img)
    ImageView addIdBackImg;
    @BindView(R.id.add_id_back_wrap)
    FrameLayout addIdBackWrap;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_auth_person;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new AuthPersonPresenter(this);
        presenter.getAuthInfo(SharedPreUtils.get(getContext(), SharedPreUtils.KEY_USER_CODE));
    }

    @Override
    @OnClick({R.id.add_id_front_wrap, R.id.add_id_back_wrap, R.id.submit, R.id.agreement})
    protected void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.add_id_front_wrap) {
            presenter.setCurrentFocusView(v);
            presenter.selectImage(addIdFrontImg, addIdFrontCover, AuthPersonPresenter.IMAGE_TYPE_ID_0);
        } else if (id == R.id.add_id_back_wrap) {
            presenter.setCurrentFocusView(v);
            presenter.selectImage(addIdBackImg, addIdBackCover, AuthPersonPresenter.IMAGE_TYPE_ID_1);
        } else if (id == R.id.submit) {
            presenter.submit();
        } else if (id == R.id.agreement) {

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            presenter.getAuthInfo(SharedPreUtils.get(getContext(), SharedPreUtils.KEY_USER_CODE));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
