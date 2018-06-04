package com.nnk.rechargeplatform.api;

import com.nnk.rechargeplatform.Constants;

public class CommRespData {
    public String[] orderInfoArray;
    public String NoOrder;
    public String Orderinfo;
    public String Sign;
    public String errorCode= Constants.CODE_SUCCESS;

    public CommRespData() {
        orderInfoArray = new String[]{};
    }

}
