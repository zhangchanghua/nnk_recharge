package com.nnk.rechargeplatform.recharge.model;

import java.io.Serializable;

public class RechargeVideoCateM implements Serializable {
    public int iconResId;
    public String name;

    public RechargeVideoCateM(int iconResId, String name) {
        this.iconResId = iconResId;
        this.name = name;
    }
}
