package com.nnk.rechargeplatform.profile.presenter;

import android.text.TextUtils;

import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.profile.view.ModifyPayPassActivity;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.Utils;
import com.nnk.rechargeplatform.utils.ViewUtils;

import java.util.Map;

public class ModifyPayPassPresenter {
    private ModifyPayPassActivity activity;

    public ModifyPayPassPresenter(ModifyPayPassActivity activity) {
        this.activity = activity;
    }

    public void submit(String origin, String pass, String confirm) {
        if (TextUtils.isEmpty(origin)) {
            ViewUtils.showToast(activity, R.string.tip_origin_pass_null);
            return;
        } else if (Utils.isPhoneNumber(pass)) {
            ViewUtils.showToast(activity, R.string.input_new_pass);
            return;
        } else if (Utils.isPhoneNumber(confirm)) {
            ViewUtils.showToast(activity, R.string.input_confirm_pass);
            return;
        } else if (!pass.equals(confirm)) {
            ViewUtils.showToast(activity, R.string.pass_not_same);
            return;
        }
        ViewUtils.hideKeyboard(activity);
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_CHANGE_PAY_PASS);
        String userCode = SharedPreUtils.get(activity, SharedPreUtils.KEY_USER_CODE);
        String payAccount = SharedPreUtils.get(activity, SharedPreUtils.KEY_PAY_ACCOUNT);
        String suff = payAccount + "|" + userCode + "|" + origin + "|" + pass;
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                String[] dataDeArray = dataDe.split("\\|");
                String errorCode = "-1";
                if (dataDeArray.length > 0) {
                    errorCode = dataDeArray[0];
                }
                if (Constants.CODE_SUCCESS.equals(errorCode)) {//修改成功
                    ViewUtils.showToast(activity, R.string.tip_modify_pass_success);
                    activity.finish();
                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(activity, msg);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ViewUtils.showToast(activity, R.string.tip_modify_pass_failed);
            }
        });
    }
}
