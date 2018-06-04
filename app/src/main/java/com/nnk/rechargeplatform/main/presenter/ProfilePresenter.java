package com.nnk.rechargeplatform.main.presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nnk.rechargeplatform.App;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.base.BaseResp;
import com.nnk.rechargeplatform.main.view.ProfileFragment;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ProfilePresenter {
    ProfileFragment fragment;

    public ProfilePresenter(ProfileFragment fragment) {
        this.fragment = fragment;
    }


    public void updateInfo(ImageView icon, TextView phone, TextView proxyGrade, TextView money, TextView commission, TextView extract) {
        String iconStr = "";
        String phoneStr = SharedPreUtils.get(fragment.getContext(), SharedPreUtils.KEY_PHONE);
        if (!TextUtils.isEmpty(iconStr)) {
            Glide.with(fragment).load(iconStr).into(icon);
        }
        phone.setText(phoneStr);
        getUserInfo();
    }

    public void getUserInfo() {
        String pre = ApiUtils.getPrex(fragment.getContext(), "1021", Constants.CMD_GET_ACC_DETAIL);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userCode", SharedPreUtils.get(fragment.getContext(), SharedPreUtils.KEY_USER_CODE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(fragment.getContext(), pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(fragment.getContext()) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                String[] dataDeArray = dataDe.split("\\|");
                BaseResp resp = ApiUtils.parseRespObject(dataDe, BaseResp.class);
                if (Constants.CODE_SUCCESS.equals(resp.resultCode)) {//成功

                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(fragment.getContext(), msg);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    public void logout() {
        new AlertDialog.Builder(fragment.getContext())
                .setTitle(R.string.tip)
                .setMessage(R.string.logout_tip)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreUtils.logout(fragment.getActivity().getApplicationContext());
                        App.getInstance().goLogin(fragment.getActivity(), true);
                    }
                }).create().show();
    }
}
