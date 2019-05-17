package com.lwxf.industry4.webapp.facade.app.dealer.company;

import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/10 0010 15:31
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */

public interface ShareMemberFacade extends BaseFacade {


	

	RequestResult addShareMember(String companyId, MapContext mapContext, HttpServletRequest request);

	RequestResult updateShareMember(String companyId,String userId, MapContext mapContext);

	RequestResult deleteShareMember(String companyId,String userId);

	RequestResult findShareMemberList(Integer pageNum, Integer pageSize, String companyId,Integer identity,Integer status);
}
