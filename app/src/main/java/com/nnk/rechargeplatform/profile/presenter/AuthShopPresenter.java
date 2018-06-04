package com.nnk.rechargeplatform.profile.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.contrarywind.interfaces.IPickerViewData;
import com.google.gson.Gson;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiFileCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.api.FileService;
import com.nnk.rechargeplatform.base.BaseResp;
import com.nnk.rechargeplatform.profile.model.AuthShopM;
import com.nnk.rechargeplatform.profile.view.AuthShopFragment;
import com.nnk.rechargeplatform.profile.view.AuthSuccessActivity;
import com.nnk.rechargeplatform.utils.Logg;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.Utils;
import com.nnk.rechargeplatform.utils.ViewUtils;
import com.nnk.rechargeplatform.widget.PickImageDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AuthShopPresenter {
    private AuthShopFragment fragment;
    public static final int IMAGE_TYPE_ID_0 = 0;//身份正面
    public static final int IMAGE_TYPE_ID_1 = 1;//身份反面
    public static final int IMAGE_TYPE_SHOP_0 = 2;//营业执照
    public static final int IMAGE_TYPE_SHOP_1 = 3;//门头照
    public static final int REQUEST_STORAGE_PERMISSION = 1;//
    private ImagePicker imagePicker;
    private View currentFocusView;//用于授权获取图片之后跟上操作

    public AuthShopPresenter(AuthShopFragment fragment) {
        this.fragment = fragment;
    }

    public void init() {
        imagePicker = new ImagePicker();
        imagePicker.setCropImage(true);
    }

    //获取实名认证信息
    public void getAuthInfo(String userCode) {
        String pre = ApiUtils.getPrex(fragment.getContext(), "0", Constants.CMD_QUERY_AUTH_INFO);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserCode", userCode);
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
                AuthShopM resp = ApiUtils.parseRespObject(dataDe, AuthShopM.class);
                if (Constants.CODE_SUCCESS.equals(resp.resultCode)) {//发送成功
                    if (resp.userinfo != null) {
                        fragment.updateInfo(resp.userinfo);
                    }
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

    public void setCurrentFocusView(View v) {
        this.currentFocusView = v;
    }

    //店铺类型
    public void selectShopType(final TextView shopType) {
        ViewUtils.hideKeyboard(fragment.getActivity());
        final List<String> dataList = new ArrayList<>();
        dataList.add("通讯店");
        dataList.add("便利店");
        dataList.add("报刊亭");
        dataList.add("手机");
        dataList.add("卖场");
        dataList.add("其他");
        ViewUtils.showSingleItemPicker(fragment.getContext(), dataList, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                shopType.setText(dataList.get(options1));
            }
        });
    }

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    ArrayList<JsonBean> jsonBean;

    class JsonBean implements IPickerViewData {

        public String name;
        public List<CityBean> city;

        // 实现 IPickerViewData 接口，
        // 这个用来显示在PickerView上面的字符串，
        // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
        @Override
        public String getPickerViewText() {
            return this.name;
        }

        class CityBean {
            public String name;
            public List<String> area;
        }
    }

    private ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    //地区
    public void selectArea(final TextView area) {
        ViewUtils.hideKeyboard(fragment.getActivity());
        if (jsonBean == null) {
            String JsonData = Utils.getJsonFromAssets(fragment.getContext(), "province.json");
            jsonBean = parseData(JsonData);
        }
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).city.size(); c++) {
                String CityName = jsonBean.get(i).city.get(c).name;
                CityList.add(CityName);
                ArrayList<String> City_AreaList = new ArrayList<>();

                if (jsonBean.get(i).city.get(c).area == null
                        || jsonBean.get(i).city.get(c).area.size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).city.get(c).area);
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            options2Items.add(CityList);
            options3Items.add(Province_AreaList);
        }
        OptionsPickerView pvOptions = new OptionsPickerBuilder(fragment.getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() + "-" +
                        options2Items.get(options1).get(options2) + "-" +
                        options3Items.get(options1).get(options2).get(options3);
                area.setText(tx);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(fragment.getResources().getColor(R.color.colorAccent)) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }


    //选择图片
    public void selectImage(final ImageView target, final View cover, int type) {
        ViewUtils.hideKeyboard(fragment.getActivity());
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
                        uploadFile(imageUri.getPath());
                    }

                });
            }
        });
        dialog.show();
    }

    private void uploadFile(String filePath) {
        File file = new File(filePath);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)//表单类型
                .addFormDataPart("userCode", SharedPreUtils.get(fragment.getContext(), SharedPreUtils.KEY_USER_CODE))
                .addFormDataPart("cardType", "A");
        RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        builder.addFormDataPart("file", file.getName(), imageBody);//imgfile 后台接收图片流的参数名
        List<MultipartBody.Part> parts = builder.build().parts();
        FileService fileService = ApiService.getInstance().createFileService(FileService.class);
        Api.request(fileService.uploadFile(parts), new ApiFileCallback(fragment.getContext()) {
            @Override
            public void onResponse(String[] orderInfoArray, String dataDe) {
                Logg.e(dataDe);
            }
        });
    }

    //提交
    public void submit(String realName, String idNo, String shopType, String shopName, String address) {
        String pre = ApiUtils.getPrex(fragment.getContext(), "0", Constants.CMD_VERIFY);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userCode", SharedPreUtils.get(fragment.getContext(), SharedPreUtils.KEY_USER_CODE));
            jsonObject.put("mob", SharedPreUtils.get(fragment.getContext(), SharedPreUtils.KEY_PHONE));
            jsonObject.put("realName", realName);
            jsonObject.put("address", address);
            jsonObject.put("idNo", idNo);
            jsonObject.put("shopType", shopType);
            jsonObject.put("shopName", shopName);
            jsonObject.put("qq", SharedPreUtils.get(fragment.getContext(),SharedPreUtils.KEY_QQ));
            jsonObject.put("msn", "");
            jsonObject.put("email", "");
            jsonObject.put("sex", "");
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
                BaseResp resp = ApiUtils.parseRespObject(dataDe, BaseResp.class);
                if (Constants.CODE_SUCCESS.equals(resp.resultCode)) {//发送成功
                    Intent intent = new Intent(fragment.getActivity(), AuthSuccessActivity.class);
                    intent.putExtra("title", fragment.getString(R.string.profile_auth));
                    fragment.startActivity(intent);
                    fragment.getActivity().finish();
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

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
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
