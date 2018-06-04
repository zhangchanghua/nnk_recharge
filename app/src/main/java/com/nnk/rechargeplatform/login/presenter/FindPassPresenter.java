package com.nnk.rechargeplatform.login.presenter;

import android.text.TextUtils;

import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.login.view.FindPassActivity;
import com.nnk.rechargeplatform.utils.Utils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.TimerView;

import java.util.Map;

public class FindPassPresenter {
    private FindPassActivity activity;

    public FindPassPresenter(FindPassActivity activity) {
        this.activity = activity;
    }

    public void getSmsCode(String phone, final TimerView timerView) {
        if (TextUtils.isEmpty(phone)) {
            ViewUtils.showToast(activity, R.string.tip_phone_null);
            return;
        } else if (!Utils.isPhoneNumber(phone)) {
            ViewUtils.showToast(activity, R.string.tip_phone_invalid);
            return;
        }
        ViewUtils.hideKeyboard(activity);
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_CHECK_MOB);
        String suff = "LOSTLOGINPWD|" + phone + "|";
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
                if (Constants.CODE_SUCCESS.equals(errorCode)) {//发送成功
                    ViewUtils.showToast(activity, R.string.sms_sended);
                    timerView.start();
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
                ViewUtils.showToast(activity, R.string.sms_send_failed);
            }
        });
    }

    public void checkCode(String phone, String code) {
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_CHECK_MOB);
        String suff = "LOSTLOGINPWD|" + phone + "|" + code;
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
                if (Constants.CODE_SUCCESS.equals(errorCode)) {//验证成功
                    activity.next();
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
                ViewUtils.showToast(activity, R.string.sms_verify_failed);
            }
        });
    }

    public void resetPass(String phone, String pass, String passConfirm) {
        if (!pass.equals(passConfirm)) {
            ViewUtils.showToast(activity, R.string.pass_not_same);
            return;
        } else if (!Utils.isVerifypass(pass)) {
            ViewUtils.showToast(activity, R.string.tip_pass_invalid);
            return;
        }
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_RESET_PASS);
        String suff = "MOB|" + phone + "|" + pass;
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
                if (Constants.CODE_SUCCESS.equals(errorCode)) {//重置成功
                    ViewUtils.showToast(activity, R.string.reset_pass_success);
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
                ViewUtils.showToast(activity, R.string.reset_pass_failed);
            }
        });
    }
}
