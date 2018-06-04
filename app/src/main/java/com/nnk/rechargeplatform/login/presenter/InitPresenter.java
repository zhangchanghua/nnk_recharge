package com.nnk.rechargeplatform.login.presenter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.nnk.rechargeplatform.App;
import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.api.Api;
import com.nnk.rechargeplatform.api.ApiCallback;
import com.nnk.rechargeplatform.api.ApiService;
import com.nnk.rechargeplatform.api.ApiUtils;
import com.nnk.rechargeplatform.api.CommService;
import com.nnk.rechargeplatform.base.BaseActivity;
import com.nnk.rechargeplatform.utils.JniHelper;
import com.nnk.rechargeplatform.utils.Logg;
import com.nnk.rechargeplatform.utils.SharedPreUtils;
import com.nnk.rechargeplatform.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class InitPresenter {

    private BaseActivity activity;
    private int retryTimes;
    private CallBack callBack;

    public InitPresenter(BaseActivity activity, CallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
        retryTimes = 0;
    }

    public void init() {
        final long beginTime = System.currentTimeMillis();
        String autoLoginKey = SharedPreUtils.get(activity, SharedPreUtils.KEY_AUTO_LOGIN);
        if (!TextUtils.isEmpty(autoLoginKey) && !"0".equals(autoLoginKey)) {
            goToNext(beginTime);
            return;
        }
        ApiUtils.init(activity.getApplicationContext());
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String versionName = "1.0";
        try {
            versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String prex = ApiUtils.getPrex(activity, "0", Constants.CMD_INIT);
        String hardwareId = SharedPreUtils.get(activity, SharedPreUtils.KEY_HARDWARE_ID);
        Logg.e("hardwareId: " + hardwareId);
        String suff = "V1.4.145.014." + time + "|V" + versionName + "|" + hardwareId + "|blowfish,DH|" + JniHelper.getInstance().dhGroupID + "|" + JniHelper.getInstance().pPublicKey;
        CommService service = ApiService.getInstance().createService(CommService.class);
        Map params = ApiUtils.getCommparams(activity, prex, suff);
        Api.request(service.get(params),
                new ApiCallback<String>(activity) {
                    @Override
                    public void onResponse(String[] orderInfoArray, String decriptStr) {
                        String initACK = "";//ack为1005需要上传手机硬件信息
                        if (orderInfoArray.length > 2) {
                            initACK = orderInfoArray[2];
                            Logg.i("initACK:" + initACK);
                        }
                        if (orderInfoArray.length > 3) {
                            String serverEncry = orderInfoArray[3];
                            String serverDecry = ApiUtils.decryptData(activity, serverEncry);
                            //Logg.i("serverDecry: " + serverDecry);
                            String[] serverDecryArray = serverDecry.split("\\|");
                            if (serverDecryArray.length > 0) {
                                String sessid = serverDecryArray[0];
                                Logg.i("sessid: " + sessid);
                                SharedPreUtils.set(activity, SharedPreUtils.KEY_SESSION_ID, sessid);
                            }
                            if (serverDecryArray.length > 1) {
                                String encT = serverDecryArray[1];
                                Logg.i("encT: " + encT);
                            }
                            if (serverDecryArray.length > 2) {
                                String dhSrvPub = serverDecryArray[2];
                                //Logg.i("dhSrvPub: " + dhSrvPub);
                                String t_key = JniHelper.getInstance().PiaGenDHZZ(JniHelper.getInstance().pPrivateKey, JniHelper.getInstance().dhGroupID, dhSrvPub);
                                SharedPreUtils.set(activity, SharedPreUtils.KEY_T_KEY, t_key);
                                Logg.i("t_key: " + SharedPreUtils.get(activity, SharedPreUtils.KEY_T_KEY));
                            } else {
                                error();
                                return;
                            }
                            if (callBack != null) {
                                callBack.onInited(initACK, beginTime);
                            }
                        } else {
                            error();
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (retryTimes > 0) {//重试一次
                            error();
                        } else {
                            retryTimes++;
                            init();
                        }

                    }
                });
    }

    public interface CallBack {
        public void onInited(String initACK, long beginTime);
    }

    public void goToNext(long beginTime) {
        long endTime = System.currentTimeMillis();
        long spendTime = endTime - beginTime;
        long delayTime = 0;
        if (spendTime < 2000) {//保证界面显示2s
            delayTime = 2000 - spendTime;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String autoLoginKey = SharedPreUtils.get(activity, SharedPreUtils.KEY_AUTO_LOGIN);
                Logg.e("autoLoginKey: " + autoLoginKey);
                if (!TextUtils.isEmpty(autoLoginKey) && !"0".equals(autoLoginKey)) {
                    App.getInstance().goHome(activity);
                } else {
                    App.getInstance().goLogin(activity, false);
                }
                activity.finish();
            }
        }, delayTime);
    }

    public void uploadMobileInfo(final long beginTime) {
        String pre = ApiUtils.getPrex(activity, "0", Constants.CMD_SETENV);
        String suff = generateMobileInfo();
        Map params = ApiUtils.getCommparams(activity, pre, suff);
        CommService service = ApiService.getInstance().createService(CommService.class);
        Api.request(service.get(params), new ApiCallback<String>(activity) {
            @Override
            public void onResponse(String[] orderInfoArray, String decriptStr) {
                Logg.e("上传硬件信息hardwareIdDe: " + decriptStr);
                SharedPreUtils.set(activity, SharedPreUtils.KEY_HARDWARE_ID, decriptStr);
                goToNext(beginTime);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                error();
            }
        });

    }

    //拼接手机信息
    private String generateMobileInfo() {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        String s11 = "android";//手机生产厂商
        String s12 = Build.BOARD;//手机品牌
        String s13 = Build.MODEL;//手机型号
        String s35 = "";//IMEI;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            try {
                s35 = tm.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(s35)) {
            s35 = UUID.randomUUID().toString().replace("-", "");
        }
        String s15 = "ARM";//指令集
        String s16 = "ARM64";//cpu 芯片型号
        String s17 = "2";//cpu 芯片型号
        String s18 = getRomSpace();//rom大小
        String s19 = activity.getResources().getDisplayMetrics().widthPixels + "*" + activity.getResources().getDisplayMetrics().heightPixels;//尺寸
        String s110 = "0";//
        String s112 = "1";//
        String s114 = "4G";//网络
        String s115 = UUID.randomUUID().toString().replace("-", "");//
        String s116 = Utils.getMac();//mac
        String s117 = "0000";//
        String s118 = "00000";//
        String s111 = "0";//
        String s21 = Build.MANUFACTURER;//宿主OS厂家
        String s23 = Build.VERSION.RELEASE;//宿主OS版本
        String s42 = getKernelVersion();//kernel
        return ("11,12,13,35,15,16,17,18,111,19,110,112,114,115,116,118,117,21,23,42|"
                + s11 + "," + s12 + "," + s13 + "," + s35 + "," + s15 + "," + s16 + "," + s17 + "," + s18 + "," + s111 + "," + s19 + ","
                + s110 + "," + s112 + "," + s114 + "," + s115 + "," + s116 + "," + s118 + "," + s117 + "," + s21 + "," + s23 + "," + s42).replace(" ", "_");
    }

    //获取Rom大小
    private String getRomSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockCount = stat.getBlockCount();
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        String totalSize = Formatter.formatFileSize(activity, blockCount * blockSize);
        String availableSize = Formatter.formatFileSize(activity, blockCount * availableBlocks);
        return totalSize;
    }

    //获取内核版本
    private String getKernelVersion() {
        String kernelVersion = "";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/proc/version");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return kernelVersion;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
        String info = "";
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                info += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (info != "") {
                final String keyword = "version ";
                int index = info.indexOf(keyword);
                line = info.substring(index + keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }

    public void error() {
        activity.alertDialog(R.string.init_failed, new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
    }

}
