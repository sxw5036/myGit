package com.lwxf.industry4.webapp.facade.app.dealer.account;

import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/1/7 16:28
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface DealerAccountLogFacade extends BaseFacade {

    //日志明细
    RequestResult findDetailsLog(MapContext mapContext,Integer pageSize,Integer pageNum,String uid);

    //查询明细详情
    RequestResult findPaymentById(String resourceId,String logId,Integer type,String uid,String companyId);

    RequestResult findByCompanyId(PaginatedFilter filter,String uid,String companyId);

}
