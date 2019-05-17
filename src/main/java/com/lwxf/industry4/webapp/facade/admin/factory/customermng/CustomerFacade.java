package com.lwxf.industry4.webapp.facade.admin.factory.customermng;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/1/15 10:10
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface CustomerFacade extends BaseFacade {

    RequestResult findByClient(String companyId,String condition,Integer pageSize,Integer pageNum);

    RequestResult addCustomer(String companyId, MapContext mapContext, String uid);

	RequestResult updateClient(String cid, MapContext mapContext);
}
