package com.nnk.rechargeplatform.main.model;

import java.io.Serializable;

public class RechargeCategory implements Serializable {
    public int id;
    public int iconRes;
    public String name;

    public RechargeCategory(int id, int iconRes, String name) {
        this.id = id;
        this.iconRes = iconRes;
        this.name = name;
    }
}
