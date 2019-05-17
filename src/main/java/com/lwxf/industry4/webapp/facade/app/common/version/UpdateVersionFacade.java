package com.lwxf.industry4.webapp.facade.app.common.version;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/5/6 0006 10:19
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface UpdateVersionFacade extends BaseFacade {

	RequestResult findVersionNo( Integer sysType, Integer platform);

	RequestResult updateVersion(MapContext mapContext);

	RequestResult addVersion(MapContext mapContext);
}
