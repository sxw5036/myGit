package com.lwxf.industry4.webapp.facade.app.factory.factorystatement;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/5/16 14:58
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface IndexStatementFacade extends BaseFacade {
    RequestResult statementHomePage(Integer type,String bTime,String eTime);
}
