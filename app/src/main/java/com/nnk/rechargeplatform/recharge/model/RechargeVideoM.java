package com.nnk.rechargeplatform.recharge.model;

import java.io.Serializable;

public class RechargeVideoM implements Serializable {


    public String name;
    public String price;
    public boolean isSelect = false;

    public RechargeVideoM(String name, String price) {
        this.name = name;
        this.price = price;
    }
}
