package com.nnk.rechargeplatform;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.nnk.rechargeplatform.login.view.LoginActivity;
import com.nnk.rechargeplatform.main.view.MainActivity;
import com.nnk.rechargeplatform.utils.JniHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.Iterator;


public class App extends Application {
    private static App instance;
    private ArrayList<Activity> activityArrayList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        LeakCanary.install(this);
        JniHelper.getInstance().init();
        instance = this;

    }


    public static App getInstance() {
        return instance;
    }

    public void addActivity(Activity ac) {
        activityArrayList.add(ac);
    }

    public void removeActivity(Activity ac) {
        if (activityArrayList.contains(ac)) {
            activityArrayList.remove(ac);
        }
    }

    public void exit() {
        for (Activity ac : activityArrayList) {
            ac.finish();
        }
        activityArrayList.clear();
    }

    public void goHome(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Iterator it = activityArrayList.iterator();
        while (it.hasNext()) {
            Activity ac = (Activity) it.next();
            if (!(ac instanceof MainActivity)) {
                ac.finish();
                it.remove();
            }

        }
    }

    public void goLogin(Context context, boolean sessionInvalied) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (sessionInvalied) {
            intent.putExtra(LoginActivity.KEY_SESSION_INVAILD, true);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Iterator it = activityArrayList.iterator();
        while (it.hasNext()) {
            Activity ac = (Activity) it.next();
            if (!(ac instanceof LoginActivity)) {
                ac.finish();
                it.remove();
            }

        }
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //layout.setPrimaryColorsId(R.color.bg, R.color.colorPrimary);
                return new ClassicsHeader(context);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context);
            }
        });
    }
}
