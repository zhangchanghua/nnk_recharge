package com.nnk.rechargeplatform.login.presenter;

import android.text.TextUtils;

import com.nnk.rechargeplatform.App;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.login.view.RegisterActivity;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.Utils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.TimerView;

import java.util.Map;

public class RegisterPresenter {
    public RegisterActivity activity;
    private String en;

    public RegisterPresenter(RegisterActivity context) {
        activity = context;
    }


    //获取验证码
    public void getSms(String number, final TimerView timerView) {
        if (TextUtils.isEmpty(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_null);
            return;
        } else if (!Utils.isPhoneNumber(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_invalid);
            return;
        }
        ViewUtils.hideKeyboard(activity);
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_CHECK_MOB);
        String suff = "REGIST|" + number + "|";
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
                if ("1".equals(errorCode)) {//发送成功
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

    //注册
    public void register(final String number, String sms, String pass) {
        if (TextUtils.isEmpty(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_null);
            return;
        } else if (!Utils.isPhoneNumber(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_invalid);
            return;
        } else if (TextUtils.isEmpty(sms)) {
            ViewUtils.showToast(activity, R.string.tip_sms_null);
            return;
        } else if (TextUtils.isEmpty(pass)) {
            ViewUtils.showToast(activity, R.string.tip_pass_null);
            return;
        } else if (!Utils.isVerifypass(pass)) {
            ViewUtils.showToast(activity, R.string.tip_pass_invalid);
            return;
        }
        ViewUtils.hideKeyboard(activity);
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_REGISTER);
        String suff = number + "|" + sms + "|" + pass + "|" + pass + "|10";
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
                    if (Constants.CODE_SUCCESS.equals(errorCode)) {//注册
                        ViewUtils.showToast(activity, R.string.tip_register_success);
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_PHONE, number);
                        if (dataDeArray.length > 1) {
                            String userCode = dataDeArray[1];
                            SharedPreUtils.set(activity, SharedPreUtils.KEY_USER_CODE, userCode);
                        }
                        if (dataDeArray.length > 2) {
                            String payUserCode = dataDeArray[2];
                            SharedPreUtils.set(activity, SharedPreUtils.KEY_PAY_ACCOUNT, payUserCode);
                        }
                        App.getInstance().goHome(activity);
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
                ViewUtils.showToast(activity, R.string.tip_register_failed);
            }
        });
    }
}
