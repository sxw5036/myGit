package com.lwxf.industry4.webapp.facade.app.factory.factoryscheme;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/2 13:15
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactorySchemeFacade extends BaseFacade {

     RequestResult findList(MapContext params, Pagination pagination);

    RequestResult findBySchemeId(String schemeId);
}
