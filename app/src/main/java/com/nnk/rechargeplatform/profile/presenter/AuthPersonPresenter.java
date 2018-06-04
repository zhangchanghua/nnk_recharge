package com.nnk.rechargeplatform.profile.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;

import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.profile.model.AuthPersonM;
import com.nnk.rechargeplatform.profile.view.AuthPersonFragment;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.PickImageDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AuthPersonPresenter {
    AuthPersonFragment fragment;
    private ImagePicker imagePicker;
    public static final int IMAGE_TYPE_ID_0 = 0;//身份正面
    public static final int IMAGE_TYPE_ID_1 = 1;//身份反面
    public static final int REQUEST_STORAGE_PERMISSION = 1;//
    private View currentFocusView;//用于授权获取图片之后跟上操作

    public AuthPersonPresenter(AuthPersonFragment fragment) {
        this.fragment = fragment;
        imagePicker = new ImagePicker();
        imagePicker.setCropImage(true);
    }

    public void setCurrentFocusView(View v) {
        this.currentFocusView = v;
    }

    public void getAuthInfo(String userCode) {
        String pre = ApiUtils.getPrex(fragment.getContext(), "1021", Constants.CMD_QUERY_AUTH_INFO);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userCode", userCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String suff = ApiUtils.getBase64JsonString(jsonObject);
        Map params = ApiUtils.getCommparams(fragment.getContext(), pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(fragment.getContext()) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                String[] dataDeArray = dataDe.split("\\|");
                AuthPersonM resp = ApiUtils.parseRespObject(dataDe, AuthPersonM.class);
                if (Constants.CODE_SUCCESS.equals(resp.resultCode)) {//发送成功

                } else {
                    if (dataDeArray.length > 1) {
                        String msg = dataDeArray[1];
                        ViewUtils.showToast(fragment.getContext(), msg);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    //选择图片
    public void selectImage(final ImageView target, final View cover, int type) {
        if (!verifyStoragePermissions(fragment.getActivity())) {
            fragment.requestPermissions(PERMISSIONS_STORAGE,
                    REQUEST_STORAGE_PERMISSION);
            return;
        }
        PickImageDialog dialog = new PickImageDialog(fragment.getContext(), new PickImageDialog.CallBack() {
            @Override
            public void onTakePhoto() {
                imagePicker.startCamera(fragment, new ImagePickerCallBack() {
                    @Override
                    public void onPickImage(Uri imageUri) {
//                        target.setImageURI(imageUri);
//                        target.setVisibility(View.VISIBLE);
//                        cover.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCropImage(Uri imageUri) {
                        super.onCropImage(imageUri);
                        target.setImageURI(imageUri);
                        target.setVisibility(View.VISIBLE);
                        cover.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onPickBlbum() {
                imagePicker.startGallery(fragment, new ImagePickerCallBack() {
                    @Override
                    public void onPickImage(Uri imageUri) {
//                        target.setImageURI(imageUri);
//                        target.setVisibility(View.VISIBLE);
//                        cover.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCropImage(Uri imageUri) {
                        super.onCropImage(imageUri);
                        target.setImageURI(imageUri);
                        target.setVisibility(View.VISIBLE);
                        cover.setVisibility(View.GONE);
                    }
                });
            }
        });
        dialog.show();
    }

    public void submit() {

    }


    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    class ImagePickerCallBack extends ImagePicker.Callback {

        @Override
        public void onPickImage(Uri imageUri) {

        }


        @Override
        public void cropConfig(CropImage.ActivityBuilder builder) {
            super.cropConfig(builder);
            int width = fragment.getResources().getDisplayMetrics().widthPixels;
            int height = fragment.getResources().getDisplayMetrics().heightPixels;
            builder.setMultiTouchEnabled(false)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setRequestedSize(width, height);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imagePicker.onActivityResult(fragment, requestCode, resultCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        imagePicker.onRequestPermissionsResult(fragment, requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (currentFocusView != null && verifyStoragePermissions(fragment.getActivity())) {
                currentFocusView.performClick();
            }
        }
    }
}
