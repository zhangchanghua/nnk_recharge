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
import com.nnk.rechargeplatform.login.model.LoginM;
import com.nnk.rechargeplatform.login.view.LoginActivity;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.Utils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.TimerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginPresenter {
    public LoginActivity activity;

    public LoginPresenter(LoginActivity context) {
        activity = context;
    }


    //密码登录
    public void loginWithPass(final String number, String pass) {
        if (TextUtils.isEmpty(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_null);
            return;
        } else if (!Utils.isPhoneNumber(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_invalid);
            return;
        } else if (TextUtils.isEmpty(pass)) {
            ViewUtils.showToast(activity, R.string.tip_pass_null);
            return;
        }
        ViewUtils.hideKeyboard(activity);
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_LOGIN);
        String sessionId = SharedPreUtils.get(activity, SharedPreUtils.KEY_SESSION_ID);
        String suff = number + "|0|" + pass + "|" + sessionId;
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
                if (Constants.CODE_SUCCESS.equals(errorCode)) {//登录成功
                    ViewUtils.showToast(activity, R.string.tip_login_success);
                    App.getInstance().goHome(activity);
                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(activity, msg);
                    }
                    return;
                }
                //保存登录数据
                SharedPreUtils.set(activity, SharedPreUtils.KEY_PHONE, number);
                if (dataDeArray.length > 2) {
                    String autoLoginKey = dataDeArray[2];
                    if (!TextUtils.isEmpty(autoLoginKey)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_AUTO_LOGIN, autoLoginKey);
                    }
                }
                if (dataDeArray.length > 3) {
                    String isAuth = dataDeArray[3];
                    if (!TextUtils.isEmpty(isAuth)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_IS_AUTH, isAuth);
                    }
                }
                if (dataDeArray.length > 4) {
                    String uname = dataDeArray[4];
                    if (!TextUtils.isEmpty(uname)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_UNAME, uname);
                    }
                }
                if (dataDeArray.length > 5) {
                    String id_cardNo = dataDeArray[5];
                    if (!TextUtils.isEmpty(id_cardNo)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_ID_CARD_NO, id_cardNo);
                    }
                }
                if (dataDeArray.length > 6) {
                    String payAccount = dataDeArray[6];
                    if (!TextUtils.isEmpty(payAccount)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_PAY_ACCOUNT, payAccount);
                    }
                }
                if (dataDeArray.length > 7) {
                    String isSetPaypwd = dataDeArray[7];
                    if (!TextUtils.isEmpty(isSetPaypwd)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_IS_SET_PAY_PASS, isSetPaypwd);
                    }
                }
                if (dataDeArray.length > 8) {
                    String lastLoginTime = dataDeArray[8];
                }
                if (dataDeArray.length > 10) {
                    String userCode = dataDeArray[10];
                    if (!TextUtils.isEmpty(userCode)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_USER_CODE, userCode);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ViewUtils.showToast(activity, R.string.tip_login_failed);
            }
        });
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
        String suff = "LOGIN|" + number + "|";
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

    //验证码登录
    public void loginWithSMS(final String number, String sms) {
        if (TextUtils.isEmpty(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_null);
            return;
        } else if (!Utils.isPhoneNumber(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_invalid);
            return;
        } else if (TextUtils.isEmpty(sms)) {
            ViewUtils.showToast(activity, R.string.tip_sms_null);
            return;
        }
        ViewUtils.hideKeyboard(activity);

        String pre = ApiUtils.getPrex(activity, "1021", Constants.CMD_LOGIN_SMS);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mob", number);
            jsonObject.put("code", sms);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                LoginM loginModel = ApiUtils.parseRespObject(dataDe, LoginM.class);
                if (Constants.CODE_SUCCESS.equals(loginModel.resultCode)) {//登录成功
                    ViewUtils.showToast(activity, R.string.tip_login_success);
                    //保存登录数据
                    SharedPreUtils.set(activity, SharedPreUtils.KEY_PHONE, number);
                    if (!TextUtils.isEmpty(loginModel.autoLoginKey)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_AUTO_LOGIN, loginModel.autoLoginKey);
                    }
                    if (!TextUtils.isEmpty(loginModel.isAuth)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_IS_AUTH, loginModel.isAuth);
                    }
                    if (!TextUtils.isEmpty(loginModel.nickName)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_UNAME, loginModel.nickName);
                    }
                    if (!TextUtils.isEmpty(loginModel.idCardNo)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_ID_CARD_NO, loginModel.idCardNo);
                    }
                    if (!TextUtils.isEmpty(loginModel.payAccount)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_PAY_ACCOUNT, loginModel.payAccount);
                    }
                    if (!TextUtils.isEmpty(loginModel.isSetPaypwd)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_IS_SET_PAY_PASS, loginModel.isSetPaypwd);
                    }
                    if (!TextUtils.isEmpty(loginModel.userCode)) {
                        SharedPreUtils.set(activity, SharedPreUtils.KEY_USER_CODE, loginModel.userCode);
                    }
                    App.getInstance().goHome(activity);
                } else {
                    ViewUtils.showToast(activity, loginModel.resultInfo);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ViewUtils.showToast(activity, R.string.tip_login_failed);
            }
        });
    }
}
