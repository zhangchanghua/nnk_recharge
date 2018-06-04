package com.nnk.rechargeplatform.profile.presenter;

import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.base.BaseResp;
import com.nnk.rechargeplatform.profile.view.ModifyQQActivity;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ModifyQQPresenter {
    ModifyQQActivity activity;

    public ModifyQQPresenter(ModifyQQActivity activity) {
        this.activity = activity;
    }

    //提交
    public void submit(final String qq) {
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_VERIFY);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userCode", SharedPreUtils.get(activity, SharedPreUtils.KEY_USER_CODE));
            jsonObject.put("qq", qq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                String[] dataDeArray = dataDe.split("\\|");
                BaseResp resp = ApiUtils.parseRespObject(dataDe, BaseResp.class);
                if (Constants.CODE_SUCCESS.equals(resp.resultCode)) {//发送成功
                    SharedPreUtils.set(activity, SharedPreUtils.KEY_QQ, qq);
                    ViewUtils.showToast(activity, activity.getString(R.string.request_success));
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
            }
        });

    }
}
