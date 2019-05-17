package com.lwxf.industry4.webapp.facade.app.dealer.account;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/24 19:17
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface DealerAccountDetailsFacade extends BaseFacade {

    //
    RequestResult addAccountDetails(MapContext params,String companyId,String uid);

    //明细列表
    RequestResult findAccountDetails(String companyId,String accountId,String uid);

    //明细详情
    RequestResult findAccountDetailsById(String detailsId,String uid,String companyId);


}
