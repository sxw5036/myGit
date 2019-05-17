package com.lwxf.industry4.webapp.facade.app.dealer.contentmng;

import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/27 0027 13:02
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface ContentsFacade extends BaseFacade {
	RequestResult findContentsList(String typeIdOrCode, HttpServletRequest request);

	RequestResult findContentMessage(String contentsId);
}
