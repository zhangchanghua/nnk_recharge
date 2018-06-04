package com.nnk.rechargeplatform.api;

import com.nnk.rechargeplatform.Constants;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileService {
    @POST(Constants.API_FILE)
    @Multipart
    Observable<String> uploadFile(@Part List<MultipartBody.Part> partList);
}
