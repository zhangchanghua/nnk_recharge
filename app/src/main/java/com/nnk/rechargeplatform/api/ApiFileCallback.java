package com.nnk.rechargeplatform.api;

import android.content.Context;

public class ApiFileCallback extends ApiCallback {
    public ApiFileCallback(Context context) {
        super(context);
    }

    @Override
    public void onNext(Object result) {
        onResponse(null, (String) result);
    }

    @Override
    public void onResponse(String[] orderInfoArray, String dataDe) {

    }
}
