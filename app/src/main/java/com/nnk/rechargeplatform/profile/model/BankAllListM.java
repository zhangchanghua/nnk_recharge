package com.nnk.rechargeplatform.profile.model;

import com.nnk.rechargeplatform.base.BaseResp;

import java.util.List;

//所有支持的银行卡列表
public class BankAllListM extends BaseResp {
    public List<BankAllItemM> credit;
    public List<BankAllItemM> debit;
}
