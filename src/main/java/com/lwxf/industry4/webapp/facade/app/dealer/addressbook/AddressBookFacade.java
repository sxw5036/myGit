package com.lwxf.industry4.webapp.facade.app.dealer.addressbook;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/7 13:55
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface AddressBookFacade extends BaseFacade {

    RequestResult findTelBookList(String companyId,Integer status,
                                  Integer type,Integer identity,
                                  Pagination pagination,String uid,String condition);

    RequestResult addTelBook(String companyId, MapContext mapContext,HttpServletRequest request);
}
