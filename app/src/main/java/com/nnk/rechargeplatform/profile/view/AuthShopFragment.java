package com.nnk.rechargeplatform.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseFragment;
import com.nnk.rechargeplatform.profile.model.AuthShopM;
import com.nnk.rechargeplatform.profile.presenter.AuthShopPresenter;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.TextWatcherAdater;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthShopFragment extends BaseFragment {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.name_wrap)
    LinearLayout nameWrap;
    @BindView(R.id.id_no)
    EditText idNo;
    @BindView(R.id.id_wrap)
    LinearLayout idWrap;
    @BindView(R.id.shop)
    EditText shop;
    @BindView(R.id.shop_wrap)
    LinearLayout shopWrap;
    @BindView(R.id.shop_type)
    TextView shopType;
    @BindView(R.id.shop_type_wrap)
    LinearLayout shopTypeWrap;
    @BindView(R.id.area)
    TextView area;
    @BindView(R.id.area_wrap)
    LinearLayout areaWrap;
    @BindView(R.id.area_detail)
    EditText areaDetail;
    @BindView(R.id.area_detail_wrap)
    LinearLayout areaDetailWrap;
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
    @BindView(R.id.add_shop_front_cover)
    LinearLayout addShopFrontCover;
    @BindView(R.id.add_shop_front_img)
    ImageView addShopFrontImg;
    @BindView(R.id.add_shop_front_wrap)
    FrameLayout addShopFrontWrap;
    @BindView(R.id.add_shop_back_cover)
    LinearLayout addShopBackCover;
    @BindView(R.id.add_shop_back_img)
    ImageView addShopBackImg;
    @BindView(R.id.add_shop_back_wrap)
    FrameLayout addShopBackWrap;
    @BindView(R.id.check_agreement)
    CheckBox checkAgreement;
    @BindView(R.id.agreement)
    TextView agreement;
    @BindView(R.id.submit)
    Button submit;
    private AuthShopPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_auth_shop;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        name.addTextChangedListener(checkEnableAdatper);
        idNo.addTextChangedListener(checkEnableAdatper);
        shop.addTextChangedListener(checkEnableAdatper);
        shopType.addTextChangedListener(checkEnableAdatper);
        area.addTextChangedListener(checkEnableAdatper);
        presenter = new AuthShopPresenter(this);
        presenter.init();
        presenter.getAuthInfo(SharedPreUtils.get(getContext(), SharedPreUtils.KEY_USER_CODE));
    }

    TextWatcherAdater checkEnableAdatper = new TextWatcherAdater() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            checkSubmitEnable();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateInfo(AuthShopM.UserInfo userInfo) {
        name.setText(userInfo.realname);
        idNo.setText(userInfo.identityId);
        area.setText(userInfo.getArea());
        areaDetail.setText(userInfo.getAreaDetail());
    }

    @Override
    @OnClick({R.id.add_id_front_wrap, R.id.add_id_back_wrap, R.id.add_shop_front_wrap, R.id.add_shop_back_wrap,
            R.id.shop_type, R.id.area, R.id.submit, R.id.agreement})
    protected void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.add_id_front_wrap) {
            presenter.setCurrentFocusView(v);
            presenter.selectImage(addIdFrontImg, addIdFrontCover, AuthShopPresenter.IMAGE_TYPE_ID_0);
        } else if (id == R.id.add_id_back_wrap) {
            presenter.setCurrentFocusView(v);
            presenter.selectImage(addIdBackImg, addIdBackCover, AuthShopPresenter.IMAGE_TYPE_ID_1);
        } else if (id == R.id.add_shop_front_wrap) {
            presenter.setCurrentFocusView(v);
            presenter.selectImage(addShopFrontImg, addShopFrontCover, AuthShopPresenter.IMAGE_TYPE_SHOP_0);
        } else if (id == R.id.add_shop_back_wrap) {
            presenter.setCurrentFocusView(v);
            presenter.selectImage(addShopBackImg, addShopBackCover, AuthShopPresenter.IMAGE_TYPE_SHOP_1);
        } else if (id == R.id.shop_type) {//店铺类型
            presenter.selectShopType(shopType);
        } else if (id == R.id.area) {//店铺类型
            presenter.selectArea(area);
        } else if (id == R.id.submit) {
            String realName = name.getText().toString().trim();
            String idS = idNo.getText().toString().trim();
            String shopName = shop.getText().toString().trim();
            String shopTypeS = shopType.getText().toString().trim();
            String address = area.getText().toString() + "-" + areaDetail.getText().toString().trim();
            presenter.submit(realName, idS, shopTypeS, shopName, address);
        } else if (id == R.id.agreement) {

        }

    }

    private void checkSubmitEnable() {
        String realName = name.getText().toString().trim();
        String idS = idNo.getText().toString().trim();
        String shopName = shop.getText().toString().trim();
        String shopTypeS = shopType.getText().toString().trim();
        String address = area.getText().toString() + areaDetail.getText().toString().trim();
        submit.setEnabled(!TextUtils.isEmpty(realName) && !TextUtils.isEmpty(idS)
                && !TextUtils.isEmpty(shopName) && !TextUtils.isEmpty(shopTypeS)
                && !TextUtils.isEmpty(address));
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
