package com.nnk.rechargeplatform.profile.model;

import android.text.TextUtils;

import com.nnk.rechargeplatform.base.BaseResp;

public class AuthShopM extends BaseResp {
    public UserInfo userinfo;

    public static class UserInfo {
        public String address;
        public String email;
        public int emailState;
        public String haveLoginPwd;
        public int id;
        public String identityId;
        public String ip;
        public String isAgent;
        public long lastLoginTime;
        public String mob;
        public int mobLogin;
        public int mobState;
        public String msn;
        public String nickName;
        public int originalSite;
        public int originalState;
        public String qq;
        public String realname;
        public long regTime;
        public int regType;
        public String registerFrom;
        public String registerRole;
        public int sex;
        public String telphone;
        public String userCode;
        public int userGrade;
        public String userName;
        public String userPhotoUrl;
        public int userSite;
        public int userState;
        public int userType;

        public String getArea() {
            if (!TextUtils.isEmpty(address) && address.indexOf("-") != -1) {
                return address.substring(0, address.lastIndexOf("-"));
            }
            return "";
        }

        public String getAreaDetail() {
            if (!TextUtils.isEmpty(address) && address.indexOf("-") != -1) {
                try {
                    return address.substring(address.lastIndexOf("-") + 1, address.length());
                } catch (Exception e) {

                }
            }
            return "";
        }
    }
}
