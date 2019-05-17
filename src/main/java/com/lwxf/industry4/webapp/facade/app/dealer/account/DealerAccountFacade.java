package com.lwxf.industry4.webapp.facade.app.dealer.account;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/24 18:40
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface DealerAccountFacade extends BaseFacade {

    RequestResult findDealerAccount(String companyId,String uid);

    RequestResult addBank(MapContext params);

    RequestResult findDealerAccountMime(String companyId,String time,String uid);

    RequestResult findMimeAccountDetails(String companyId,Integer status,String time,Integer pageSize,Integer pageNum,String uid);

    RequestResult findMimeAccountInform(String companyId,String orderId,String paymentId,String uid);

    RequestResult reminders(MapContext params);
}
