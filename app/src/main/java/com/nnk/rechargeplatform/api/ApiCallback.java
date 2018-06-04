package com.nnk.rechargeplatform.api;


import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.nnk.rechargeplatform.App;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseResp;
import com.nnk.rechargeplatform.utils.Logg;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.LoadingProgressDialog;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class ApiCallback<T> implements Observer<T> {
    private LoadingProgressDialog loading;
    private WeakReference<Context> wr;

    public ApiCallback(Context context) {
        wr = new WeakReference<>(context);
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (wr.get() != null) {
            loading = new LoadingProgressDialog(wr.get());
            loading.show();
        }

    }

    @Override
    public void onNext(T result) {
        Logg.d("结果: " + result);
        if (wr.get() != null && result instanceof String && !TextUtils.isEmpty((String) result)) {
            CommRespData commRespData = ApiUtils.getCommDRespata(wr.get(), (String) result);
            if (Constants.CODE_SUCCESS.equals(commRespData.errorCode)) {
                String[] orderInfoArray = commRespData.orderInfoArray;
                String dataDe = "";
                if (orderInfoArray.length > 2) {
                    String dataEn = orderInfoArray[2];
                    dataDe = ApiUtils.decryptData(wr.get(), dataEn);
                }
                if (!TextUtils.isEmpty(dataDe)) {
                    try {
                        String base64DecodeStr = new String(Base64.decode(dataDe.getBytes(), Base64.DEFAULT), "gbk");
                        BaseResp baseResp = new Gson().fromJson(base64DecodeStr, BaseResp.class);
                        Logg.e("服务器解密json: " + base64DecodeStr);
                        if (!Constants.CODE_SUCCESS.equals(baseResp.resultCode)&&!Constants.CODE_SUCCESS1.equals(baseResp.resultCode)) {
                            handError(baseResp.resultCode, baseResp.resultInfo);
                        } else {
                            onResponse(orderInfoArray, dataDe);
                        }
                    } catch (Exception e) {
                        onResponse(orderInfoArray, dataDe);
                    }
                } else {
                    Logg.e("解密异常...");
                }
            } else {//错误处理
                Logg.e("服务器返回错误=> errorCode: " + commRespData.errorCode);
                handError(commRespData.errorCode, "");
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        Logg.e("请求失败");
        ViewUtils.showToast(wr.get(), R.string.request_failed);
        e.printStackTrace();
        if (loading != null) {
            loading.dismiss();
        }
    }

    @Override
    public void onComplete() {
        if (loading != null) {
            loading.dismiss();
        }
    }

    public void handError(String errorCode, String errorMsg) {
        if ("7".equals(errorCode) || "10211".equals(errorCode)) {//sesstion失效，重新初始化，登录
            ViewUtils.showToastLong(wr.get().getApplicationContext(), R.string.session_invalid);
            SharedPreUtils.logout(wr.get().getApplicationContext());
            App.getInstance().goLogin(wr.get(), true);
        } else {
            if (TextUtils.isEmpty(errorMsg)) {
                ViewUtils.showToast(wr.get(), R.string.request_failed);
            } else {
                ViewUtils.showToast(wr.get(), errorMsg);
            }
        }
    }

    public abstract void onResponse(String[] orderInfoArray, String dataDe);
}
