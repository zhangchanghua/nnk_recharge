package com.nnk.rechargeplatform.profile.presenter;

import android.text.TextUtils;

import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.profile.model.ModifyPassM;
import com.nnk.rechargeplatform.profile.view.ModifyPhoneActivity;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.Utils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.TimerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ModifyPhonePresenter {
    ModifyPhoneActivity activity;

    public ModifyPhonePresenter(ModifyPhoneActivity activity) {
        this.activity = activity;
    }

    //获取验证码
    public void getSms(String number, final TimerView timerView, boolean bind) {
        if (TextUtils.isEmpty(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_null);
            return;
        } else if (!Utils.isPhoneNumber(number)) {
            ViewUtils.showToast(activity, R.string.tip_phone_invalid);
            return;
        }
        ViewUtils.hideKeyboard(activity);
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_CHECK_MOB);
        String oper = bind ? "BINDMOBLOGIN" : "UNBINDMOBLOGIN";
        String suff = oper + "|" + number + "|";
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
        String suff = "UNBINDMOBLOGIN|" + phone + "|" + code;
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

    public void submit(String originPhone, final String newPhone, String code) {
        String pre = ApiUtils.getPrex(activity, "1021", Constants.CMD_RE_BIND);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldMob", originPhone);
            jsonObject.put("newMob", newPhone);
            jsonObject.put("verifyCode", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                ModifyPassM modifyPassModel = ApiUtils.parseRespObject(dataDe, ModifyPassM.class);
                if (Constants.CODE_SUCCESS.equals(modifyPassModel.resultCode)) {//登录成功
                    SharedPreUtils.set(activity, SharedPreUtils.KEY_PHONE, newPhone);
                    SharedPreUtils.set(activity, SharedPreUtils.KEY_USER_CODE, modifyPassModel.userCode);
                    ViewUtils.showToast(activity, R.string.profile_modify_phone_success);
                    activity.finish();
                } else {
                    ViewUtils.showToast(activity, modifyPassModel.resultInfo);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ViewUtils.showToast(activity, R.string.profile_modify_phone_failed);
            }
        });
    }

}
