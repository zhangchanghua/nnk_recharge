package com.nnk.rechargeplatform.profile.presenter;

import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.profile.view.UnbindCardActivity;
import com.nnk.rechargeplatform.utils.ViewUtils;

import java.util.Map;

public class UnbindCardPresenter {
    UnbindCardActivity activity;

    public UnbindCardPresenter(UnbindCardActivity activity) {
        this.activity = activity;
    }

    public void unbind(String accountName, String payPass) {
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_UNBIND_ACCOUNT);
        String suff = accountName + "|2|0|" + payPass;
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
                if (Constants.CODE_SUCCESS.equals(errorCode)) {//成功
                    ViewUtils.showToast(activity,R.string.profile_bank_unbind_card_success );
                    activity.finish();
                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(activity, msg);
                    }
                    return;
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
