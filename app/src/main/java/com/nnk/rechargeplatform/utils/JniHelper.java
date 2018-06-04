package com.nnk.rechargeplatform.utils;

import java.security.MessageDigest;

public class JniHelper {
    private JniHelper() {

    }

    static {
        System.loadLibrary("nnk-lib");
    }

    private static JniHelper instance;

    public static JniHelper getInstance() {
        if (instance == null) {
            instance = new JniHelper();
        }
        return instance;
    }

    public String pPublicKey = "";//公钥
    public String pPrivateKey = "";//私钥
    public int dhGroupID = 0;//生成秘钥大素数组index

    public void init() {
        initKeys();//调C赋值
    }

    //nnk全部转小写
    public String md5(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(origin.getBytes());
            return toHex(bytes).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    /**
     * 生成公钥,私钥 PiaGenDHKey
     */
    public native void initKeys();

    /**
     * BlowFish 加密 解密 type 0解密 1加密
     */
    public native byte[] BlowFishWithBase64(int type, String key, String originData);

    /**
     * 根据私钥 服务器公钥 生成新的 t_key
     */
    public native String PiaGenDHZZ(String pPrivateKey, int dhGroupID, String pPublicKey);

}
