package com.nnk.rechargeplatform.api;

import com.nnk.rechargeplatform.Constants;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CommService {
    @POST(Constants.API_INDEX)
    @FormUrlEncoded
    Observable<String> get(@FieldMap Map<String, String> params);
}
